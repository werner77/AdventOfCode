package com.behindmedia.adventofcode.year2024.day18

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val coordinates = parseLines("/2024/day18.txt") { line ->
        val (x, y) = readIntsFromString(line)
        Coordinate(x, y)
    }
    val blocks = coordinates.withIndex().associate { it.value to it.index }

    // Part 1
    println(findPath(blocks, 1024))

    // Part 2
    val lastValidTime = binarySearch(0, blocks.size) { current ->
        findPath(blocks, current) != null
    } ?: error("No result found")
    println(coordinates[lastValidTime])
}

private fun findPath(
    blocks: Map<Coordinate, Int>,
    time: Int,
    size: Int = 71
): Int? {
    val start = Coordinate(0, 0)
    val finish = Coordinate(size - 1, size - 1)
    val range = CoordinateRange(start, finish)
    return shortestPath(from = start, neighbours = { path ->
        path.destination.directNeighbours
    }, reachable = { _, position ->
        position in range && (blocks[position] ?: Int.MAX_VALUE) >= time
    }, process = { path ->
        if (path.destination == finish) {
            path.length
        } else {
            null
        }
    })
}