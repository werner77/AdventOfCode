package com.behindmedia.adventofcode.year2024.day6

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val grid = CharGrid(read("/2024/day6.txt"))
    val start = grid.single { it.value == '^' }.key to Coordinate.up
    val seen = part1(start, grid)
    println(seen.size)
    println(part2(start, grid, seen))
}

private fun part1(
    start: Pair<Coordinate, Coordinate>,
    grid: CharGrid
): Collection<Coordinate> {
    var current = start
    val seen = mutableSetOf<Coordinate>()
    while (true) {
        seen += current.first
        current = grid.findNext(current) ?: break
    }
    return seen
}

private fun part2(
    start: Pair<Coordinate, Coordinate>,
    grid: CharGrid,
    candidates: Collection<Coordinate>
): Int {
    return candidates.countParallel { obstruction ->
        if (obstruction == start.first) {
            false
        } else {
            grid.recursionHappens(start, obstruction)
        }
    }.toInt()
}

private fun CharGrid.findNext(
    current: Pair<Coordinate, Coordinate>,
    obstruction: Coordinate? = null
): Pair<Coordinate, Coordinate>? {
    var (position, direction) = current
    repeat(4) {
        val nextPosition = position + direction
        val nextObject = this.getOrNull(nextPosition) ?: return null
        if (nextObject == '#' || nextPosition == obstruction) {
            // Turn right
            direction = direction.rotate(RotationDirection.Right)
        } else {
            return nextPosition to direction
        }
    }
    error("Stuck!")
}

private fun CharGrid.recursionHappens(start: Pair<Coordinate, Coordinate>, obstruction: Coordinate): Boolean {
    val seen = mutableSetOf<Pair<Coordinate, Coordinate>>()
    var current = start
    while (true) {
        if (!seen.add(current)) return true
        current = findNext(current, obstruction) ?: return false
    }
}