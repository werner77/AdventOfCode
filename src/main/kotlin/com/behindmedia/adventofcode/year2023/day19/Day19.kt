package com.behindmedia.adventofcode.year2023.day19

import com.behindmedia.adventofcode.common.productOf
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.splitWithDelimiters
import kotlin.math.max
import kotlin.math.min

private data class Condition(val conditional: String, val operator: String, val conditionValue: Int) {
    fun evaluate(part: Part): Boolean {
        val value = part[conditional]
        return when (operator) {
            "<" -> value < conditionValue
            ">" -> value > conditionValue
            else -> error("Invalid operator: $operator")
        }
    }

    /**
     * Returns the matching range and remaining range after applying the condition.
     * The remaining range may be applied to subsequent rules in a workflow.
     */
    fun evaluate(partRange: PartRange): Pair<PartRange, PartRange> {
        val range = partRange[conditional]
        val (matchingRange, remainingRange) = when (operator) {
            "<" ->
                range.start..min(conditionValue - 1, range.endInclusive) to
                        max(range.start, conditionValue)..range.endInclusive
            ">" -> max(conditionValue + 1, range.start)..range.endInclusive to
                    range.start..min(range.endInclusive, conditionValue)
            else -> error("Invalid operator: $operator")
        }
        return PartRange(partRange.ranges + (conditional to matchingRange)) to
                PartRange(partRange.ranges + (conditional to remainingRange))
    }
}

data class PartRange(val ranges: Map<String, IntRange>) {
    operator fun get(name: String): IntRange = ranges[name] ?: error("Invalid name: $name")

    /**
     * Total size of this part range
     */
    val totalSize: Long
        get() {
            return ranges.values.productOf { range ->
                max(0, range.last - range.first + 1).toLong()
            }
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

    fun evaluate(part: Part): String? {
        val condition = this.condition
        return if (condition != null && condition.evaluate(part)) {
            target
        } else if (condition == null) {
            target
        } else {
            null
        }
    }

    fun evaluate(part: PartRange): Pair<Pair<String, PartRange>, PartRange> {
        val condition = this.condition
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

    fun evaluate(part: Part): String {
        return rules.firstNotNullOfOrNull { it.evaluate(part) } ?: error("Could not evaluate workflow: $name")
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

data class Part(val values: Map<String, Int>) {
    operator fun get(name: String): Int = values[name] ?: error("Invalid name: $name")

    companion object {
        operator fun invoke(string: String): Part {
            val values = string.trim('{', '}').split(",").associate {
                val (key, value) = it.split("=")
                key to value.toInt()
            }
            return Part(values)
        }
    }
}

fun main() {
    val (workflowsString, partsString) = read("/2023/day19.txt").split("\n\n")
    val workflows = workflowsString.split("\n").filter { it.isNotBlank() }.map { Workflow(it) }.associateBy { it.name }
    val parts = partsString.split("\n").filter { it.isNotBlank() }.map { Part(it) }

    // Part 1
    println(part1(parts, workflows))

    // Part 2
    println(part2(workflows))
}

private fun part1(
    parts: List<Part>,
    workflows: Map<String, Workflow>
): Int {
    var ans = 0
    for (part in parts) {
        var name = "in"
        while (true) {
            val workflow = workflows[name] ?: error("Could not find workflow: $name")
            when (val outcome = workflow.evaluate(part)) {
                "A" -> {
                    ans += part.values.values.sum()
                    break
                }
                "R" -> {
                    break
                }
                else -> {
                    name = outcome
                }
            }
        }
    }
    return ans
}

private fun part2(workflows: Map<String, Workflow>): Long {
    val partRange = PartRange(mapOf("x" to 1..4000, "a" to 1..4000, "m" to 1..4000, "s" to 1..4000))
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