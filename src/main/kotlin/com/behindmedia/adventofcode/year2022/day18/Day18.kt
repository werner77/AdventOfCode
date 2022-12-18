package com.behindmedia.adventofcode.year2022.day18

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2022/day18.txt") { line ->
        val (x, y, z) = line.split(",")
        Coordinate3D(x.toInt(), y.toInt(), z.toInt())
    }.toSet()
    timing {
        part1(data)
    }
    timing {
        part2(data)
    }
}

private fun part1(data: Set<Coordinate3D>) {
    computeSurfaceArea(data) { _, _, _ ->
        true
    }
}

private fun part2(data: Set<Coordinate3D>) {
    computeSurfaceArea(data) { minCoordinate, maxCoordinate, next ->
        hasPathToOutside(next, data, minCoordinate, maxCoordinate)
    }
}

private fun computeSurfaceArea(data: Set<Coordinate3D>, predicate: (Coordinate3D, Coordinate3D, Coordinate3D) -> Boolean) {
    val minCoordinate = data.minCoordinate
    val maxCoordinate = data.maxCoordinate
    var total = 0
    for (c in data) {
        for (direction in Coordinate3D.allDirections) {
            val next = c + direction
            if (!data.contains(next) && predicate.invoke(minCoordinate, maxCoordinate, next)) {
                total++
            }
        }
    }
    println(total)
}

private fun hasPathToOutside(
    start: Coordinate3D,
    data: Set<Coordinate3D>,
    minCoordinate: Coordinate3D,
    maxCoordinate: Coordinate3D
): Boolean {
    val pending = ArrayDeque<Coordinate3D>()
    pending.add(start)
    val seen = mutableSetOf<Coordinate3D>()
    while (pending.isNotEmpty()) {
        val current = pending.removeFirstOrNull() ?: break
        if (current in seen) {
            continue
        }
        seen += current
        val inside = current.x in minCoordinate.x..maxCoordinate.x &&
                current.y in minCoordinate.y..maxCoordinate.y &&
                current.z in minCoordinate.z..maxCoordinate.z
        if (!inside) {
            return true
        }
        for (d in Coordinate3D.allDirections) {
            val next = current + d
            if (!data.contains(next)) {
                pending += next
            }
        }
    }
    return false
}