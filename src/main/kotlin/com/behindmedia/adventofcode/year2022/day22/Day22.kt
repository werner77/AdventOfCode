package com.behindmedia.adventofcode.year2022.day22

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class State(var coordinate: Coordinate, var direction: Coordinate)
data class Direction(val amount: Int, val rotation: RotationDirection?)

fun main() {
    val data = read("/2022/day22.txt")
    val (mapString, directionString) = data.split("\n\n")
    val map = parseMapFromString(mapString) {
        it
    }
    val directions = parseDirections(directionString)
    part1(map, directions)
}

private fun parseDirections(directionString: String): List<Direction> {
    val directions = mutableListOf<Direction>()
    var i = 0
    while (i < directionString.length) {
        var amount = 0
        while (i < directionString.length) {
            val k = directionString[i] - '0'
            if (k > 10) break
            amount *= 10
            amount += k
            i++
        }
        require(amount > 0)
        val rotation: RotationDirection? = if (i < directionString.length) {
            val d = directionString[i++]
            when (d) {
                'L' -> RotationDirection.Left
                'R' -> RotationDirection.Right
                else -> error(
                    "Invalid direction: ${directionString[i]}"
                )
            }
        } else {
            null
        }
        directions += Direction(amount, rotation)
    }
    return directions
}

private fun part1(
    map: Map<Coordinate, Char>,
    directions: List<Direction>
) {
    val xBoundaries = mutableMapOf<Int, Pair<Int, Int>>()
    val yBoundaries = mutableMapOf<Int, Pair<Int, Int>>()
    for ((c, v) in map.entries) {
        if (v != ' ') {
            yBoundaries[c.x] =
                (yBoundaries[c.x] ?: Pair(Int.MAX_VALUE, Int.MIN_VALUE)).let {
                    Pair(min(it.first, c.y), max(it.second, c.y))
                }
            xBoundaries[c.y] =
                (xBoundaries[c.y] ?: Pair(Int.MAX_VALUE, Int.MIN_VALUE)).let {
                    Pair(min(it.first, c.x), max(it.second, c.x))
                }
        }
    }
    solve(map, directions) { coordinate, direction ->
        when (direction) {
            Coordinate.up -> Coordinate(coordinate.x, yBoundaries[coordinate.x]!!.second)
            Coordinate.down -> Coordinate(coordinate.x, yBoundaries[coordinate.x]!!.first)
            Coordinate.left -> Coordinate(xBoundaries[coordinate.y]!!.second, coordinate.y)
            Coordinate.right -> Coordinate(xBoundaries[coordinate.y]!!.first, coordinate.y)
            else -> error("Invalid direction")
        }
    }
}

private fun solve(
    map: Map<Coordinate, Char>,
    directions: List<Direction>,
    wrapAround: (Coordinate, Coordinate) -> Coordinate
) {
    val start = map.entries.first { it.value == '.' }.key
    val state = State(start, Coordinate.right)
    for (direction in directions) {
        for (k in 0 until direction.amount) {
            var c = state.coordinate + state.direction
            var h = map[c]
            if (h == null || h == ' ') {
                c = wrapAround(c, state.direction)
                h = map[c]
            }
            if (h == '#') {
                // Wall, just stop
                break
            } else if (h == '.') {
                // Move
                state.coordinate = c
            }
        }
        direction.rotation?.let {
            state.direction = state.direction.rotate(it)
        }
    }
    val directionValue = when (state.direction) {
        Coordinate.right -> 0
        Coordinate.down -> 1
        Coordinate.left -> 2
        Coordinate.up -> 3
        else -> error("Invalid direction")
    }

    println("Final coordinate: ${state.coordinate}")
    println("Final direction: ${state.direction}")

    val ans = 4 * (state.coordinate.x + 1) + 1000 * (state.coordinate.y + 1) + directionValue
    println(ans)
}
