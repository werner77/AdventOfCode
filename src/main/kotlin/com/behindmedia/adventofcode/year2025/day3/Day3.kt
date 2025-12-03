package com.behindmedia.adventofcode.year2025.day3

import com.behindmedia.adventofcode.common.*

private fun maxJoltage(line: String, startIndex: Int, remaining: Int): Long {
    if (remaining == 0) return 0L
    var best: Pair<Int, Long>? = null
    for (i in startIndex .. line.length - remaining) {
        val value  = line[i].digitToInt().toLong()
        if (best == null || value > best.second) {
            best = i to value
        }
    }
    requireNotNull(best)
    return best.second * 10L.pow(remaining - 1) + maxJoltage(line, best.first + 1, remaining - 1)
}

fun main() {
    val data = parseLines("/2025/day3.txt") { line ->
        line
    }
    for (remaining in listOf(2, 12)) {
        println(data.sumOf { maxJoltage(it, 0, remaining) })
    }
}