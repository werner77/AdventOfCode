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
    part2(map, directions)
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
    solve(map, directions) { c, direction ->
        val coordinate = c + direction
        val nextCoordinate = when (direction) {
            Coordinate.up -> Coordinate(coordinate.x, yBoundaries[coordinate.x]!!.second)
            Coordinate.down -> Coordinate(coordinate.x, yBoundaries[coordinate.x]!!.first)
            Coordinate.left -> Coordinate(xBoundaries[coordinate.y]!!.second, coordinate.y)
            Coordinate.right -> Coordinate(xBoundaries[coordinate.y]!!.first, coordinate.y)
            else -> error("Invalid direction")
        }
        Pair(nextCoordinate, direction)
    }
}

private fun part2(
    map: Map<Coordinate, Char>,
    directions: List<Direction>
) {
    val size = max(map.sizeX / 4, map.sizeY / 4)
    val mappings = mutableListOf<CoordinateDirectionMapping>()

    // This maps the sides of the quadrants, there are 16 in total, of which only 6 are occupied
    // We have to map 7 sides back and forth
    // Invert means that the perpendicular coordinate is inverted in the edge mapping

    // Map all the sides
    mappings.addForwardAndBackMappings(
        row = 0,
        column = 1,
        direction = Coordinate.up,
        toRow = 3,
        toColumn = 0,
        toDirection = Coordinate.right,
        invert = false,
        size = size
    ) // A

    mappings.addForwardAndBackMappings(
        row = 1,
        column = 1,
        direction = Coordinate.left,
        toRow = 2,
        toColumn = 0,
        toDirection = Coordinate.down,
        invert = false,
        size = size
    ) // B

    mappings.addForwardAndBackMappings(
        row = 0,
        column = 1,
        direction = Coordinate.left,
        toRow = 2,
        toColumn = 0,
        toDirection = Coordinate.right,
        invert = true,
        size = size
    ) // C


    mappings.addForwardAndBackMappings(
        row = 0,
        column = 2,
        direction = Coordinate.up,
        toRow = 3,
        toColumn = 0,
        toDirection = Coordinate.up,
        invert = false,
        size = size
    ) // D

    mappings.addForwardAndBackMappings(
        row = 0,
        column = 2,
        direction = Coordinate.right,
        toRow = 2,
        toColumn = 1,
        toDirection = Coordinate.left,
        invert = true,
        size = size
    ) // E

    mappings.addForwardAndBackMappings(
        row = 2,
        column = 1,
        direction = Coordinate.down,
        toRow = 3,
        toColumn = 0,
        toDirection = Coordinate.left,
        invert = false,
        size = size
    ) // F

    mappings.addForwardAndBackMappings(
        row = 0,
        column = 2,
        direction = Coordinate.down,
        toRow = 1,
        toColumn = 1,
        toDirection = Coordinate.left,
        invert = false,
        size = size
    ) // G

    solve(map, directions) { coordinate, direction ->
        // Find the mapping for this edge, there should be exactly one
        mappings.asSequence().mapNotNull { it.invoke(coordinate, direction) }.singleOrNull() ?: error("No mapping found")
    }
}

private fun solve(
    map: Map<Coordinate, Char>,
    directions: List<Direction>,
    wrapAround: (Coordinate, Coordinate) -> Pair<Coordinate, Coordinate>
) {
    val start = map.entries.first { it.value == '.' }.key
    val state = State(start, Coordinate.right)
    for (direction in directions) {
        for (k in 0 until direction.amount) {
            var c = state.coordinate + state.direction
            var d = state.direction
            var h = map[c]
            if (h == null || h == ' ') {
                val wrapped = wrapAround(state.coordinate, state.direction)
                c = wrapped.first
                d = wrapped.second
                h = map[c]
                require(h != null && h != ' ')
            }
            if (h == '#') {
                // Wall, just stop
                break
            } else if (h == '.') {
                // Move
                state.coordinate = c
                state.direction = d
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

private typealias CoordinateDirectionMapping = (Coordinate, Coordinate) -> Pair<Coordinate, Coordinate>?

private fun MutableList<CoordinateDirectionMapping>.addMapping(
    quadrantRow: Int,
    quadrantColumn: Int,
    direction: Coordinate,
    toQuadrantRow: Int,
    toQuadrantColumn: Int,
    toDirection: Coordinate,
    invert: Boolean,
    quadrantSize: Int
) {
    val quadrantRange = CoordinateRange(Coordinate.origin, quadrantSize, quadrantSize)
    val mapping: CoordinateDirectionMapping = { inputCoordinate, inputDirection ->
        // direction should match
        var targetCoordinate: Coordinate? = null
        if (inputDirection == direction) {
            val normalizedStartCoordinate = inputCoordinate - Coordinate(quadrantColumn * quadrantSize, quadrantRow * quadrantSize)
            val normalizedEndCoordinate = normalizedStartCoordinate + direction
            if (normalizedStartCoordinate in quadrantRange && normalizedEndCoordinate !in quadrantRange) {
                // Both x and y are in range, and target x1 and y2 are not in range, which means we are going over the edge
                // The source value is the value of the coordinate that did not go out of range, which is where
                var mappedValue = if (normalizedStartCoordinate.x == normalizedEndCoordinate.x) normalizedStartCoordinate.x
                        else if (normalizedStartCoordinate.y == normalizedEndCoordinate.y) normalizedStartCoordinate.y
                        else error("Should not be true")
                if (invert) {
                    mappedValue = quadrantSize - 1 - mappedValue
                }
                targetCoordinate = when (toDirection) {
                    Coordinate.down -> {
                        // Coming in from the top side, y == constant to min value of quadrant
                        Coordinate(toQuadrantColumn * quadrantSize + mappedValue, toQuadrantRow * quadrantSize)
                    }
                    Coordinate.up -> {
                        // Coming in from the bottom side, y == constant to max value of quadrant
                        Coordinate(toQuadrantColumn * quadrantSize + mappedValue, (toQuadrantRow + 1) * quadrantSize - 1)
                    }
                    Coordinate.right -> {
                        // Coming in from the left side, x == constant to min value of quadrant
                        Coordinate(toQuadrantColumn * quadrantSize, toQuadrantRow * quadrantSize + mappedValue)
                    }
                    Coordinate.left -> {
                        // Coming in from the right side, x == constant to max value of quadrant
                        Coordinate((toQuadrantColumn + 1) * quadrantSize - 1, toQuadrantRow * quadrantSize + mappedValue)
                    }
                    else -> error("Invalid direction")
                }
            }
        }
        targetCoordinate?.let { Pair(it, toDirection) }
    }
    this += mapping
}

private fun MutableList<CoordinateDirectionMapping>.addForwardAndBackMappings(
    row: Int,
    column: Int,
    direction: Coordinate,
    toRow: Int,
    toColumn: Int,
    toDirection: Coordinate,
    invert: Boolean,
    size: Int
) {
    addMapping(row, column, direction, toRow, toColumn, toDirection, invert, size)
    addMapping(toRow, toColumn, toDirection.inverted(), row, column, direction.inverted(), invert, size)
}