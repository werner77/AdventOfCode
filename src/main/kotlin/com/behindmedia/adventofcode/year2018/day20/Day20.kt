package com.behindmedia.adventofcode.year2018.day20

import com.behindmedia.adventofcode.common.*
import kotlin.math.max

private val directionMap = mapOf(
    'N' to Coordinate.up,
    'S' to Coordinate.down,
    'E' to Coordinate.right,
    'W' to Coordinate.left
)

private fun parse(string: String, startIndex: Int, startCoordinates: List<Coordinate>, onVisit: (Coordinate, Coordinate) -> Unit): Pair<Set<Coordinate>, Int> {
    var currentCoordinates = startCoordinates.toMutableList()
    val endCoordinates = mutableSetOf<Coordinate>()
    var i = startIndex
    while (i < string.length) {
        when (val c = string[i++]) {
            '(' -> {
                // Parses up to the next closing parenthesis
                val (nextCoordinates, j) = parse(string, i, currentCoordinates, onVisit)
                i = j
                // Replace current coordinates with the next coordinates
                currentCoordinates.clear()
                currentCoordinates.addAll(nextCoordinates)
            }
            '|' -> {
                endCoordinates += currentCoordinates
                // Restart with the start coordinates
                currentCoordinates = startCoordinates.toMutableList()
            }
            ')' -> {
                endCoordinates += currentCoordinates
                return Pair(endCoordinates, i)
            }
            '^' -> {
                // ignore
            }
            '$' -> {
                // Finish
                return Pair(currentCoordinates.toSet(), i)
            }
            else -> {
                for (j in 0 until currentCoordinates.size) {
                    val direction = directionMap[c] ?: error("Unknown direction $c")
                    val old = currentCoordinates[j]
                    currentCoordinates[j] += direction
                    onVisit(old, currentCoordinates[j])
                }
            }
        }
    }
    error("Should not reach this line")
}

fun main() {
    val input = read("/2018/day20.txt").trim()
    val start = Coordinate.origin
    val map = mutableMapOf<Coordinate, Char>()
    map[start] = 'X'
    parse(input, 0, listOf(Coordinate(0, 0))) { old, new ->
        val direction = new - old
        map[new * 2] = '.'
        map[new * 2 - direction] = if (direction.x == 0) '-' else '|'
    }
    map.printMap(default = '#', includeBorder = true)
    var maxDistance = 0
    var roomCount = 0
    shortestPath(
        from = start,
        neighbours = { path -> path.destination.directNeighbours },
        reachable = { _, coordinate ->
            (map[coordinate] ?: '#') != '#'
        },
        process = {
            val distance = it.length / 2
            maxDistance = max(maxDistance, distance)
            if (distance >= 1000 && map[it.destination] == '.') {
                roomCount++
            }
            null
        })

    // Part1
    println(maxDistance)

    // Part2
    println(roomCount)
}
