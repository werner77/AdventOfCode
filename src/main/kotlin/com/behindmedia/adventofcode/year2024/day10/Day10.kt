package com.behindmedia.adventofcode.year2024.day10

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val grid = CharGrid(read("/2024/day10.txt"))

    val trailHeads = grid.filter { it.value == '0' }.map { it.key }

    // Part1
    println(trailHeads.sumOf { grid.findScore(it to '0', hashSetOf()) })

    // Part2
    println(trailHeads.sumOf { grid.findScore(it to '0', null) })
}

private fun CharGrid.findScore(current: Pair<Coordinate, Char>, seen: MutableSet<Coordinate>?): Long {
    val (coordinate, value) = current
    if (seen != null && !seen.add(coordinate)) {
        return 0L
    } else if (value == '9') {
        return 1L
    }
    var score = 0L
    for (neighbor in coordinate.directNeighbours) {
        val newValue = getOrNull(neighbor) ?: continue
        if (newValue != value + 1) continue
        score += findScore(neighbor to newValue, seen)
    }
    return score
}
