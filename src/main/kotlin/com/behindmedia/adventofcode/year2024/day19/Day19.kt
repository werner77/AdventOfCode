package com.behindmedia.adventofcode.year2024.day19

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val data = read("/2024/day19.txt")
    val (first, second) = data.splitTrimmed("\n\n")
    val patterns = first.splitTrimmed(",")
    val designs = second.splitTrimmed("\n")
    val matchCounts = designs.map { matchCount(it, patterns) }

    // Part 1
    println(designs.indices.count { matchCounts[it] > 0L })

    // Part 2
    println(matchCounts.sum())
}

private fun matchCount(design: String, patterns: List<String>): Long {
    return dp(design, patterns)
}

private fun dp(design: String, patterns: List<String>): Long {
    val cache = LongArray(design.length + 1) { 0L }
    cache[0] = 1L
    for (index in design.indices) {
        val currentCount = cache[index]
        for (pattern in patterns) {
            if (design.hasSubstring(pattern, index)) {
                cache[index + pattern.length] += currentCount
            }
        }
    }
    return cache[design.length]
}

private fun String.hasSubstring(other: String, offset: Int): Boolean {
    for (i in other.indices) {
        val index = offset + i
        if (index >= this.length || this[index] != other[i]) {
            return false
        }
    }
    return true
}