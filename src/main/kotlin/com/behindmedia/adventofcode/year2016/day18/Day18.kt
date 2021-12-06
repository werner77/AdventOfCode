package com.behindmedia.adventofcode.year2016.day18

import com.behindmedia.adventofcode.common.*

private fun MutableMap<Coordinate, Char>.isTrap(coordinate: Coordinate): Boolean {
    val (x, y) = coordinate
    val leftValue = getValue(Coordinate(x - 1, y - 1))
    val centerValue = getValue(Coordinate(x, y - 1))
    val rightValue = getValue(Coordinate(x + 1, y - 1))
    return if (leftValue == '^') {
        (centerValue == '^' && rightValue == '.') || (centerValue == '.' && rightValue == '.')
    } else {
        (centerValue == '^' && rightValue == '^') || (centerValue == '.' && rightValue == '^')
    }
}

private fun MutableMap<Coordinate, Char>.fillCoordinates(y: Int, size: Int) {
    for (x in 0 until size) {
        val c = Coordinate(x, y)
        this[c] = if (isTrap(c)) '^' else '.'
    }
}

fun main() {
    // Part 1
    solve(40)
    // Part 2
    solve(400000)
}

private fun solve(rowCount: Int) {
    val map = mutableMapOf<Coordinate, Char>().withDefault { '.' }
    var coordinate = Coordinate.origin
    parseLines("/2016/day18.txt") { line ->
        for (c in line) {
            map[coordinate] = c
            coordinate += Coordinate.right
        }
    }
    val xCount = map.map { it.key.x }.maxOrNull() ?: error("No values")
    for (y in 1 until rowCount) {
        map.fillCoordinates(y, xCount + 1)
    }
    println(map.count { it.value == '.' })
}