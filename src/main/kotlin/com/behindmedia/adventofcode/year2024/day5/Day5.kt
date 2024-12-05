package com.behindmedia.adventofcode.year2024.day5

import com.behindmedia.adventofcode.common.*

private typealias Rule = Pair<Int, Int>

fun main() = timing {
    val data = read("/2024/day5.txt")
    val (firstSection, secondSection) = data.split("\n\n")
    val rules: List<Rule> = firstSection.splitNonEmpty("\n").map { line -> line.splitNonEmpty("|").map { it.toInt() }.toRule() }
    val pagesList: List<List<Int>> = secondSection.splitNonEmpty("\n").map { line -> line.splitNonEmpty(",").map { it.toInt() } }
    val ruleMap = rules.groupBy { it.first }

    var result1 = 0
    var result2 = 0
    for (pages in pagesList) {
        if (pages.isValid(ruleMap)) {
            result1 += pages[pages.size / 2]
        } else {
            val validPage = pages.makeValid(ruleMap)
            result2 += validPage[pages.size / 2]
        }
    }

    // Part1
    println(result1)

    // Part2
    println(result2)
}

private fun List<Int>.toRule(): Rule {
    require(this.size == 2)
    return Rule(this[0], this[1])
}

private fun List<Int>.isValid(ruleMap: Map<Int, List<Rule>>): Boolean {
    val seen = mutableSetOf<Int>()
    for (n in this) {
        val rules = ruleMap[n] ?: emptyList()
        if (rules.any { seen.contains(it.second) }) {
            return false
        }
        seen += n
    }
    return true
}

private fun List<Int>.makeValid(ruleMap: Map<Int, List<Rule>>): List<Int> {
    // Only select the applicable rules for this page: it is impossible to take all rules as they contain cycles!
    val allItems = this.toSet()
    val applicableRules = allItems.flatMap { ruleMap[it] ?: emptyList() }.filter { it.second in allItems}
    val sortedPages = applicableRules.topologicallySortedPages()
    require(sortedPages.size == this.size) {
        "Expected all items to be present"
    }
    return sortedPages
}

/**
 * Returns the topologically sorted pages under the conditions of these rules.
 */
private fun List<Rule>.topologicallySortedPages(): List<Int> {
    val dependencies = defaultMutableMapOf<Int, MutableSet<Int>>(putValueImplicitly = true) {
        mutableSetOf()
    }
    val inDegrees = defaultMutableMapOf<Int, Int> { 0 }
    for (rule in this) {
        if (dependencies[rule.first].add(rule.second)) {
            inDegrees[rule.second]++
        }
    }
    val pending = ArrayDeque<Int>()
    for (rule in this) {
        if (inDegrees[rule.first] == 0) {
            pending += rule.first
        }
    }
    require(pending.isNotEmpty()) {
        "Recursion in rules: no solution found"
    }
    val seen = linkedSetOf<Int>()
    while (pending.isNotEmpty()) {
        val next = pending.removeFirst()
        if (!seen.add(next)) {
            continue
        }
        for (dependency in dependencies[next]) {
            val count = inDegrees[dependency]
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
