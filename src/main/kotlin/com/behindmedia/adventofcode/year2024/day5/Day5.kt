package com.behindmedia.adventofcode.year2024.day5

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val data = read("/2024/day5.txt")
    val (firstSection, secondSection) = data.split("\n\n")
    val rules = firstSection.splitNonEmpty("\n").map { line -> line.splitNonEmpty("|").map { it.toInt() }.let(Rule::invoke) }
    val pages = secondSection.splitNonEmpty("\n").map { line -> line.splitNonEmpty(",").map { it.toInt() } }
    val ruleMap = rules.groupBy { it.before }

    var result1 = 0
    var result2 = 0
    for (page in pages) {
        if (page.isValidPage(ruleMap)) {
            result1 += page[page.size / 2]
        } else {
            val validPage = page.makeValidPage(ruleMap)
            result2 += validPage[page.size / 2]
        }
    }

    // Part1
    println(result1)

    // Part2
    println(result2)
}

private fun List<Int>.isValidPage(ruleMap: Map<Int, List<Rule>>): Boolean {
    val seen = mutableSetOf<Int>()
    for (n in this) {
        val rules = ruleMap[n] ?: emptyList()
        if (rules.any { seen.contains(it.after) }) {
            return false
        }
        seen += n
    }
    return true
}

private fun List<Int>.makeValidPage(ruleMap: Map<Int, List<Rule>>): List<Int> {
    // Only select the applicable rules for this page: it is impossible to take all rules as they contain cycles!
    val allItems = this.toSet()
    val applicableRules = allItems.flatMap { ruleMap[it] ?: emptyList() }.filter { it.after in allItems}
    val sortedPages = applicableRules.topologicallySortedPages()
    require(sortedPages.size == this.size) {
        "Expected all items to be present"
    }
    return sortedPages
}

private data class Rule(val before: Int, val after: Int) {
    companion object {
        operator fun invoke(items: List<Int>): Rule {
            require(items.size == 2)
            return Rule(items[0], items[1])
        }
    }
}

/**
 * Returns the topologically sorted pages under the conditions of these rules.
 */
private fun List<Rule>.topologicallySortedPages(): List<Int> {
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

