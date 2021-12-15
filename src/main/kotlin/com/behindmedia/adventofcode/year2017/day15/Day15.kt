package com.behindmedia.adventofcode.year2017.day15

import com.behindmedia.adventofcode.common.*

private fun calculate(input: Long, factor: Long): Long {
    return (input * factor) % 2147483647L
}

private const val MASK = 0xFFFFL
private const val FACTOR_A = 16807L
private const val FACTOR_B = 48271L

private fun part1(x: Long, y: Long): Long {
    var a = x
    var b = y
    var matches = 0L
    for (i in 0 until 40_000_000) {
        a = calculate(a, FACTOR_A)
        b = calculate(b, FACTOR_B)
        if (a and MASK == b and MASK) matches++
    }
    return matches
}

private fun part2(x: Long, y: Long): Long {
    var a = x
    var b = y
    var matches = 0L
    for (i in 0 until 5_000_000) {
        do {
            a = calculate(a, FACTOR_A)
        } while (a % 4L != 0L)
        do {
            b = calculate(b, FACTOR_B)
        } while (b % 8L != 0L)
        if (a and MASK == b and MASK) matches++
    }
    return matches
}

private val regex = """Generator ([A-Z]+) starts with (\d+)""".toRegex()

fun main() {
    val data = parseLines("/2017/day15.txt") { line ->
        val components = regex.matchEntire(line)?.groupValues ?: error("Could not parse line: $line")
        Pair(components[1], components[2].toLong())
    }
    println(part1(data[0].second, data[1].second))
    println(part2(data[0].second, data[1].second))
}