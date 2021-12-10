package com.behindmedia.adventofcode.year2021.day9

import com.behindmedia.adventofcode.common.*

private fun expandBasin(start: Coordinate, map: Map<Coordinate, Int>): Int {
    var size = 0
    val seen = mutableSetOf(start)
    val candidates = ArrayDeque(seen)
    while (candidates.isNotEmpty()) {
        val c = candidates.popFirst() ?: break
        size++
        for (d in c.directNeighbours) {
            val h = map[d]
            if (h == null || h == 9 || d in seen) continue
            candidates += d
            seen += d
        }
    }
    return size
}

fun main() {
    val data = parseLines("/2021/day9.txt") { line ->
        line.map { it - '0' }
    }
    val sizeX = data.maxOf { it.size }
    val sizeY = data.size
    val map = mutableMapOf<Coordinate, Int>()
    for (y in 0 until sizeY) {
        for (x in 0 until sizeX) {
            map[Coordinate(x, y)] = data[y][x]
        }
    }

    val lowPoints = map.entries.filter { entry ->
        entry.key.directNeighbours.all { coordinate ->
            map[coordinate]?.let { it > entry.value } ?: true
        }
    }

    timing {
        // Part 1
        println(lowPoints.fold(0) { count, entry -> count + entry.value + 1 })

        // Part 2
        val basinSums = lowPoints.fold(mutableListOf<Int>()) { list, entry ->
            list.apply { this += expandBasin(entry.key, map) }
        }.sortedDescending()
        println(basinSums.subList(0, 3).fold(1) { product, sum -> product * sum })
    }
}