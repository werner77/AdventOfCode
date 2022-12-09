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
    println(data.keys.count { isVisible(it, data) })
}

private fun part2(data: Map<Coordinate, Int>) {
    println(data.keys.maxOf { getScenicScore(it, data) })
}

private fun isVisible(treeCoordinate: Coordinate, map: Map<Coordinate, Int>): Boolean {
    val directions = Coordinate.directNeighbourDirections
    val treeHeight = map[treeCoordinate] ?: error("Expected tree coordinate to be on map")
    for (direction in directions) {
        var visible = true
        var currentCoordinate = treeCoordinate
        while (true) {
            currentCoordinate += direction
            val height = map[currentCoordinate] ?: break
            if (height >= treeHeight) {
                visible = false
                break
            }
        }
        if (visible) return true
    }
    return false
}

private fun getScenicScore(treeCoordinate: Coordinate, map: Map<Coordinate, Int>): Int {
    val directions = Coordinate.directNeighbourDirections
    val scores = MutableList(directions.size) { 0 }
    val treeHeight = map[treeCoordinate] ?: error("Expected tree coordinate to be on map")
    for ((i, direction) in directions.withIndex()) {
        var currentCoordinate = treeCoordinate
        while (true) {
            currentCoordinate += direction
            val height = map[currentCoordinate] ?: break
            scores[i]++
            if (height >= treeHeight) break
        }
    }
    return scores.product()
}
