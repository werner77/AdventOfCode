package com.behindmedia.adventofcode.year2022.day14

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data: List<List<Coordinate>> = parseLines("/2022/day14.txt") { line ->
        line.splitNonEmptySequence(" ", "-", ">", ",")
            .map { it.toInt() }
            .chunked(2)
            .map { (x, y) -> Coordinate(x, y) }
            .toList()
    }
    val source = Coordinate(500, 0)
    val map: MutableMap<Coordinate, Char> = data.fold(mutableMapOf(source to '+')) { map, instructions ->
        map.apply {
            drawLine(map, instructions)
        }
    }
    timing {
        part1(map, source)
        part2(map, source)
    }
}

private fun part1(mapIn: Map<Coordinate, Char>, source: Coordinate) {
    val map = mapIn.toMutableMap()
    val maxCoordinateY = map.keys.maxOf { it.y }
    while (true) {
        simulate(map, source, maxCoordinateY) ?: break
    }
    println(map.values.count { it == 'o' })
}


private fun part2(mapIn: Map<Coordinate, Char>, source: Coordinate) {
    val map = mapIn.toMutableMap()
    val maxCoordinateY = map.keys.maxOf { it.y }
    val floorCoordinateY = maxCoordinateY + 2
    while (true) {
        val nextCoordinate = simulate(map, source, floorCoordinateY) {
            !map.containsKey(it) && it.y < floorCoordinateY
        } ?: error("No value found")
        if (nextCoordinate == source) break
    }
    println(map.values.count { it == 'o' })
}

// Simulates one grain of sane
private fun simulate(map: MutableMap<Coordinate, Char>, source: Coordinate, maxCoordinateY: Int, reachable: (Coordinate) -> Boolean = { !map.containsKey(it) }): Coordinate? {
    var coordinate = source
    while (coordinate.y < maxCoordinateY) {
        var foundDirection = false
        for (direction in listOf(Coordinate.down, Coordinate.downLeft, Coordinate.downRight)) {
            if (reachable(coordinate + direction)) {
                coordinate += direction
                foundDirection = true
                break
            }
        }
        if (!foundDirection) {
            map[coordinate] = 'o'
            return coordinate
        }
    }
    return null
}

private fun drawLine(map: MutableMap<Coordinate, Char>, line: List<Coordinate>) {
    for (i in 0 until line.size - 1) {
        val start = line[i]
        val end = line[i + 1]
        for (x in min(start.x, end.x)..max(start.x, end.x)) {
            for (y in min(start.y, end.y)..max(start.y, end.y)) {
                map[Coordinate(x, y)] = '#'
            }
        }
    }
}