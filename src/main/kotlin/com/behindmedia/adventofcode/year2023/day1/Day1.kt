package com.behindmedia.adventofcode.year2023.day1

import com.behindmedia.adventofcode.common.parseLines

private val digitStrings = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
)

private fun String.findDigit(includeWords: Boolean, selector: (String, String, Int?) -> Int?): Int {
    var bestIndex: Int? = null
    var bestValue: Int? = null
    if (includeWords) {
        for ((key, value) in digitStrings) {
            val index = selector.invoke(this, key, bestIndex) ?: continue
            bestValue = value
            bestIndex = index
        }
    }
    for (value in digitStrings.values) {
        val index = selector.invoke(this, value.toString(), bestIndex) ?: continue
        bestValue = value
        bestIndex = index
    }
    return bestValue ?: error("No value found in line: $this")
}

private fun String.firstDigit(includeWords: Boolean): Int {
    return findDigit(includeWords) { l, v, i ->
        l.indexOf(string = v).takeIf { it >= 0 && (i == null || it < i) }
    }
}

private fun String.lastDigit(includeWords: Boolean): Int {
    return findDigit(includeWords) { l, v, i ->
        l.lastIndexOf(string = v).takeIf { it >= 0 && (i == null || it > i) }
    }
}

fun main() {
    val data = parseLines("/2023/day1.txt") { it }

    // Part 1
    println(data.sumOf { line -> line.firstDigit(false) * 10 + line.lastDigit(false) })

    // Part 2
    println(data.sumOf { line -> line.firstDigit(true) * 10 + line.lastDigit(true) })
}