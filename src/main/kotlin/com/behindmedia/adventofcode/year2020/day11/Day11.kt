package com.behindmedia.adventofcode.year2020.day11

import com.behindmedia.adventofcode.common.*

fun main() {
    val grid = CharGrid(read("/2020/day11.txt"))
    println(findUnchangedState(grid) { process1(it) })
    println(findUnchangedState(grid) { process2(it) })
}

private fun findUnchangedState(grid: CharGrid, process: (CharGrid) -> CharGrid): Int {
    var current = grid
    while (true) {
        val next = process(current)
        if (next == current) {
            return next.count { it.value == '#' }
        }
        current = next
    }
}

private fun process1(grid: CharGrid): CharGrid {
    return CharGrid(grid.sizeX, grid.sizeY) { x, y ->
        val c = Coordinate(x, y)
        val value = grid[c]
        val occupied = c.allNeighbours.count { grid.getOrNull(it) == '#' }
        if (value == 'L' && occupied == 0) {
            '#'
        } else if (value == '#' && occupied >= 4) {
            'L'
        } else {
            value
        }
    }
}

private fun process2(grid: CharGrid): CharGrid {
    return CharGrid(grid.sizeX, grid.sizeY) { x, y ->
        val c = Coordinate(x, y)
        val value = grid[c]
        val occupied = Coordinate.allNeighbourDirections.count { direction ->
            var found = false
            var c1 = c
            while (true) {
                c1 += direction
                when(grid.getOrNull(c1)) {
                    '#' -> {
                        found = true
                        break
                    }
                    '.' -> continue
                    else -> break
                }
            }
            found
        }
        if (value == 'L' && occupied == 0) {
            '#'
        } else if (value == '#' && occupied >= 5) {
            'L'
        } else {
            value
        }
    }
}