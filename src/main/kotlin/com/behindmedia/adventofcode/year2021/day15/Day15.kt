package com.behindmedia.adventofcode.year2021.day15

import com.behindmedia.adventofcode.common.*

private fun findMinCostPath(
    start: Coordinate,
    end: Coordinate,
    map: Map<Coordinate, Int>
): Int {
    return shortestWeightedPath(
        from = start,
        neighbours = { c ->
            c.directNeighbours.mapNotNull { n ->
                map[n]?.let { Pair(n, it) }
            }
        },
        process = { if (it.destination == end) it.pathLength else null }
    )?.toInt() ?: error("No path found")
}

private fun expandMap(map: Map<Coordinate, Int>): Map<Coordinate, Int> {
    val sizeX = map.sizeX
    val sizeY = map.sizeY
    val ret = mutableMapOf<Coordinate, Int>()
    for (k in 0 until 5) {
        for (m in 0 until 5) {
            for (y in 0 until sizeY) {
                for (x in 0 until sizeX) {
                    val c = Coordinate(k * sizeX + x, m * sizeY + y)
                    val v = map[Coordinate(x, y)]!!
                    ret[c] = 1 + (v + k + m - 1) % 9
                }
            }
        }
    }
    return ret
}

fun main() {
    val map: Map<Coordinate, Int> = parseLines("/2021/day15.txt") { line ->
        line.map { it - '0' }
    }.foldIndexed(mutableMapOf()) { y, map, value ->
        for ((x, v) in value.withIndex()) {
            map[Coordinate(x, y)] = v
        }
        map
    }

    // Part 1
    println(findMinCostPath(Coordinate.origin, map.maxCoordinate, map))

    val expandedMap = expandMap(map)

    // Part 2
    println(findMinCostPath(Coordinate.origin, expandedMap.maxCoordinate, expandedMap))
}