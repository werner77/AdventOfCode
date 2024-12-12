package com.behindmedia.adventofcode.year2024.day12

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val grid = CharGrid(read("/2024/day12.txt"))

    // Part1
    println(solve(grid, false))

    // Part2
    println(solve(grid, true))
}

private fun solve(
    grid: CharGrid,
    countSides: Boolean
): Long {
    val seen = mutableSetOf<Coordinate>()
    return grid.coordinateRange.sumOf { start ->
        val (area, perimeter) = grid.findRegion(start, seen, countSides)
        area * perimeter
    }
}

private fun CharGrid.findRegion(start: Coordinate, seen: MutableSet<Coordinate>, countSides: Boolean): Pair<Long, Long> {
    if (start in seen) {
        // Already processed
        return 0L to 0L
    }
    val pending = ArrayDeque<Coordinate>()
    pending += start
    val type = this[start]
    var area = 0L
    // Contains the coordinate and the perpendicular direction of the perimeter at that point
    val perimeterPoints = mutableSetOf<Pair<Coordinate, Coordinate>>()
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        if (!seen.add(current)) continue
        area++
        for (neighbour in current.directNeighbours) {
            val neighbourType = this.getOrNull(neighbour)
            if (neighbourType != type) {
                val direction = neighbour - current
                perimeterPoints += current to direction
            } else {
                pending.add(neighbour)
            }
        }
    }
    return area to if (countSides) findSides(perimeterPoints) else perimeterPoints.size.toLong()
}

private fun findSides(perimeterCoordinates: Set<Pair<Coordinate, Coordinate>>): Long {
    val seen = mutableSetOf<Pair<Coordinate, Coordinate>>()
    var result = 0L
    for ((c, d) in perimeterCoordinates) {
        if (!seen.add(c to d)) continue

        // Found a new side
        result++

        val directions = Coordinate.directNeighbourDirections.filter { it.isVertical != d.isVertical }

        // Go in both directions
        for (delta in directions) {
            var current = c
            while (true) {
                current += delta
                if (current to d in perimeterCoordinates) {
                    // Part of same side
                    seen += current to d
                } else {
                    break
                }
            }
        }
    }
    return result
}