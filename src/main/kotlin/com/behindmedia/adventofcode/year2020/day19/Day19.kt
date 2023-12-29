package com.behindmedia.adventofcode.year2020.day19

import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.year2020.day19.Rule.Container
import com.behindmedia.adventofcode.year2020.day19.Rule.Literal
sealed class Rule {
    abstract fun matches(string: String, position: Int, rules: Map<Int, Rule>): Set<Int>
    fun matchEntire(string: String, rules: Map<Int, Rule>): Boolean {
        return string.length in matches(string, 0, rules)
    }
    data class Container(val id: Int, val options: List<List<Int>>): Rule() {
        override fun matches(string: String, position: Int, rules: Map<Int, Rule>): Set<Int> {
            val result = mutableSetOf<Int>()
            for (option in options) {
                var positions = mutableSetOf(position)
                for (ruleId in option) {
                    val nextPositions = mutableSetOf<Int>()
                    for (current in positions) {
                        val rule = rules[ruleId] ?: error("Could not find rule for ruleID: $ruleId")
                        val matches = rule.matches(string, current, rules)
                        nextPositions += matches
                    }
                    positions = nextPositions
                }
                result += positions
            }
            return result
        }
    }
    data class Literal(val id: Int, val value: String): Rule() {
        override fun matches(string: String, position: Int, rules: Map<Int, Rule>): Set<Int> {
            return if (string.startsWith(prefix = value, startIndex = position)) {
                setOf(position + value.length)
            } else {
                emptySet()
            }
        }
    }
}

private fun parse(text: String): Pair<Map<Int, Rule>, List<String>> {
    val (first, second) = text.split("\n\n").map { it.trim() }
    val ruleMap = mutableMapOf<Int, Rule>()
    for (line in first.split("\n")) {
        val (a, b) = line.split(": ")
        val ruleId = a.toInt()
        val rule = if (b.contains('"')) {
            val string = b.trim('"')
            Literal(ruleId, string)
        } else {
            val rules = b.split("|").map { part -> part.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }
            Container(ruleId, rules)
        }
        ruleMap[ruleId] = rule
    }
    return ruleMap to second.split("\n")
}

fun main() {
    val (rules, lines) = parse(read("/2020/day19.txt"))

    val rule = rules[0] ?: error("Rule zero not found")

    // Part 1
    println(lines.count { rule.matchEntire(it, rules) })

    // Part 2
    val modifiedRules = rules + listOf(
        8 to Container(id = 8, options = listOf(listOf(42), listOf(42, 8))),
        11 to Container(id = 11, options = listOf(listOf(42, 31), listOf(42, 11, 31)))
    )
    println(lines.count { rule.matchEntire(it, modifiedRules) })
}