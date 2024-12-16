package com.behindmedia.adventofcode.year2024.day15

import com.behindmedia.adventofcode.common.*

private fun Char.toCoordinate(): Coordinate? {
    return when (this) {
        '<' -> Coordinate.left
        '>' -> Coordinate.right
        '^' -> Coordinate.up
        'v' -> Coordinate.down
        '\n' -> null
        else -> error("Unexpected character: $this")
    }
}

private const val visualize = false

fun main() = timing {
    val string = read("/2024/day15.txt")
    val gui: TextWindow? = if (visualize) TextWindow("AdventOfCode 2024 Day 15", 50, 100) else null
    val (mapString, directionsString) = string.splitNonEmpty("\n\n")
    val grid = CharGrid(mapString)
    val directions = directionsString.mapNotNull { it.toCoordinate() }

    // Part1
    println(grid.mutableCopy().simulate(directions, gui))

    // Part2
    println(grid.expandedMutableCopy().simulate(directions, gui))
}

private fun CharGrid.expandedMutableCopy(): MutableCharGrid {
    val grid = MutableCharGrid(sizeX * 2, sizeY) { _, _ ->
        '.'
    }
    for ((c, item) in this) {
        val first = Coordinate(c.x * 2, c.y)
        val second = Coordinate(c.x * 2 + 1, c.y)
        when (item) {
            '.' -> {
                grid[first] = '.'
                grid[second] = '.'
            }

            '@' -> {
                grid[first] = '@'
                grid[second] = '.'
            }

            '#' -> {
                grid[first] = '#'
                grid[second] = '#'
            }

            'O' -> {
                grid[first] = '['
                grid[second] = ']'
            }

            else -> {
                error("Unexpected character: $item")
            }
        }
    }
    return grid
}

private fun MutableCharGrid.simulate(directions: List<Coordinate>, gui: TextWindow?): Int {
    var current = this.single { it.value == '@' }.key
    gui?.setText(this.toString())
    for (d in directions) {
        val moved = this.move(current, d)
        if (gui != null) {
            gui.setText(this.toString())
            Thread.sleep(1000 / 25)
        }
        if (moved) {
            current += d
        }
    }
    return this.checksum
}

private val CharGrid.checksum: Int
    get() {
        var result = 0
        for ((c, item) in this) {
            if (item == '[' || item == 'O') {
                result += 100 * c.y + c.x
            }
        }
        return result
    }

private fun MutableCharGrid.move(current: Coordinate, direction: Coordinate): Boolean {
    // Use a linkedMap explicitly to ensure the insertion order is preserved.
    val seen = linkedMapOf<Coordinate, Boolean>()
    val moved = move(current, direction, seen)
    if (moved) {
        // Commit the moves, the order should be the last encountered in the recursion first
        for (c in seen.keys) {
            val target = c + direction
            val currentItem = this[c]
            this[target] = currentItem
            this[c] = '.'
        }
    }
    return moved
}

private fun MutableCharGrid.move(
    current: Coordinate,
    direction: Coordinate,
    seen: MutableMap<Coordinate, Boolean>
): Boolean {
    return seen.getOrPut(current) {
        val target = current + direction
        when (this[target]) {
            '.' -> {
                // Ok can move, recursion terminates
                true
            }

            '[' -> {
                if (direction == Coordinate.left) {
                    move(target, direction, seen)
                } else {
                    move(target, direction, seen) && move(target + Coordinate.right, direction, seen)
                }
            }

            'O' -> {
                move(target, direction, seen)
            }

            ']' -> {
                if (direction == Coordinate.right) {
                    move(target, direction, seen)
                } else {
                    move(target, direction, seen) && move(target + Coordinate.left, direction, seen)
                }
            }

            '#' -> {
                // Cannot move, Recursion terminates
                false
            }

            else -> {
                error("Unexpected item: ${this[target]}")
            }
        }
    }
}