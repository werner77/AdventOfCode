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

val gui: TextWindow? = null
//val gui: TextWindow? = TextWindow("AdventOfCode 2024 Day 15", 50, 100)

fun main() {
    val string = read("/2024/day15.txt")

    val (mapString, directionsString) = string.splitNonEmpty("\n\n")
    val originalGrid = CharGrid(mapString)
    val directions = directionsString.mapNotNull { it.toCoordinate() }

    println(part1(originalGrid, directions))
    println(part2(originalGrid, directions))
}

private fun part1(
    originalGrid: CharGrid,
    directions: List<Coordinate>
): Int {
    val grid = originalGrid.mutableCopy()
    return grid.simulate(directions) { current, direction ->
        grid.move1(current, direction)
    }
}

private fun part2(
    originalGrid: CharGrid,
    directions: List<Coordinate>
): Int {
    val grid = MutableCharGrid(originalGrid.sizeX *2, originalGrid.sizeY) {  _, _ ->
        '.'
    }
    for ((c, item) in originalGrid) {
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
    return grid.simulate(directions) { current, direction ->
        grid.move2(current, direction)
    }
}

private fun CharGrid.simulate(directions: List<Coordinate>, move: (Coordinate, Coordinate) -> Boolean): Int {
    val grid = this
    var current = grid.single { it.value == '@' }.key
    gui?.setText(grid.toString())
    for (d in directions) {
        val moved = move(current, d)
        if (gui != null) {
            gui.setText(grid.toString())
            Thread.sleep(1000 / 25)
        }
        if (moved) {
            current += d
        }
    }
    var result = 0
    for ((c, item) in grid) {
        if (item == '[' || item == 'O') {
            result += 100 * c.y + c.x
        }
    }
    return result
}

private fun MutableCharGrid.move1(current: Coordinate, direction: Coordinate): Boolean {
    val target = current + direction
    val currentItem = this[current]
    val moved = when (this[target]) {
        '.' -> {
            // Ok can move, recursion terminates
            true
        }

        'O' -> {
            move1(target, direction)
        }

        '#' -> {
            // Cannot move, Recursion terminates
            false
        }

        else -> {
            error("Unexpected item: ${this[target]}")
        }
    }
    if (moved) {
        this[target] = currentItem
        this[current] = '.'
    }
    return moved
}

private fun MutableCharGrid.move2(current: Coordinate, direction: Coordinate): Boolean {
    val seen = mutableMapOf<Coordinate, Boolean>()
    val moved = move2(current, direction, seen)
    if (moved) {
        // Commit the move
        for (c in seen.keys) {
            val target = c + direction
            val currentItem = this[c]
            this[target] = currentItem
            this[c] = '.'
        }
    }
    return moved
}

private fun MutableCharGrid.move2(current: Coordinate, direction: Coordinate, seen: MutableMap<Coordinate, Boolean>): Boolean {
    return seen.getOrPut(current) {
        val target = current + direction
        when (this[target]) {
            '.' -> {
                // Ok can move, recursion terminates
                true
            }

            '[' -> {
                if (direction == Coordinate.left) {
                    move2(target, direction, seen)
                } else {
                    move2(target, direction, seen) && move2(target + Coordinate.right, direction, seen)
                }
            }

            ']' -> {
                if (direction == Coordinate.right) {
                    move2(target, direction, seen)
                } else {
                    move2(target, direction, seen) && move2(target + Coordinate.left, direction, seen)
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