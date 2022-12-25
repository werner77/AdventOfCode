package com.behindmedia.adventofcode.year2018.day6

import com.behindmedia.adventofcode.common.*
import kotlin.math.max

private data class Location(val identifier: Int, val coordinate: Coordinate)

fun main() {
    var index = 1
    val locations = parseLines("/2018/day6.txt") { line ->
        val (x, y) = line.splitNonEmptySequence(" ", ",").map { it.toInt() }.toList()
        Location(index++, Coordinate(x, y))
    }
    val coordinateRange = locations.map { it.coordinate }.range()
    part1(coordinateRange, locations)
    part2(coordinateRange, locations)
}
private fun part1(
    coordinateRange: CoordinateRange,
    locations: List<Location>
) {
    val map = mutableMapOf<Coordinate, Int>()
    for (c in coordinateRange) {
        // Find the nearest location
        val l = nearestLocation(c, locations)
        if (l != null) {
            map[c] = l.identifier
        } else {
            // This designates an equal distance coordinate, null means outside
            map[c] = 0
        }
    }
    var maxValue = 0
    for (identifier in locations.map { it.identifier }) {
        // Non-infinite means a location is fully surrounded by zeros
        val area = findNonInfiniteArea(identifier, map)
        if (area != null) {
            maxValue = max(maxValue, area)
        }
    }
    println(maxValue)
}

private fun part2(
    coordinateRange: CoordinateRange,
    locations: List<Location>
) {
    val map2 = mutableMapOf<Coordinate, Int>()
    for (c in coordinateRange) {
        var totalDistance = 0
        for (l in locations) {
            val distance = c.manhattenDistance(l.coordinate)
            totalDistance += distance
        }
        map2[c] = totalDistance
    }
    println(findAreaWithValueLessThan(10000, map2))
}

private fun findNonInfiniteArea(identifier: Int, map: Map<Coordinate, Int>): Int? {
    // Find any coordinate with value == identifier
    val initial = map.entries.firstOrNull { it.value == identifier }?.key ?: return null
    var size = 0
    val foundPath = shortestPath(
        from = initial,
        neighbours = { path ->
            path.destination.directNeighbours
        },
        reachable = { _, coordinate ->
            map[coordinate] == identifier || map[coordinate] == null
        },
        process = { path ->
            if (map[path.destination] == null) {
                true
            } else {
                size++
                null
            }
        }
    ) ?: false
    return if (!foundPath) {
        size
    } else {
        null
    }
}

private fun findAreaWithValueLessThan(maxValue: Int, map: Map<Coordinate, Int>): Int? {
    // Find any coordinate with value == identifier
    val initial = map.entries.firstOrNull { it.value < maxValue }?.key ?: return null
    var size = 0
    shortestPath(
        from = initial,
        neighbours = { path ->
            path.destination.directNeighbours
        },
        reachable = { _, coordinate ->
            map[coordinate]?.let { it <= maxValue } ?: false
        },
        process = {
            size++
            null
        }
    )
    return size
}

private fun nearestLocation(coordinate: Coordinate, locations: List<Location>): Location? {
    var minDistance = Int.MAX_VALUE
    var foundLocation: Location? = null
    for (location in locations) {
        val distance = coordinate.manhattenDistance(location.coordinate)
        if (distance < minDistance) {
            minDistance = distance
            foundLocation = location
        } else if (distance == minDistance) {
            foundLocation = null
        }
    }
    return foundLocation
}