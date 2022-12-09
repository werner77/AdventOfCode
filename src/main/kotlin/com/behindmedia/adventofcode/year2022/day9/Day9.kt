package com.behindmedia.adventofcode.year2022.day9

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.common.Coordinate.Companion
import kotlin.math.abs
import kotlin.math.sign

private data class Command(val dir: Char, val amount: Int) {
    val direction: Coordinate
        get() = when (dir) {
            'L' -> Coordinate.left
            'R' -> Coordinate.right
            'D' -> Coordinate.down
            'U' -> Companion.up
            else -> error("Unknown direction: $dir")
        }
}

fun main() {
    val data = parseLines("/2022/day9.txt") { line ->
        val components = line.splitNonEmptySequence(" ").toList()
        Command(components[0][0], components[1].toInt())
    }
    part1(data)
    part2(data)
}

private fun part1(data: List<Command>) {
    simulate(data, 2)
}

private fun part2(data: List<Command>) {
    simulate(data, 10)
}

private fun simulate(commands: List<Command>, ropeLength: Int) {
    val seen = mutableSetOf<Coordinate>()
    val knots = Array(ropeLength) { Coordinate.origin }
    seen.add(Coordinate.origin)
    for (command in commands) {
        repeat(command.amount) {
            knots[0] += command.direction
            for (i in 1 until ropeLength) {
                val head = knots[i - 1]
                val diff = head - knots[i]
                if (abs(diff.x) > 1 || abs(diff.y) > 1) {
                    knots[i] += Coordinate(diff.x.sign, diff.y.sign)
                    if (i == ropeLength - 1) {
                        seen += knots[i]
                    }
                }
            }
        }
    }
    println(seen.size)
}