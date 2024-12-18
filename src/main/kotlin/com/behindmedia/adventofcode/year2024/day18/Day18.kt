package com.behindmedia.adventofcode.year2024.day18

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() = timing {
    val size = 71
    val grid = MutableCharGrid(size) { _, _ -> '.' }
    val data = parseLines("/2024/day18.txt") { line ->
        val (x, y) = line.splitNonEmpty(",").map { it.toInt() }
        Coordinate(x, y)
    }
    val start = Coordinate(0, 0)
    val finish = Coordinate(size - 1, size - 1)
    for ((i, c) in data.withIndex()) {
        grid[c] = '#'
        val minLength = findPath(start, finish, grid)
        if (i == 1024 - 1) {
            // Part 1
            println(minLength)
        } else if (minLength == null) {
            // Part 2
            println(c)
            break
        }
    }
}

private fun findPath(
    start: Coordinate,
    finish: Coordinate,
    grid: MutableCharGrid
): Int? {
    val pending = ArrayDeque<Pair<Coordinate, Int>>()
    pending += start to 0
    val seen = mutableSetOf<Coordinate>()
    while (pending.isNotEmpty()) {
        val (next, length) = pending.removeFirst()
        if (next == finish) {
            // Found location
            return length
        }
        if (!seen.add(next)) {
            continue
        }
        for (neighbor in next.directNeighbours) {
            if (grid.getOrNull(neighbor) == '.') {
                pending += Pair(neighbor, length + 1)
            }
        }
    }
    return null
}