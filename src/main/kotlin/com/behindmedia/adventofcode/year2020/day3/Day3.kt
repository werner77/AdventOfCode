package com.behindmedia.adventofcode.year2020.day3

import com.behindmedia.adventofcode.common.*

fun main() {
    val grid = CharGrid(read("/2020/day3.txt"))
    println(
        process(
            listOf(Coordinate(3, 1)),
            grid
        ))
    println(
        process(
            listOf(Coordinate(1, 1), Coordinate(3, 1), Coordinate(5, 1), Coordinate(7, 1), Coordinate(1, 2)),
            grid
        ))
}

private fun process(
    slopes: List<Coordinate>,
    grid: CharGrid
): Long {
    var total = 1L
    for (slope in slopes) {
        var current = Coordinate.origin
        var ans = 0
        while (current.y <= grid.maxY) {
            val value = grid[current.copy(x = current.x % grid.sizeX)]
            if (value == '#') ans++
            current += slope
        }
        total *= ans.toLong()
    }
    return total
}