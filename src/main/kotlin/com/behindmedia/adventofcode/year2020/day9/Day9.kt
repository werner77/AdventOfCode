package com.behindmedia.adventofcode.year2020.day9

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.processPairs

fun main() {
    val data = parseLines("/2020/day9.txt") { line ->
        line.toLong()
    }

    val required = part1(data)
    println(required)

    println(part2(data, required))
}

private fun part2(data: List<Long>, required: Long): Long {
    var currentSum = data[0]
    var i = 0
    var j = 1
    while (i < data.size - 1) {
        while (j < data.size && currentSum < required) {
            currentSum += data[j++]
        }
        while (currentSum > required && i < j - 2) {
            currentSum -= data[i++]
        }
        if (currentSum == required) {
            return data.subList(i, j).min() + data.subList(i, j).max()
        }
    }
    error("No result found")
}

private fun part1(data: List<Long>, windowSize: Int = 25): Long {
    val window = ArrayDeque<Long>()
    for (n in data) {
        if (window.size < windowSize) {
            window += n
        } else {
            window.processPairs { first, second ->
                if (first + second == n) {
                    n
                } else {
                    null
                }
            } ?: return n
            window.removeFirst()
            window.add(n)
        }
    }
    error("No result found")
}