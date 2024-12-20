package com.behindmedia.adventofcode.year2024.day20

import com.behindmedia.adventofcode.common.*
import kotlin.math.abs

fun main() = timing {
    val grid = CharGrid(read("/2024/day20.txt"))
    val pathStart = grid.single { it.value == 'S' }.key
    val pathEnd = grid.single { it.value == 'E' }.key
    val path = shortestPath(
        from = pathStart,
        neighbours = { it.destination.directNeighbours },
        reachable = { _, it -> grid.getOrNull(it) != '#' },
        process = { if (it.destination == pathEnd) it else null }
    ) ?: error("No path found")

    // Part 1
    println(cheatCount(path, 2))

    // Part 2
    println(cheatCount(path, 20))
}

private fun cheatCount(
    path: Path<Coordinate>,
    maxTime: Int,
    minSave: Int = 100,
): Int {
    val nodes = path.allNodes
    val (sortedSteps, stepLengths) = sortedSteps(nodes)
    val countMap = defaultMutableMapOf<Int, Int> { 0 }
    val signs = listOf(-1, 1)
    for ((startLength, start) in nodes.withIndex()) {
        val (x1, y1) = start
        for (y2 in y1 - maxTime..y1 + maxTime) {
            // Gets the sorted x values corresponding with y value = y2
            val sortedX = sortedSteps.getOrNull(y2) ?: continue
            val distY = abs(y2 - y1)

            // As distY increases the rangeX decreases to keep the max manhattan distance within the maxTime bounds
            val rangeX = maxTime - distY

            // Locate the current coordinate in the sorted array of x coordinates corresponding with
            var pivot = sortedX.binarySearch(x1)
            if (pivot < 0) {
                pivot = -pivot - 1
            }
            for (sign in signs) {
                var offset = if (sign >= 0) 0 else 1
                while (true) {
                    val x2 = sortedX.getOrNull(pivot + sign * offset) ?: break
                    val distX = abs(x2 - x1)
                    if (distX > rangeX) break
                    val endLength = stepLengths[y2][x2]
                    val save = endLength - startLength - distX - distY
                    if (save >= minSave) {
                        countMap[save]++
                    }
                    offset++
                }
            }
        }
    }
    return countMap.values.sum()
}

/**
 * Returns two 2D arrays:
 *
 * - first: steps in path indexed by y and then all nodes belonging to that y coordinate sorted by x
 * - second: the lengths of the steps for each x, y coordinate
 */
private fun sortedSteps(
    nodes: List<Coordinate>,
): Pair<Array<IntArray>, Array<IntArray>> {
    val sizeX = nodes.maxOf { it.x } + 1
    val sizeY = nodes.maxOf { it.y } + 1
    val stepLengths = Array(sizeY) { IntArray(sizeX) { 0 } }
    val steps = Array(sizeY) { mutableListOf<Int>() }
    for ((length, c) in nodes.withIndex()) {
        steps[c.y] += c.x
        stepLengths[c.y][c.x] = length
    }
    val sortedSteps = steps.map { list -> list.toIntArray().also { it.sort() } }.toTypedArray<IntArray>()
    return sortedSteps to stepLengths
}
