package com.behindmedia.adventofcode.year2022.day18

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2022/day18.txt") { line ->
        val (x, y, z) = line.split(",")
        Coordinate3D(x.toLong(), y.toLong(), z.toLong())
    }.toSet()
    timing {
        part1(data)
    }
    timing {
        part2(data)
    }
}

private fun part1(data: Set<Coordinate3D>) {
    computeSurfaceArea(data) { _, _ ->
        true
    }
}

private fun part2(data: Set<Coordinate3D>) {
    computeSurfaceArea(data) { range, next ->
        hasPathToOutside(next, data, range)
    }
}

private fun computeSurfaceArea(data: Set<Coordinate3D>, predicate: (CoordinateRange3D, Coordinate3D) -> Boolean) {
    val range = data.range()
    var total = 0
    for (c in data) {
        for (direction in Coordinate3D.allDirections) {
            val next = c + direction
            if (!data.contains(next) && predicate.invoke(range, next)) {
                total++
            }
        }
    }
    println(total)
}

private fun hasPathToOutside(
    start: Coordinate3D,
    data: Set<Coordinate3D>,
    range: CoordinateRange3D
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
        if (current !in range) {
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