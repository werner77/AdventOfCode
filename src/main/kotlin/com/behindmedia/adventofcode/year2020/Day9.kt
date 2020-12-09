package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines
import java.util.*

class Day9 {

    fun part1(input: String, preambleSize: Int = 25): Long {
        val values = parseValues(input)

        for (i in preambleSize until values.size) {
            if (!check(values, i, preambleSize)) {
                return values[i]
            }
        }
        error("No invalid value found")
    }

    fun part2(input: String, preambleSize: Int = 25): Long {
        val requiredSum = part1(input, preambleSize)
        val values = parseValues(input)

        var sum = 0L
        val sumRange = LinkedList<Long>()

        for (value in values) {
            sumRange.add(value)
            sum += value
            while (sum > requiredSum && sumRange.isNotEmpty()) {
                sum -= sumRange.removeFirst()
            }
            if (sum == requiredSum && sumRange.size >= 2) {
                val minValue = sumRange.minOrNull() ?: error("Expected min to be present")
                val maxValue = sumRange.maxOrNull() ?: error("Expected max to be present")
                return minValue + maxValue
            }
        }
        error("Required sum not found")
    }

    private fun parseValues(input: String): List<Long> {
        return parseLines(input) {
            it.toLong()
        }
    }

    private fun check(values: List<Long>, valueIndex: Int, preambleSize: Int): Boolean {
        val value = values[valueIndex]
        for (i in valueIndex - preambleSize until valueIndex) {
            for (j in i + 1 until valueIndex) {
                if (values[i] + values[j] == value) {
                    return true
                }
            }
        }
        return false
    }
}