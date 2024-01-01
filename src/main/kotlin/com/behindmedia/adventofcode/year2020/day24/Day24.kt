package com.behindmedia.adventofcode.year2020.day24

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.read

private val directions = listOf(
    Coordinate(1, 0),
    Coordinate(0, 1),
    Coordinate(-1, 1),
    Coordinate(-1, 0),
    Coordinate(0, -1),
    Coordinate(1, -1)
)

private fun consumeDirection(string: String, index: Int): Pair<Coordinate, Int> {
    return when {
        string.startsWith("e", index) -> Coordinate(1, 0) to 1
        string.startsWith("se", index) -> Coordinate(0, 1) to 2
        string.startsWith("sw", index) -> Coordinate(-1, 1) to 2
        string.startsWith("w", index) -> Coordinate(-1, 0) to 1
        string.startsWith("nw", index) -> Coordinate(0, -1) to 2
        string.startsWith("ne", index) -> Coordinate(1, -1) to 2
        else -> error("Invalid direction: $string")
    }
}

private fun process1(line: String): Coordinate {
    var current = Coordinate.origin
    var i = 0
    while (i < line.length) {
        val (c, j) = consumeDirection(line, i)
        current += c
        i += j
    }
    return current
}

private fun process2(set: Set<Coordinate>): Set<Coordinate> {
    val result = mutableSetOf<Coordinate>()
    val countMap = mutableMapOf<Coordinate, Int>()
    for (c in set) {
        countMap.getOrPut(c) { 0 }
        for (d in directions) {
            countMap[c + d] = countMap.getOrDefault(c + d, 0) + 1
        }
    }
    for ((c, v) in countMap) {
        if ((v == 0 || v > 2) && c in set) {
            // white tile
        } else if (v == 2 && c !in set) {
            result += c
        } else if (c in set) {
            result += c
        }
    }
    return result
}

fun main() {
    val flippedTiles = mutableSetOf<Coordinate>()
    read("/2020/day24.txt").split("\n").forEach { line ->
        val coordinate = process1(line)
        if (!flippedTiles.remove(coordinate)) flippedTiles.add(coordinate)
    }
    println(flippedTiles.size)

    var blackTiles: Set<Coordinate> = flippedTiles
    repeat(100) {
        blackTiles = process2(blackTiles)
    }
    println(blackTiles.size)
}