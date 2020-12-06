package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.parseLines
import kotlin.math.max
import kotlin.math.min

class Day5 {

    fun part1(input: String): Int {
        var highestSeatId = 0
        parseLines(input) {
            val coordinate = parseCoordinate(it)
            val seatId = coordinate.x * 8 + coordinate.y

            highestSeatId = max(highestSeatId, seatId)
        }
        return highestSeatId
    }

    fun part2(input: String): Int {
        var ids = mutableSetOf<Int>()
        var highestSeatId = 0
        var lowestSeatId = Int.MAX_VALUE
        parseLines(input) {
            val coordinate = parseCoordinate(it)
            val seatId = coordinate.x * 8 + coordinate.y
            ids.add(seatId)
            highestSeatId = max(highestSeatId, seatId)
            lowestSeatId = min(lowestSeatId, seatId)
        }

        for (id in lowestSeatId..highestSeatId) {
            if (!ids.contains(id)) {
                return id
            }
        }
        error("Not found")
    }

    fun parseCoordinate(string: String): Coordinate {
        var column = 0
        var row = 0
        for (i in 0 until 7) {
            val c = string[i]
            if (c == 'B') {
                row = row or (1 shl 6 - i)
            }
        }
        for (i in 0 until 3) {
            val c = string[i + 7]
            if (c == 'R') {
                column = column or (1 shl 2 - i)
            }
        }
        return Coordinate(row, column)
    }
}