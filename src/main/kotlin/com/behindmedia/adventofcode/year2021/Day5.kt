package com.behindmedia.adventofcode.year2021

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun solve(discardDiagonals: Boolean) {
    val pointMap = mutableMapOf<Coordinate, Int>()
    parseLines("/2021/day5.txt") { line ->
        val components = line.splitNonEmptySequence(",", " ", "->") { it.toInt() }.toList()
        val start = Coordinate(components[0], components[1])
        val end = Coordinate(components[2], components[3])
        if (!discardDiagonals || start.x == end.x || start.y == end.y) {
            for (i in 0..max(abs(end.x - start.x), abs(end.y - start.y))) {
                val delta = Coordinate((end.x - start.x).sign, (end.y - start.y).sign) * i
                val c = start + delta
                pointMap[c] = (pointMap[c] ?: 0) + 1
            }
        }
    }
    println(pointMap.values.count { it >= 2 })
}

fun main() {
    // Part1
    solve(true)
    // Part2
    solve(false)
}