package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.RotationDirection
import com.behindmedia.adventofcode.common.parseLines

class Day12 {
    fun part1(input: String) : Int {
        val commands = parseCommands(input)
        var position = Coordinate.origin
        var vector = Coordinate.right
        for (command in commands) {
            when (command.first) {
                'N' -> position += Coordinate.up * command.second
                'S' -> position += Coordinate.down * command.second
                'E' -> position += Coordinate.right * command.second
                'W' -> position += Coordinate.left * command.second
                'L' -> vector = rotate(vector, RotationDirection.Left, command.second)
                'R' -> vector = rotate(vector, RotationDirection.Right, command.second)
                'F' -> position += (vector * command.second)
            }
        }
        return position.manhattenDistance(Coordinate.origin)
    }

    fun part2(input: String) : Int {
        val commands = parseCommands(input)
        var position = Coordinate.origin
        var vector = (Coordinate.right * 10 + Coordinate.up)
        for (command in commands) {
            when (command.first) {
                'N' -> vector += Coordinate.up * command.second
                'S' -> vector += Coordinate.down * command.second
                'E' -> vector += Coordinate.right * command.second
                'W' -> vector += Coordinate.left * command.second
                'L' -> vector = rotate(vector, RotationDirection.Left, command.second)
                'R' -> vector = rotate(vector, RotationDirection.Right, command.second)
                'F' -> position += vector * command.second
            }
        }
        return position.manhattenDistance(Coordinate.origin)
    }

    private fun parseCommands(input: String): List<Pair<Char, Int>> {
        return parseLines(input) {
            val command = it[0]
            val amount = it.substring(1).toInt()
            Pair(command, amount)
        }
    }

    private fun rotate(vector: Coordinate, direction: RotationDirection, degrees: Int) : Coordinate {
        assert(degrees % 90 == 0)
        var newVector = vector
        for (i in 0 until degrees / 90) {
            newVector = newVector.rotate(direction)
        }
        return newVector
    }
}