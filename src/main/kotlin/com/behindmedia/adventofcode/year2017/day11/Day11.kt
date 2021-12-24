package com.behindmedia.adventofcode.year2017.day11

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private val directionsMap: Map<String, Coordinate3D> = mapOf(
    "n" to Coordinate3D(1, 0, -1),
    "ne" to Coordinate3D(1, -1, 0),
    "se" to Coordinate3D(0, -1, 1),
    "s" to Coordinate3D(-1, 0, 1),
    "sw" to Coordinate3D(-1, 1, 0),
    "nw" to Coordinate3D(0, 1, -1),
)

private fun path(start: Coordinate3D, destination: Coordinate3D): Int {
    return shortestPath(
        start,
        { path -> directionsMap.values.map { path.destination + it } },
        reachable = { true },
        { if (it.destination == destination) it.pathLength else null })?.toInt() ?: error("No path found")
}

fun main() {
    val data = parse("/2017/day11.txt") { line ->
        line.trim().split(",").map { directionsMap[it] ?: error("Could not find direction: $it") }
    }

    val start = Coordinate3D.origin
    var current = start
    var farthest = 0

    for (direction in data) {
        current += direction
        farthest = max(farthest, current.manhattenDistance(start) / 2)
    }

    println(current.manhattenDistance(start) / 2)
    println(farthest)
}