package com.behindmedia.adventofcode.year2017.day3

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun grow(value: (Map<Coordinate, Int>, Int, Coordinate) -> Int?): Map<Coordinate, Int> {
    var current = Coordinate.origin
    val map = mutableMapOf<Coordinate, Int>()
    var direction = Coordinate.down
    var i = 0
    while (true) {
        map[current] = value.invoke(map, i, current)?.also { i = it } ?: break

        // See if we need to change the direction
        val rotatedDirection = direction.rotate(RotationDirection.Left)
        if (map[current + rotatedDirection] == null) {
            // Rotate
            direction = rotatedDirection
        }
        current += direction
    }
    return map
}

fun main() {
    val data = parseLines("/2017/day3.txt") { line ->
        line.toInt()
    }
    val number = data[0]

    // Part 1
    part1(number)

    part2(number)
}

private fun part1(number: Int) {
    var lastCoordinate: Coordinate? = null
    grow { _, value, coordinate ->
        lastCoordinate = coordinate
        val newValue = value + 1
        if (newValue == number) null else newValue
    }

    println(lastCoordinate!!.manhattenDistance(Coordinate.origin))
}

private fun part2(number: Int) {
    var biggestValue: Int? = null
    grow { map, _, coordinate ->
        val newValue = max(1, coordinate.allNeighbours.mapNotNull { map[it] }.sum())
        biggestValue = newValue
        if (newValue > number) null else newValue
    }
    println(biggestValue!!)
}