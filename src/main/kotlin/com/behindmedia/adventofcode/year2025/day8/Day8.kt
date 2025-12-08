package com.behindmedia.adventofcode.year2025.day8

import com.behindmedia.adventofcode.common.*

private fun getSortedPairs(points: List<Coordinate3D>): List<Pair<Coordinate3D, Coordinate3D>> {
    val pairs = mutableListOf<Pair<Coordinate3D, Coordinate3D>>()
    for (i in 0 until points.size - 1) {
        for (j in i + 1 until points.size) {
            pairs += points[i] to points[j]
        }
    }
    pairs.sortBy { it.first.distance(it.second) }
    return pairs
}

private fun <T> solve(pairs: List<Pair<Coordinate3D, Coordinate3D>>, onConnection: (Pair<Coordinate3D, Coordinate3D>, Map<Int, Set<Coordinate3D>>) -> T?): T {
    var circuitId = 0
    val points = mutableMapOf<Coordinate3D, Int>()
    val circuits = mutableMapOf<Int, MutableSet<Coordinate3D>>()
    for (pair in pairs) {
        val p1 = pair.first
        val p2 = pair.second
        val c1 = points[p1]
        val c2 = points[p2]
        if (c1 == null && c2 == null) {
            // New circuit
            circuitId++
            points[p1] = circuitId
            points[p2] = circuitId
            require(circuits.getOrPut(circuitId) { mutableSetOf() }.add(p1))
            require(circuits.getOrPut(circuitId) { mutableSetOf() }.add(p2))
        } else if (c1 == null) {
            points[p1] = c2!!
            require(circuits.getOrPut(c2) { mutableSetOf() }.add(p1))
        } else if (c2 == null) {
            points[p2] = c1
            require(circuits.getOrPut(c1) { mutableSetOf() }.add(p2))
        } else if (c1 != c2) {
            val s = circuits[c2]!!
            for (p in circuits.remove(c1)!!) {
                points[p] = c2
                s += p
            }
        } else {
            // do nothing
        }
        return onConnection(pair, circuits) ?: continue
    }
    error("No solution found")
}

private fun solve(fileName: String, part: Int, limit: Int): Long {
    val points = parseLines("/2025/$fileName") { line ->
        val components = line.split(",").map { it.toLong() }
        require(components.size == 3)
        Coordinate3D(components[0], components[1], components[2])
    }
    val pairs = getSortedPairs(points)
    if (part == 1) {
        var counter = 0
        return solve(pairs) { _, circuits ->
            if (++counter == limit) {
                val sizes = circuits.values.map { it.size }.sortedDescending()
                sizes[0].toLong() * sizes[1].toLong() * sizes[2].toLong()
            } else {
                null
            }
        }
    } else {
        return solve(pairs) { p, circuits ->
            if (circuits.size == 1 && circuits.iterator().next().value.size == points.size) {
                p.first.x * p.second.x
            } else {
                null
            }
        }
    }
}

fun main() {
    for (part in 1..2) {
        println(solve("day8-sample1.txt", part, 10))
        println(solve("day8.txt", part, 1000))
    }
}
