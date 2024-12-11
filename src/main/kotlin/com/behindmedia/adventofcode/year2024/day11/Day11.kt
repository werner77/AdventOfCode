package com.behindmedia.adventofcode.year2024.day11

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val numbers = read("/2024/day11.txt").trim().splitNonEmpty(" ").map { it.toLong() }
    println(getTotalSize(numbers, 25))
    println(getTotalSize(numbers, 75))
}

private fun getTotalSize(numbers: List<Long>, count: Int): Long {
    val cache = mutableMapOf<Pair<Long, Int>, Long>()
    return numbers.sumOf {
        getSize(it, count, cache)
    }
}

private fun getSize(value: Long, iterationsLeft: Int, cache: MutableMap<Pair<Long, Int>, Long>): Long {
    if (iterationsLeft == 0) {
        return 1L
    }
    val key = value to iterationsLeft
    val cachedValue = cache[key]
    if (cachedValue != null) {
        return cachedValue
    } else {
        val valueString = value.toString()
        val valueLength = valueString.length
        // We have to manually compute this value
        return if (value == 0L) {
            getSize(1L, iterationsLeft - 1, cache)
        } else if (valueLength % 2 == 0) {
            val left = valueString.substring(0, valueLength / 2).toLong()
            val right = valueString.substring(valueLength / 2, valueLength).toLong()
            return getSize(left, iterationsLeft - 1, cache) +
                    getSize(right, iterationsLeft - 1, cache)
        } else {
            getSize(value * 2024L, iterationsLeft - 1, cache)
        }.also {
            cache[key] = it
        }
    }
}
