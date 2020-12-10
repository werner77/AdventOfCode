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
            if (diff <= 3) {
                if (diff == 1) {
                    deltaOneCount++
                } else if (diff == 3) {
                    deltaThreeCount++
                }
            } else {
                error("Could not find an adapter with a diff <= 3")
            }
            lastValue = value
        }
        return deltaOneCount * deltaThreeCount
    }

    fun part2(input: String): Long {
        val values = parseLines(input) { it.toInt() }.toSet()
        val maxValue = values.maxOrNull() ?: error("There should be at least one value")

        // Dynamic programming, an array keeping track of the number of ways to arrive at the joltage which is the index
        // of the array
        val ways = LongArray(maxValue + 1) { 0 }

        // One way to start (joltage == 0)
        ways[0] = 1

        for (i in 1..maxValue) {
            if (values.contains(i)) {
                // Number of ways is the sum of the ways to arrive at the three joltages below
                ways[i] = ways.getOrElse(i - 3) { 0 } + ways.getOrElse(i - 2) { 0 } + ways.getOrElse(i - 1) { 0 }
            }
        }
        return ways[maxValue]
    }
}