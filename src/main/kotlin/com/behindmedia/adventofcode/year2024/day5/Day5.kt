package com.behindmedia.adventofcode.year2024.day5

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val data = read("/2024/day5.txt")
    val (firstSection, secondSection) = data.split("\n\n")
    val rules = firstSection.splitNonEmpty("\n").map { line -> line.splitNonEmpty("|").map { it.toInt() }.toRule() }
    val pages = secondSection.splitNonEmpty("\n").map { line -> line.splitNonEmpty(",").map { it.toInt() } }
    val ruleMap = rules.groupBy { it.before }

    var result1 = 0
    var result2 = 0
    for (page in pages) {
        if (isValid(page, ruleMap)) {
            result1 += page[page.size / 2]
        } else {
            val validPage = makeValid(page, ruleMap)
            result2 += validPage[page.size / 2]
        }
    }

    // Part1
    println(result1)

    // Part2
    println(result2)
}

private fun isValid(page: List<Int>, ruleMap: Map<Int, List<Rule>>): Boolean {
    val seen = mutableSetOf<Int>()
    for (n in page) {
        val rules = ruleMap[n] ?: emptyList()
        if (rules.any { seen.contains(it.after) }) {
            return false
        }
        seen += n
    }
    return true
}

private fun makeValid(page: List<Int>, ruleMap: Map<Int, List<Rule>>): List<Int> {
    val allItems = page.toMutableSet()
    val applicableRules = allItems.flatMap { ruleMap[it] ?: emptyList() }.filter { it.after in allItems}
    val order = applicableRules.topologicallySorted().withIndex().associateBy({ it.value }, { it.index })
    return page.sortedBy { order[it] ?: error("No order found") }
}

private data class Rule(val before: Int, val after: Int)

private fun List<Int>.toRule(): Rule {
    val (i, j) = this
    return Rule(i, j)
}

private fun List<Rule>.topologicallySorted(): List<Int> {
    val dependencies = defaultMutableMapOf<Int, MutableSet<Int>>(putValueImplicitly = true) { mutableSetOf() }
    val inDegrees = defaultMutableMapOf<Int, Int> { 0 }
    for (rule in this) {
        if (dependencies[rule.before].add(rule.after)) {
            inDegrees[rule.after]++
        }
    }
    val pending = ArrayDeque<Int>()
    for (rule in this) {
        if (inDegrees[rule.before] == 0) {
            pending += rule.before
        }
    }
    require(pending.isNotEmpty()) { "Could not find a solution" }
    val seen = linkedSetOf<Int>()
    while (pending.isNotEmpty()) {
        val next = pending.removeFirst()
        if (seen.contains(next)) {
            continue
        }
        seen += next
        val deps = dependencies[next]
        for (dependency in deps) {
            val count = inDegrees[dependency] ?: 0
            if (count == 1) {
                inDegrees.remove(dependency)
                pending.add(dependency)
            } else {
                inDegrees[dependency] = count - 1
            }
        }
    }
    return seen.toList()
}

