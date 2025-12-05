package com.behindmedia.adventofcode.year2025.day5

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun mergeRanges(ranges: List<LongRange>): List<LongRange> {
    val mergedRanges = ranges.sortedBy { it.first }.toMutableList()
    var i = 0
    while (i < mergedRanges.lastIndex) {
        val thisRange = mergedRanges[i]
        val nextRange = mergedRanges[i + 1]
        if (nextRange.first <= thisRange.last + 1) {
            // We can merge these two ranges
            mergedRanges[i] = thisRange.first..max(nextRange.last, thisRange.last)
            mergedRanges.removeAt(i + 1)
        } else {
            // Cannot merge
            i++
        }
    }
    return mergedRanges
}

fun main() {
    val data = read("/2025/day5.txt")
    val parts = data.split("\n\n")
    require(parts.size == 2) { "Unparsable input: $data" }
    val ranges = parts[0].trim().split("\n").map { line ->
        val components = line.split("-")
        require(components.size == 2) { "Unparsable input: $line" }
        components[0].toLong()..components[1].toLong()
    }
    val values = parts[1].trim().split("\n").map { it.toLong() }

    // Part 1
    println(values.count { v -> ranges.any { v in it } })

    // Part 2
    println(mergeRanges(ranges).sumOf { it.last - it.first + 1L })
}