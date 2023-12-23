package com.behindmedia.adventofcode.year2023.day23

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing
import kotlin.math.max

fun main() {
    val grid = CharGrid(read("/2023/day23.txt"))
    val start = grid.single { it.key.y == 0 && it.value == '.' }.key
    val finish = grid.single { it.key.y == grid.maxY && it.value == '.' }.key

    timing {
        // Part 1
        println(solution(grid, start, finish, true))

        // Part 2
        println(solution(grid, start, finish, false))
    }
}

private fun solution(grid: CharGrid, start: Coordinate, finish: Coordinate, strict: Boolean): Int {
    // Determine start and finish point
    val vertices = findVertices(grid, start, finish)
    val edges = findEdges(grid, vertices, strict)
    // Process the connections with a dfs to find the max path
    return findMaxSteps(edges, start, finish, mutableSetOf())
}

private fun findVertices(grid: CharGrid, start: Coordinate, finish: Coordinate): Set<Coordinate> {
    val nodes = mutableSetOf<Coordinate>()
    nodes += start
    nodes += finish
    val pending = ArrayDeque<Coordinate>()
    pending += start
    val seen = mutableSetOf<Coordinate>()
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        if (current in seen) continue
        seen += current
        val candidates = current.directNeighbours.filter { grid.isReachable(it, it - current, false) }
        if (candidates.size >= 3) {
            nodes += current
        }
        pending += candidates
    }
    return nodes
}

private fun findEdges(
    grid: CharGrid,
    vertices: Set<Coordinate>,
    strict: Boolean
): Map<Coordinate, List<Pair<Coordinate, Int>>> {
    val result = mutableMapOf<Coordinate, MutableList<Pair<Coordinate, Int>>>()
    for (vertex in vertices) {
        val currentEdges = mutableListOf<Pair<Coordinate, Int>>()
        result[vertex] = currentEdges
        val pending = ArrayDeque<Pair<Coordinate, Int>>()
        pending += vertex to 0
        val seen = mutableSetOf<Coordinate>()
        while (pending.isNotEmpty()) {
            val (pos, length) = pending.removeFirst()
            if (pos != vertex && pos in vertices) {
                // Add to edges
                currentEdges += pos to length
                continue
            }
            if (pos in seen) continue
            seen += pos
            val candidates = pos.directNeighbours.filter { grid.isReachable(it, it - pos, strict) }
            for (next in candidates) {
                pending += next to length + 1
            }
        }
    }
    return result
}

private fun findMaxSteps(
    graph: Map<Coordinate, List<Pair<Coordinate, Int>>>,
    current: Coordinate,
    to: Coordinate,
    seen: MutableSet<Coordinate>
): Int {
    if (current == to) {
        // Found destination
        return 0
    } else if (current in seen) {
        // Cannot reach same coordinate twice
        return -1
    }
    seen += current
    var maxSteps = -1
    for ((neighbour, length) in graph[current]!!) {
        val steps = findMaxSteps(graph, neighbour, to, seen).takeIf { it >= 0 } ?: continue
        maxSteps = max(maxSteps, steps + length)
    }
    seen -= current
    return maxSteps
}

private fun CharGrid.isReachable(coordinate: Coordinate, direction: Coordinate, strict: Boolean): Boolean {
    return when (val value = this.getOrNull(coordinate)) {
        null, '#' -> false
        '.' -> true
        '>' -> !strict || direction == Coordinate.right
        '<' -> !strict || direction == Coordinate.left
        'v' -> !strict || direction == Coordinate.down
        '^' -> !strict || direction == Coordinate.up
        else -> error("Invalid value: $value")
    }
}