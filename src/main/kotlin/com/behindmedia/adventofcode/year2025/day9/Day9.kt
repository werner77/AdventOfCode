package com.behindmedia.adventofcode.year2025.day9

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun IntRange.compare(pos: Int): Int {
    return if (pos <= this.first) {
        -1
    } else if (pos < this.last) {
        0
    } else {
        1
    }
}

/**
 * Check for crossing lines between the two shapes
 */
private fun isValid(points: List<Coordinate>, p1: Coordinate, p2: Coordinate): Boolean {
    val p3 = Coordinate(p1.x, p2.y)
    val p4 = Coordinate(p2.x, p1.y)
    val rect = listOf(p1, p3, p2, p4)
    val minX = min(p1.x, p2.x)
    val maxX = max(p1.x, p2.x)
    val minY = min(p1.y, p2.y)
    val maxY = max(p1.y, p2.y)

    for (i in 0 until points.size) {
        val u1 = points[i]
        val u2 = points[(i + 1) % points.size]
        val vertical1 = u1.x == u2.x
        for (j in 0 until 4) {
            val v1 = rect[j]
            val v2 = rect[(j + 1) % 4]
            val vertical2 = v1.x == v2.x

            // Only check perpendicular lines
            if (vertical1 == vertical2) continue

            if (vertical2) {
                require(!vertical1)
                require(u1.y == u2.y)
                if (u1.y in minY + 1..<maxY) {
                    if ((minX..maxX).compare(u1.x) != (minX..maxX).compare(u2.x)) {
                        // Found a crossing horizontal line
                        return false
                    }
                }
            } else {
                require(vertical1)
                require(v1.y == v2.y)
                if (u1.x in minX + 1..<maxX) {
                    if ((minY..maxY).compare(u1.y) != (minY..maxY).compare(u2.y)) {
                        // Found a crossing vertical line
                        return false
                    }
                }
            }
        }
    }
    return true
}

private fun findMaxSurface(points: List<Coordinate>, isValid: (List<Coordinate>, Coordinate, Coordinate) -> Boolean): Long {
    var maxSurface = 0L
    for (i in 0 until points.size) {
        val p1 = points[i]
        for (j in i + 1 until points.size) {
            val p2 = points[j]
            val surface = (abs(p1.x - p2.x) + 1).toLong() * (abs(p1.y - p2.y) + 1).toLong()
            if (surface > maxSurface && isValid(points, p1, p2)) {
                maxSurface = surface
            }
        }
    }
    return maxSurface
}

private fun solve(fileName: String, part: Int): Long {
    val points = parseLines("/2025/$fileName") { line ->
        val components = line.split(",").map { it.toInt() }
        Coordinate(components[0], components[1])
    }
    return findMaxSurface(points) { points, p1, p2 ->
        if (part == 1) true else isValid(points, p1, p2)
    }
}

fun main() {
    for (part in 1..2) {
        println(solve("day9-sample1.txt", part))
        println(solve("day9.txt", part))
    }
}
