package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines

class Day19 {
    fun part1(input: String): Int {
       return solve(input, false)
    }

    fun part2(input: String): Int {
        return solve(input, true)
    }

    data class Rule(val id: Int, val dependencies: List<List<Int>>, val value: String?)

    private fun solve(input: String, replaceRules: Boolean): Int {
        var section = 0
        val ruleMap = mutableMapOf<Int, Rule>()
        val words = mutableListOf<String>()
        parseLines(input) { line ->
            when {
                line.isBlank() -> section++
                section == 0 -> {
                    val components = line.split(":")
                    assert(components.size == 2)
                    val ruleId = components[0].trim().toInt()
                    val rules = components[1].split("|").map { it.trim() }
                    assert(rules.size <= 2)
                    val dependencies = mutableListOf<List<Int>>()
                    var value: String? = null
                    for (rule in rules) {
                        if (rule.contains('"')) {
                            assert(rules.size == 1)
                            value = rule.trim('"')
                        } else {
                            dependencies.add(rule.split(" ").map { it.trim().toInt() })
                        }
                    }
                    ruleMap[ruleId] = Rule(ruleId, dependencies, value)
                }
                section == 1 -> words.add(line.trim())
                else -> error("Expected no more than 2 sections to be present")
            }
        }

        if (replaceRules) {
            ruleMap[8] = Rule(8, listOf(listOf(42, 8), listOf(42)), null)
            ruleMap[11] = Rule(11, listOf(listOf(42, 11, 31), listOf(42, 31)), null)
        }
        return words.count {
            match(it, 0, 0, ruleMap).contains(it.length)
        }
    }

    /**
     * Tries to match the specified string from the specified start index (position) to the rule with the specified id.
     *
     * Returns a set of possible positions after the match (or an empty set if no matches exist)
     */
    private fun match(string: String, position: Int, ruleId: Int, map: Map<Int, Rule>): Set<Int> {
        val rule = map[ruleId] ?: error("Rule with id $ruleId does not exist")
        return rule.value?.let { value ->
            if (string.regionMatches(position, value, 0, value.length)) {
                setOf(position + value.length)
            } else {
                emptySet()
            }
        } ?: run {
            rule.dependencies.fold(mutableSetOf()) { result, ruleSequence ->
                result.apply {
                    var positions = listOf(position)
                    for (nextRuleId in ruleSequence) {
                        positions = positions.flatMap {
                            match(string, it, nextRuleId, map)
                        }
                        if (positions.isEmpty()) break
                    }
                    addAll(positions)
                }
            }
        }
    }
}