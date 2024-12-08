package com.behindmedia.adventofcode.year2024.day8

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val grid = CharGrid(read("/2024/day8.txt"))

    // Part1
    println(grid.findAntiNodes(false).size)

    // Part2
    println(grid.findAntiNodes(true).size)
}

private fun CharGrid.findAntiNodes(
    all: Boolean
): MutableSet<Coordinate> {
    val map = this.filter { it.value != '.' }.groupBy { it.value }
    val seen = mutableSetOf<Coordinate>()
    for (entries in map.values) {
        for (i in entries.indices) {
            val c1 = entries[i].key
            for (j in i + 1 until entries.size) {
                val c2 = entries[j].key
                this.findAntiNodes(c1, c2, seen, all)
            }
        }
    }
    return seen
}

private fun CharGrid.findAntiNodes(
    c1: Coordinate,
    c2: Coordinate,
    seen: MutableSet<Coordinate>,
    all: Boolean
) {
    val coordinateRange = this.coordinateRange
    val direction = c2 - c1
    if (all) {
        var current = c2
        while (current in coordinateRange) {
            seen += current
            current += direction
        }
        current = c1
        while (current in coordinateRange) {
            seen += current
            current -= direction
        }
    } else {
        (c1 - direction).takeIf { it in coordinateRange }?.let { seen += it }
        (c2 + direction).takeIf { it in coordinateRange }?.let { seen += it }
    }
}
