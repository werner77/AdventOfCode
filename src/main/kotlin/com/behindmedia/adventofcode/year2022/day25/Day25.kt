package com.behindmedia.adventofcode.year2022.day25

import com.behindmedia.adventofcode.common.*

private fun toSnafu(number: Long): String {
    if (number == 0L) {
        return "0"
    }
    val result = ArrayDeque<Char>()
    var n = number
    while (n != 0L) {
        n += 2
        val c = when (n % 5) {
            4L -> '2'
            3L -> '1'
            2L -> '0'
            1L -> '-'
            0L -> '='
            else -> error("Invalid value: ${n % 5}")
        }
        result.addFirst(c)
        n /= 5
    }
    return result.joinToString("")
}

private fun fromSnafu(string: String): Long {
    var value = 0L
    var multiplier = 1L
    for (i in string.length - 1 downTo 0) {
        val k = when (string[i]) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> error("Invalid character: ${string[i]}")
        }
        value += multiplier * k
        multiplier *= 5L
    }
    return value
}

fun main() {
    val data = parseLines("/2022/day25.txt") { line ->
        fromSnafu(line)
    }

    val sum = data.sum()
    println(toSnafu(sum))
}