package com.behindmedia.adventofcode.year2021.day11

import com.behindmedia.adventofcode.common.*

private fun MutableMap<Coordinate, Int>.increment(
    coordinate: Coordinate,
    flashed: MutableCollection<Coordinate>,
    ignorePreviouslyFlashed: Boolean
) {
    val value = this[coordinate] ?: return
    if (value == 0 && ignorePreviouslyFlashed) return
    val newValue = if (value == 9) 0 else value + 1
    if (newValue == 0) flashed += coordinate
    this[coordinate] = newValue
}

private fun MutableMap<Coordinate, Int>.simulate(): Int {
    var flashCount = 0
    val flashed = ArrayDeque<Coordinate>()
    // First increment each value
    this.keys.forEach { increment(it, flashed, false) }
    // Then recursively increment for all the flashes
    while (flashed.isNotEmpty()) {
        val c = flashed.removeFirst()
        flashCount++
        // Increment the data for each neighbour of the flashed node
        c.allNeighbours.forEach { increment(it, flashed, true) }
    }
    return flashCount
}

private fun part1(data: Map<Coordinate, Int>): Int {
    val map = data.toMutableMap()
    return (1..100).sumOf { map.simulate() }
}

private fun part2(data: Map<Coordinate, Int>): Int {
    val map = data.toMutableMap()
    return sequence {
        while (true) yield(map.simulate())
    }.indexOfFirst { it == data.size } + 1
}

fun main() {
    val data: Map<Coordinate, Int> = parseLines("/2021/day11.txt") { line ->
        line.map { it - '0' }
    }.foldIndexed(mutableMapOf()) { y, m, l ->
        for ((x, i) in l.withIndex()) {
            m[Coordinate(x, y)] = i
        }
        m
    }
    timing {
        // Part 1
        println(part1(data))

        // Part 2
        println(part2(data))
    }
}