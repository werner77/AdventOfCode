package com.behindmedia.adventofcode.year2022.day8

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseMap("/2022/day8.txt") { line ->
        line - '0'
    }
    part1(data)
    part2(data)
}

private fun part1(data: Map<Coordinate, Int>) {
    val count = data.coordinateRange.count { isVisible(it, data) }
    println(count)
}

private fun part2(
    data: Map<Coordinate, Int>
) {
    val highestScore = data.coordinateRange.maxOf { calculateScenicScore(it, data) }
    println(highestScore)
}

private fun isVisible(treeCoordinate: Coordinate, data: Map<Coordinate, Int>): Boolean {
    val directions = Coordinate.directNeighbourDirections
    val treeValue = data[treeCoordinate] ?: error("Expected tree coordinate to be on map")
    for (direction in directions) {
        var visible = true
        var currentCoordinate = treeCoordinate
        while (true) {
            currentCoordinate += direction
            val nextValue = data[currentCoordinate] ?: break
            if (nextValue >= treeValue) {
                visible = false
                break
            }
        }
        if (visible) return true
    }
    return false
}

private fun calculateScenicScore(treeCoordinate: Coordinate, data: Map<Coordinate, Int>): Int {
    val directions = Coordinate.directNeighbourDirections
    val scores = MutableList(directions.size) { 0 }
    val highest = data[treeCoordinate] ?: error("Expected tree coordinate to be on map")
    for ((i, direction) in directions.withIndex()) {
        var currentCoordinate = treeCoordinate
        while (true) {
            currentCoordinate += direction
            val currentValue = data[currentCoordinate] ?: break
            scores[i]++
            if (currentValue >= highest) break
        }
    }
    return scores.product()
}
