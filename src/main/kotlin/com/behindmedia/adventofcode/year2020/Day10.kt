package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines

class Day10 {

    fun part1(input: String): Int {
        val values = parseLines(input) { it.toInt() }.sorted()
        var lastValue = 0
        var deltaOneCount = 0
        // One for the last step always
        var deltaThreeCount = 1

        for (value in values) {
            val diff = value - lastValue
            when (diff) {
                1 -> deltaOneCount++
                2 -> break
                3 -> deltaThreeCount++
                else -> error("Could not find an adapter with a diff <= 3")
            }
            lastValue = value
        }
        return deltaOneCount * deltaThreeCount
    }

    fun part2(input: String): Long {
        val values = parseLines(input) { it.toInt() }.sorted()
        val maxValue = values.maxOrNull() ?: error("There should be at least one value")

        // Dynamic programming, an array keeping track of the number of ways to arrive at the joltage which is the index
        // of the array
        val ways = mutableMapOf<Int, Long>()

        // One way to start (joltage == 0)
        ways[0] = 1L

        for (i in values) {
            // Number of ways is the sum of the ways to arrive at the three joltages below
            for (j in i-3 until i) {
                ways[i] = (ways[i] ?: 0L) + (ways[j] ?: 0L)
            }
        }
        return ways[maxValue] ?: error("No value for max which should never occur")
    }
}