package com.behindmedia.adventofcode.year2020.day5

import com.behindmedia.adventofcode.common.parseLines

private data class Seat(val row: Int, val column: Int) {
    companion object {
        operator fun invoke(encoded: String): Seat {
            var colRange = 0..7
            var rowRange = 0..127
            for (c in encoded) {
                when (c) {
                    'F' -> rowRange = rowRange.first..(rowRange.first + rowRange.last) / 2
                    'B' -> rowRange = (rowRange.first + rowRange.last) / 2 + 1..rowRange.last
                    'L' -> colRange = colRange.first..(colRange.first + colRange.last) / 2
                    'R' -> colRange = (colRange.first + colRange.last) / 2 + 1..colRange.last
                }
            }
            require(rowRange.first == rowRange.last)
            require(colRange.first == colRange.last)
            return Seat(rowRange.first, colRange.first)
        }
    }

    val id: Int
        get() = row * 8 + column
}

fun main() {
    val data = parseLines("/2020/day5.txt") { line ->
        Seat(line)
    }

    // Part 1
    println(data.maxOf { it.id })

    // Part 2
    val minId = data.minOf { it.id }
    val maxId = data.maxOf { it.id }
    println(((minId..maxId).toSet() - data.map { it.id }.toSet()).single())
}