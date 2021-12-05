package com.behindmedia.adventofcode.year2021.day1

import com.behindmedia.adventofcode.common.*

private fun <T> parseLines(lineParser: (String) -> T): List<T> {
    return parseLines("/2021/day1.txt") {
        lineParser.invoke(it)
    }
}

private fun parseInts(): List<Int> {
    val values = parseLines {
        it.toInt()
    }
    return values
}

private fun part1() {
    var lastInt: Int? = null
    var count = 0
    for (value in parseInts()) {
        if (lastInt != null && value > lastInt) {
            count++
        }
        lastInt = value
    }
    println(count)
}

private fun part2() {
    val sumElements = ArrayDeque<Int>(3)
    var sum: Int? = null
    var count = 0
    for (value in parseInts()) {
        if (sumElements.size < 3) {
            sumElements += value
        } else if (sumElements.size == 3) {
            if (sum == null) {
                sum = sumElements.sum()
            }
            val lastSum = sum
            sum += value - sumElements.removeFirst()
            sumElements += value
            if (sum > lastSum) {
                count++
            }
        }
    }
    println(count)
}

fun main() {
    part1()
    part2()
}