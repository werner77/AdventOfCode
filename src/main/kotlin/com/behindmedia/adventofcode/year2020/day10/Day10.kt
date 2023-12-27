package com.behindmedia.adventofcode.year2020.day10

import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.parseLines
import kotlin.math.max

fun main() {
    val d = parseLines("/2020/day10.txt") { line ->
        line.toInt()
    }.sorted()

    val data = d + listOf(d.max() + 3)

    println(part1(data))

    // Part 2
    println(part2(data))
}

private fun part1(data: List<Int>): Int {
    var lastValue = 0
    var ones = 0
    var threes = 0
    for (value in data) {
        val diff = value - lastValue
        when (diff) {
            1 -> ones++
            2 -> break
            3 -> threes++
            else -> error("Could not find an adapter with a diff <= 3")
        }
        lastValue = value
    }
    return ones * threes
}

private fun part2(data: List<Int>): Long {
    val dp = defaultMutableMapOf<Int, Long> { 0L }
    dp[0] = 1L
    for (j in data) {
        for (k in max(0, j - 3) until j) {
            dp[j] += dp[k]
        }
    }
    return dp[data.last()]
}