package com.behindmedia.adventofcode.year2023.day9

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2023/day9.txt") { line ->
        line.split(" ").map { it.toLong()}
    }

    // Part 1
    println(data.sumOf { process(it, false) })

    // Part 2
    println(data.sumOf { process(it, true) })
}

private fun process(data: List<Long>, reverse: Boolean): Long {
    val numbers = ArrayDeque<Long>()
    var current = data
    while (current.any { it != 0L } && current.size >= 2) {
        numbers.addFirst(if (reverse) current.first() else current.last())
        current = List(current.size - 1) {
            current[it + 1] - current[it]
        }
    }
    return numbers.fold(0L) { value, n ->
        if (reverse) n - value else n + value
    }
}

