package com.behindmedia.adventofcode.year2024.day4

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private data class Vector(val start: Coordinate, val direction: Coordinate)

fun main() {
    val grid = CharGrid.invoke(read("/2024/day4.txt"))

    // Part1
    println(grid.countMatches("XMAS".length, ::match1))

    // Part2
    println(grid.countMatches("MAS".length, ::match2))
}

private fun match1(grid: CharGridView): Int {
    val vectors: List<Vector> = listOf(
        Vector(Coordinate(0, 0), Coordinate.right), // Right
        Vector(Coordinate(grid.maxX, 0), Coordinate.left), // Left
        Vector(Coordinate(0, 0), Coordinate.down), // Down
        Vector(Coordinate(0, grid.maxY), Coordinate.up), // Up
        Vector(Coordinate(grid.maxX, 0), Coordinate.downLeft), // DownLeft
        Vector(Coordinate(0, 0), Coordinate.downRight), // DownRight
        Vector(Coordinate(grid.maxX, grid.maxY), Coordinate.upLeft), // UpLeft
        Vector(Coordinate(0, grid.maxY), Coordinate.upRight), // UpRight
    )
    return vectors.count { (start, direction) ->
        grid.matches(string = "XMAS", start = start, direction = direction)
    }
}

private fun match2(grid: CharGridView): Int {
    val vectors1: List<Vector> = listOf(
        Vector(Coordinate(grid.maxX, 0), Coordinate.downLeft), // DownLeft
        Vector(Coordinate(0, grid.maxY), Coordinate.upRight), // UpRight
    )
    val vectors2: List<Vector> = listOf(
        Vector(Coordinate(0, 0), Coordinate.downRight), // DownRight
        Vector(Coordinate(grid.maxX, grid.maxY), Coordinate.upLeft), // UpLeft
    )
    val match = vectors1.any { (start, direction) ->
        grid.matches(string = "MAS", start = start, direction = direction)
    } && vectors2.any { (start, direction) ->
        grid.matches(string = "MAS", start = start, direction = direction)
    }
    return if (match) 1 else 0
}

private fun CharGrid.countMatches(size: Int, process: (CharGridView) -> Int): Int {
    return this.coordinateRange.sumOf { offset ->
        getSubGrid(
            offset = offset,
            sizeX = min(size, this.sizeX - offset.x),
            sizeY = min(size, this.sizeY - offset.y)
        )?.let {
            process(it)
        } ?: 0
    }
}

private fun CharGridView.matches(string: String, start: Coordinate, direction: Coordinate): Boolean {
    var current = start
    val coordinateRange = this.coordinateRange
    var matchIndex = 0
    while (current in coordinateRange) {
        while (true) {
            if (string[matchIndex] == this[current]) {
                matchIndex++
                if (matchIndex == string.length) {
                    return true
                } else {
                    break
                }
            } else {
                if (matchIndex == 0) {
                    // Match index already 0, break loop
                    break
                }
                // Reset matchIndex to 0 and try again
                matchIndex = 0
            }
        }
        current += direction
    }
    return false
}