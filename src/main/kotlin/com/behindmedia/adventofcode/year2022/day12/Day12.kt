package com.behindmedia.adventofcode.year2022.day12

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parseMap("/2022/day12.txt") { it }

    val start = data.entries.first { it.value == 'S' }.key
    val finish = data.entries.first { it.value == 'E' }.key

    // Part 1
    solve(data, finish, listOf(start))

    // Part 2
    solve(data, finish, data.filter { it.value == 'a' }.map { it.key })
}

private val Char.height: Int
    get() = when (this) {
        'S' -> 0
        'E' -> 25
        else -> this - 'a'
    }

private fun solve(map: Map<Coordinate, Char>, destination: Coordinate, startCoordinates: List<Coordinate>) {
    var shortest = Int.MAX_VALUE
    for (start in startCoordinates) {
        val path = shortestPath(
            from = start,
            neighbours = { it.destination.directNeighbourSequence() },
            reachable = { current, next ->
                val currentHeight = map[current.destination]?.height ?: error("Expected current height to be defined")
                val nextHeight = map[next]?.height
                (nextHeight != null && nextHeight <= currentHeight + 1)
            },
            process = { path ->
                if (path.destination == destination) path else null
            }
        ) ?: continue
        val pathLength = path.pathLength
        shortest = min(shortest, pathLength)
    }
    println(shortest)
}