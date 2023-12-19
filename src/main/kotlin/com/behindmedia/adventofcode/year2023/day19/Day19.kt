package com.behindmedia.adventofcode.year2023.day19

import com.behindmedia.adventofcode.common.productOf
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.splitWithDelimiters
import kotlin.math.max
import kotlin.math.min

private typealias Part = Map<String, Int>
private typealias PartRange = Map<String, IntRange>
private val PartRange.totalSize: Long
    get() = values.productOf { range ->
        max(0, range.last - range.first + 1).toLong()
    }

private data class Condition(val conditional: String, val operator: String, val conditionValue: Int) {
    /**
     * Returns the matching range and remaining range after applying the condition.
     * The remaining range may be applied to subsequent rules in a workflow.
     */
    fun evaluate(partRange: PartRange): Pair<PartRange, PartRange> {
        val range = partRange[conditional] ?: error("No value for $conditional")
        val (matchingRange, remainingRange) = when (operator) {
            "<" ->
                range.start..min(conditionValue - 1, range.endInclusive) to
                        max(range.start, conditionValue)..range.endInclusive

            ">" -> max(conditionValue + 1, range.start)..range.endInclusive to
                    range.start..min(range.endInclusive, conditionValue)

            else -> error("Invalid operator: $operator")
        }
        return partRange + (conditional to matchingRange) to
                partRange + (conditional to remainingRange)
    }
}

private data class Rule(val target: String, val condition: Condition?) {
    companion object {
        operator fun invoke(string: String): Rule {
            return if (string.contains(":")) {
                val (conditionString, target) = string.split(":")
                val (conditional, operator, conditionValue) = conditionString.splitWithDelimiters("<", ">")
                Rule(
                    target = target,
                    condition = Condition(
                        conditional = conditional,
                        operator = operator,
                        conditionValue = conditionValue.toInt()
                    )
                )
            } else {
                Rule(target = string, condition = null)
            }
        }
    }

    fun evaluate(part: PartRange): Pair<Pair<String, PartRange>, PartRange> {
        return if (condition != null) {
            val (targetRange, remainingRange) = condition.evaluate(part)
            (target to targetRange) to remainingRange
        } else {
            (target to part) to part
        }
    }
}

private data class Workflow(val name: String, val rules: List<Rule>) {
    companion object {
        operator fun invoke(string: String): Workflow {
            val (name, rulesString) = string.split("{", "}").filter { it.isNotBlank() }
            val rules = rulesString.split(",").map { Rule(it) }
            return Workflow(name, rules)
        }
    }

    fun evaluate(partRange: PartRange): List<Pair<String, PartRange>> {
        val result = mutableListOf<Pair<String, PartRange>>()
        var currentRange = partRange
        for (rule in rules) {
            val (pair, remainingRange) = rule.evaluate(currentRange)
            result += pair
            currentRange = remainingRange
        }
        return result
    }
}

private fun parsePart(string: String): Part {
    return string.trim('{', '}').split(",").associate {
        val (key, value) = it.split("=")
        key to value.toInt()
    }
}

fun main() {
    val (workflowsString, partsString) = read("/2023/day19.txt").split("\n\n")
    val workflows = workflowsString.split("\n").filter { it.isNotBlank() }.map { Workflow(it) }.associateBy { it.name }
    val parts = partsString.split("\n").filter { it.isNotBlank() }.map { parsePart(it) }

    // Part 1
    println(part1(parts, workflows))

    // Part 2
    println(part2(workflows))
}

private fun part1(
    parts: List<Part>,
    workflows: Map<String, Workflow>
): Int {
    return parts.sumOf { if (process(it.mapValues { (_, v) -> v..v }, workflows) > 0L) it.values.sum() else 0 }
}

private fun part2(workflows: Map<String, Workflow>): Long {
    return process(mapOf("x" to 1..4000, "a" to 1..4000, "m" to 1..4000, "s" to 1..4000), workflows)
}

private fun process(
    partRange: Map<String, IntRange>,
    workflows: Map<String, Workflow>
): Long {
    val pending = ArrayDeque<Pair<String, PartRange>>()
    pending += "in" to partRange
    var totalAccepted = 0L
    while (pending.isNotEmpty()) {
        val (name, range) = pending.removeFirst()
        val workflow = workflows[name] ?: error("Could not find workflow: $name")
        val outcomes = workflow.evaluate(range)
        for ((nextName, nextRange) in outcomes) {
            when (nextName) {
                "A" -> {
                    totalAccepted += nextRange.totalSize
                }

                "R" -> {
                    // ignore
                }

                else -> {
                    pending += nextName to nextRange
                }
            }
        }
    }
    return totalAccepted
}