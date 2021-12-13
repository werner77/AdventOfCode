package com.behindmedia.adventofcode.year2017.day1

import com.behindmedia.adventofcode.common.*

private fun part1(data: List<Int>): Int {
    return data.foldIndexed(0) { i, sum, value ->
        val j = (data.size + i - 1) % data.size
        if (value == data[j]) sum + value else sum
    }
}

private fun part2(data: List<Int>): Int {
    return data.foldIndexed(0) { i, sum, value ->
        val j = (data.size + data.size / 2 + i) % data.size
        if (value == data[j]) sum + value else sum
    }
}

fun main() {
    val allDigits = mutableListOf<Int>()
    parseLines("/2017/day1.txt") { line ->
        line.map { it - '0' }.forEach { allDigits += it }
    }
    println(part1(allDigits))
    println(part2(allDigits))
}