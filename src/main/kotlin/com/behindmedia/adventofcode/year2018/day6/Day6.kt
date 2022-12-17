package com.behindmedia.adventofcode.year2018.day6

import com.behindmedia.adventofcode.common.*

private data class Location(val identifier: Int, val coordinate: Coordinate)

fun main() {
    var index = 1
    val locations = parseLines("/2018/day6.txt") { line ->
        val (x, y) = line.splitNonEmptySequence(" ", ",").map { it.toInt() }.toList()
        Location(index++, Coordinate(x, y))
    }

    val minX = locations.minOf { it.coordinate.x }
    val minY = locations.minOf { it.coordinate.y }
    val maxX = locations.maxOf { it.coordinate.x }
    val maxY = locations.maxOf { it.coordinate.y }
    val minCoordinate = Coordinate(minX, minY) - Coordinate(100, 100)
    val maxCoordinate = Coordinate(maxX, maxY) + Coordinate(100, 100)
    val map = mutableMapOf<Coordinate, Int>()
    for (coordinate in CoordinateRange(minCoordinate, maxCoordinate)) {
        // Find the closest coordinate
        val minLocations = locations.allMinBy { it.coordinate.manhattenDistance(coordinate) }
        require(minLocations.size > 0)
        if (minLocations.size == 1) {
            map[coordinate] = minLocations.first().identifier
        } else {
            map[coordinate] = 0
        }
    }

    val minX1 = map.keys.minOf { it.x }
    val minY1 = map.keys.minOf { it.y }
    val maxX1 = map.keys.maxOf { it.x }
    val maxY1 = map.keys.maxOf { it.y }
    val edgeValues = map.filter { it.key.x in setOf(minX1, maxX1) && it.key.y in setOf(minY1, maxY1) }.map { it.value }.toSet()

    var maxArea = 0
    for (i in 1 until index) {
        if (i in edgeValues) continue
        val area = map.values.count { it == i }
        if (area > maxArea) {
            maxArea = area
        }
    }
    println(maxArea)
}

private fun List<Location>.allMinBy(selector: (Location) -> Int): List<Location> {
    val result = mutableListOf<Location>()
    var currentMin = Int.MAX_VALUE
    for (l in this) {
        val value = selector.invoke(l)
        if (value < currentMin) {
            result.clear()
            currentMin = value
            result += l
        } else if (value == currentMin) {
            result += l
        }
    }
    return result
}