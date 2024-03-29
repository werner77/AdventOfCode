package com.behindmedia.adventofcode.year2016.day17

import com.behindmedia.adventofcode.common.*

private const val INPUT = "njfxhljp"
private const val GRID_SIZE = 4

private val Char.isAccessible: Boolean
    get() = this in 'b'..'f'

private val charMap = mapOf<Coordinate, Char>(
    Coordinate.up to 'U',
    Coordinate.down to 'D',
    Coordinate.left to 'L',
    Coordinate.right to 'R',
)

data class PathCoordinate(val coordinate: Coordinate, val directions: String) {
    fun reachableCoordinates(passcode: String): List<PathCoordinate> {
        return list(4) {
            val modifiedPasscode = passcode + directions
            val hash = md5(modifiedPasscode)
            for ((i, direction) in Coordinate.directNeighbourDirections.withIndex()) {
                if (hash[i].isAccessible) add(PathCoordinate(coordinate + direction, directions + charMap[direction]))
            }
        }
    }
}

private fun findPossiblePaths(): List<Path<PathCoordinate>> {
    val start = PathCoordinate(Coordinate.origin, "")
    val passcode = INPUT
    val end = Coordinate(GRID_SIZE - 1, GRID_SIZE - 1)
    val possiblePaths = mutableListOf<Path<PathCoordinate>>()
    shortestPath(
        from = start,
        neighbours = { path ->
            if (path.destination.coordinate == end) {
                emptyList()
            } else {
                path.destination.reachableCoordinates(passcode)
            }
        },
        reachable = { _, it -> it.coordinate.x in 0 until GRID_SIZE && it.coordinate.y in 0 until GRID_SIZE },
        process = {
            if (it.destination.coordinate == end) {
                // Candidate
                possiblePaths.add(it)
            }
            null
        }
    )
    return possiblePaths
}

fun main() {
    val possiblePaths = findPossiblePaths()

    // Part1
    println(possiblePaths.first().destination.directions)

    // Part2
    println(possiblePaths.last().length)
}

