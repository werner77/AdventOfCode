package com.behindmedia.adventofcode.year2022.day23

import com.behindmedia.adventofcode.common.*

fun main() {
    val inputMap = parseMap("/2022/day23.txt") {
        it
    }

    // Part 1
    part1(inputMap)

    // Part 2
    part2(inputMap)
}

private fun part1(inputMap: Map<Coordinate, Char>) {
    val elfCount = inputMap.values.count { it == '#' }
    val ans = simulate(inputMap) { iteration, _, map ->
        if (iteration == 10) {
            // Beak after 10 iterations
            map.keys.range().size - elfCount
        } else {
            null
        }
    }
    println(ans)
}

private fun part2(inputMap: Map<Coordinate, Char>) {
    val ans = simulate(inputMap) { iteration, moveCount, _ ->
        if (moveCount == 0) {
            iteration
        } else {
            null
        }
    }
    println(ans)
}

private fun <T> simulate(inputMap: Map<Coordinate, Char>, predicate: (Int, Int, Map<Coordinate, Char>) -> T?): T {
    val north = Coordinate.up
    val south = Coordinate.down
    val west = Coordinate.left
    val east = Coordinate.right
    val northEast = Coordinate.upRight
    val southEast = Coordinate.downRight
    val southWest = Coordinate.downLeft
    val northWest = Coordinate.upLeft
    val allDirections = listOf(north, northEast, east, southEast, south, southWest, west, northWest)
    val directionList = ArrayDeque(listOf(0, 4, 6, 2))
    val mutableMap = inputMap.toMutableMap()
    val nextDirections = mutableMapOf<Coordinate, MutableList<Coordinate>>()
    mutableMap.entries.removeIf { it.value != '#' }
    var iteration = 1
    while(true) {
        // Map of key = destination coordinate, and value is from coordinates
        nextDirections.clear()
        for (c in mutableMap.keys) {
            // Consider moves
            val optionList = ArrayList<Coordinate>(4)
            for (optionIndex in directionList) {
                val foundOther = (-1..1).any {
                    mutableMap[c + allDirections[(optionIndex + it + allDirections.size) % allDirections.size]] != null
                }
                if (!foundOther) {
                    val directionToMove = allDirections[optionIndex]
                    optionList += directionToMove
                } else if (optionList.size > 0) {
                    break
                }
            }
            // If 4 we don't do anything
            if (optionList.size in 1 until 4) {
                // Take the first
                val firstDirection = optionList.first()
                nextDirections.getOrPut(c + firstDirection) { mutableListOf() }.add(c)
            }
        }
        var moveCount = 0
        for ((to, fromList) in nextDirections) {
            val from = fromList.singleOrNull()
            if (from != null) {
                // Move
                mutableMap.remove(from)
                mutableMap[to] = '#'
                moveCount++
            }
        }
        directionList.addLast(directionList.removeFirst())
        return predicate(iteration++, moveCount, mutableMap) ?: continue
    }
}