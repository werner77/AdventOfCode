package com.behindmedia.adventofcode.year2023.day16

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.RotationDirection.Left
import com.behindmedia.adventofcode.common.RotationDirection.Right
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing

fun main() {
    val grid = CharGrid(read("/2023/day16.txt"))
    timing {
        // Part 1
        println(process(Coordinate.origin to Coordinate.right, grid))

        // Part 2
        println(candidateSequence(grid).maxOf { process(it, grid) })
    }
}

private fun candidateSequence(grid: CharGrid): Sequence<Pair<Coordinate, Coordinate>> {
    val maxX = grid.maxX
    val maxY = grid.maxY
    return sequence {
        for (x in listOf(0, maxX)) {
            for (y in 0..maxY) {
                val direction = if (x == 0) {
                    Coordinate.right
                } else {
                    Coordinate.left
                }
                yield(Coordinate(x, y) to direction)
            }
        }
        for (x in 0 until maxX) {
            for (y in listOf(0, maxY)) {
                val direction = if (y == 0) {
                    Coordinate.down
                } else {
                    Coordinate.up
                }
                yield(Coordinate(x, y) to direction)
            }
        }
    }
}

private fun process(start: Pair<Coordinate, Coordinate>, grid: CharGrid): Int {
    val pending = ArrayDeque<Pair<Coordinate, Coordinate>>()
    val seen = mutableSetOf<Pair<Coordinate, Coordinate>>()
    pending += start
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        val (position, direction) = current
        val tile = grid.getOrNull(position) ?: continue
        if (seen.contains(current)) continue
        seen += current
        val nextDirections: List<Coordinate> = when (tile) {
            '.' -> listOf(direction)
            '|' -> if (direction.isHorizontal) listOf(Coordinate.up, Coordinate.down) else listOf(direction)
            '-' -> if (direction.isVertical) listOf(Coordinate.left, Coordinate.right) else listOf(direction)
            '/' -> if (direction.isHorizontal) listOf(direction.rotate(Left)) else listOf(direction.rotate(Right))
            '\\' -> if (direction.isVertical) listOf(direction.rotate(Left)) else listOf(direction.rotate(Right))
            else -> error("Invalid tile")
        }
        nextDirections.forEach {
            pending += position + it to it
        }
    }
    return seen.asSequence().map { it.first }.toSet().size
}