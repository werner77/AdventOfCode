package com.behindmedia.adventofcode.year2016

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.parseLines

private fun part1(instructions: List<CharArray>) {
    var position = Coordinate(1,1)
    var output = ""
    for (instrLine in instructions) {
        for (instr in instrLine) {
            val direction = when(instr) {
                'U' -> Coordinate.up
                'D' -> Coordinate.down
                'L' -> Coordinate.left
                'R' -> Coordinate.right
                else -> error("Unexpected instruction: $instr")
            }
            val nextPosition = position + direction
            if (nextPosition.x in 0 until 3 && nextPosition.y in 0 until 3) {
                position = nextPosition
            }
        }
        output += (1 + position.x + position.y * 3).toString()
    }
    println(output)
}

private fun part2(instructions: List<CharArray>) {
    val map = mapOf<Coordinate, Char>(
        Coordinate.origin to '7',
        Coordinate(-2, 0) to '5',
        Coordinate(-1, 0) to '6',
        Coordinate(1, 0) to '8',
        Coordinate(2, 0) to '9',
        Coordinate(-1, 1) to 'A',
        Coordinate(0, 1) to 'B',
        Coordinate(1, 1) to 'C',
        Coordinate(-1, -1) to '2',
        Coordinate(0, -1) to '3',
        Coordinate(1, -1) to '4',
        Coordinate(0, 2) to 'D',
        Coordinate(0, -2) to '1',
    )

    var position = Coordinate.origin
    var output = ""
    for (instrLine in instructions) {
        for (instr in instrLine) {
            val direction = when (instr) {
                'U' -> Coordinate.up
                'D' -> Coordinate.down
                'L' -> Coordinate.left
                'R' -> Coordinate.right
                else -> error("Unexpected instruction: $instr")
            }
            val nextPosition = position + direction
            val number = map[nextPosition]
            if (number != null) {
                position = nextPosition
            }
        }
        output += map[position]
    }
    println(output)
}

fun main() {
    val instructions = parseLines("/2016/day2.txt") {
        it.toCharArray()
    }
    part1(instructions)
    part2(instructions)
}