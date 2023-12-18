package com.behindmedia.adventofcode.year2023.day18

import com.behindmedia.adventofcode.common.LongCoordinate
import com.behindmedia.adventofcode.common.insidePointCount
import com.behindmedia.adventofcode.common.parseLines

private typealias Point = LongCoordinate

private data class Instruction(val direction: Point, val count: Long) {
    companion object {
        fun parse(string: String, part2: Boolean): Instruction {
            return if (part2) parse2(string) else parse1(string)
        }
        fun parse1(string: String): Instruction {
            val (direction, count, _) = string.split(" ")
            val coordinate = when (direction) {
                "R" -> Point.right
                "L" -> Point.left
                "D" -> Point.down
                "U" -> Point.up
                else -> error("Invalid direction: $direction")
            }
            return Instruction(direction = coordinate, count = count.toLong())
        }

        fun parse2(string: String): Instruction {
            val (_, _, encoded) = string.split(" ")
            val count = encoded.substring(2, 7).toLong(radix = 16)
            val direction = encoded.substring(7, 8).toLong(radix = 16)
            val coordinate = when (direction) {
                0L -> Point.right
                1L -> Point.down
                2L -> Point.left
                3L -> Point.up
                else -> error("Invalid direction: $direction")
            }
            return Instruction(direction = coordinate, count = count)
        }
    }
}

fun main() {
    for (part2 in listOf(false, true)) {
        val data = parseLines("/2023/day18.txt") { line ->
            Instruction.parse(line, part2)
        }
        val corners = mutableListOf<Point>()
        var current = Point.origin
        var count = 0L
        for (instruction in data) {
            current += instruction.direction * instruction.count
            corners += current
            count += instruction.count
        }
        println(count + corners.insidePointCount(boundaryPointCount = count))
    }
}