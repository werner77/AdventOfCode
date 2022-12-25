package com.behindmedia.adventofcode.year2022.day25

import com.behindmedia.adventofcode.common.*

private fun toSnafu(number: Long): String {
    if (number == 0L) {
        return "0"
    }

    var result = ""
    var n = number
    var i = 1
    while (n != 0L) {
        n += 2
        val k = n % 5
        val c = when (k) {
            4L -> "2"
            3L -> "1"
            2L -> "0"
            1L -> "-"
            0L -> "="
            else -> error("Invalid value: $k")
        }
        i++
        result = c + result
        n /= 5
    }
    return result
}

private fun fromSnafu(string: String): Long {
    var value = 0L
    var multiplier = 1L
    for (i in string.length - 1 downTo 0) {
        val c = string[i]
        val k = when (c) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> error("Invalid character: $c")
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