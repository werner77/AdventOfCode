package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.parseLines
import java.lang.Integer.max

class Day3 {

    fun part1(inputFile: String, deltaX: Int = 3, deltaY: Int = 1): Long {
        val map = mutableMapOf<Coordinate, Char>()

        var y = 0
        var maxX = 0
        parseLines(inputFile) {
            for (x in it.indices) {
                if (it[x] == '#') {
                    map[Coordinate(x, y)] = '#'
                }
            }
            maxX = max(maxX, it.length)
            y++
        }

        var coordinate = Coordinate.origin
        var count = 0L
        while (coordinate.y < y) {
            if (map[coordinate] != null) {
                count++
            }
            coordinate = coordinate.copy(x = (coordinate.x + deltaX) % maxX, y = coordinate.y + deltaY)
        }
        return count
    }

    fun part2(inputFile: String): Long {
        val deltaX = listOf(1, 3, 5, 7, 1)
        val deltaY = listOf(1, 1, 1, 1, 2)

        var count = 1L

        for (i in deltaX.indices) {
            val dx = deltaX[i]
            val dy = deltaY[i]
            count *= part1(inputFile, dx, dy)
        }
        return count
    }
}