package com.behindmedia.adventofcode.year2015.day3

import com.behindmedia.adventofcode.common.*

private val directionMap = mapOf<Char, Coordinate>(
    '<' to Coordinate.left,
    '>' to Coordinate.right,
    '^' to Coordinate.up,
    'v' to Coordinate.down,
)

private fun part1(directions: List<Coordinate>): Int {
    val counter = defaultMutableMapOf<Coordinate, Int> { 0 }
    var pos = Coordinate.origin
    counter[pos] += 1
    for (direction in directions) {
        pos += direction
        counter[pos] += 1
    }
    return counter.size
}

private fun part2(directions: List<Coordinate>): Int {
    val counter = defaultMutableMapOf<Coordinate, Int> { 0 }
    var pos1 = Coordinate.origin
    var pos2 = Coordinate.origin
    counter[pos1] += 1
    counter[pos2] += 1
    for ((i, direction) in directions.withIndex()) {
        if (i % 2 == 0) {
            pos1 += direction
            counter[pos1] += 1
        } else {
            pos2 += direction
            counter[pos2] += 1
        }
    }
    return counter.size
}

fun main() {
    val directions = parse("/2015/day3.txt") { line ->
        line.trim().map { directionMap[it] ?: error("Invalid char: $it") }
    }

    println(part1(directions))
    println(part2(directions))
}