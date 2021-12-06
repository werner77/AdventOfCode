package com.behindmedia.adventofcode.year2016.day20

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class Range(val min: Long, val max: Long) {
    fun contains(ip: Long): Boolean {
        return ip in min..max
    }
}

private const val MAX_VALID_IP = 4294967295

private fun findMinValid(start: Long = 0, ranges: List<Range>): Long? {
    val sortedRanges = sortRanges(ranges)
    var current = start
    for (range in sortedRanges) {
        val (min, max) = range
        if (min <= current) current = max(current, max + 1)
        if (max < current) continue
    }
    return if (current in 0..MAX_VALID_IP) current else null
}

private fun countValid(ranges: List<Range>): Long {
    val sortedRanges = sortRanges(ranges)
    var subRanges = sortedRanges
    var totalCount = 0L
    var start = 0L
    while (true) {
        val minValidIp = findMinValid(start, subRanges) ?: break
        val index = subRanges.indexOfFirst { it.min > minValidIp }
        if (index < 0) {
            totalCount += MAX_VALID_IP - minValidIp + 1
            break
        }
        subRanges = subRanges.subList(index, subRanges.size)
        val nextRange = subRanges[0]
        totalCount += nextRange.min - minValidIp
        start = nextRange.min
    }
    return totalCount
}

private fun sortRanges(ranges: List<Range>): List<Range> {
    val sortedRanges = ranges.sortedWith { o1, o2 ->
        val minComparison = o1.min.compareTo(o2.min)
        if (minComparison == 0) {
            o2.max.compareTo(o1.max)
        } else {
            minComparison
        }
    }
    return sortedRanges
}

fun main() {
    val ranges = parseLines("/2016/day20.txt") { line ->
        val (min, max) = line.splitNonEmptySequence("-") { it.toLong() }.toList()
        Range(min, max)
    }

    // Part1
    println(findMinValid(0L, ranges) ?: error("No valid IP found"))

    // Part2
    println(countValid(ranges))


}