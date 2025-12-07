package com.behindmedia.adventofcode.year2025.day7

import com.behindmedia.adventofcode.common.*

// The number of ways to reach splitter n is the
private fun wayCount(grid: CharGrid, coordinate: Coordinate, cache: MutableMap<Coordinate, Long>, unique: Boolean): Long {
    val value = grid.getOrNull(coordinate) ?: return 1L
    val cached = cache[coordinate]
    if (cached != null) return if (unique) 1L else cached
    return when (value) {
        '^' -> {
            // Split, count two ways
            val left = coordinate + Coordinate.left
            val right = coordinate + Coordinate.right
            wayCount(grid, left, cache, unique) + wayCount(grid, right, cache, unique)
        }
        '.', 'S' -> {
            wayCount(grid, coordinate + Coordinate.down, cache, unique)
        }
        else -> {
            error("Invalid value $value")
        }
    }.also { cache[coordinate] = it }
}

private fun solve(fileName: String, part: Int) {
    val data = read("/2025/$fileName")
    val grid = CharGrid(data).mutableCopy()
    val start = grid.coordinateRange.first { grid[it] == 'S' }
    val count = wayCount(grid, start, mutableMapOf(), part == 1)
    if (part == 1) println(count - 1) else println(count)
}

fun main() {
    for (part in 1..2) {
        solve("day7-sample1.txt", part)
        solve("day7.txt", part)
    }
}
