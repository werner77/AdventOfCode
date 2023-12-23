package com.behindmedia.adventofcode.year2023.day23

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.DefaultMap
import com.behindmedia.adventofcode.common.defaultMutableMapOf
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
    val vertexMap: DefaultMap<Coordinate, Int> =
        vertices.withIndex().fold(defaultMutableMapOf { error("No value") }) { map, (index, coordinate) ->
            map.apply { put(coordinate, index) }
        }
    val graph: DefaultMap<Int, List<Pair<Int, Int>>> =
        vertices.fold(defaultMutableMapOf { error("No value") }) { map, coordinate ->
            map.apply {
                put(vertexMap[coordinate], edges[coordinate]!!.map { (c, length) -> vertexMap[c] to length })
            }
        }
    // Process the connections with a dfs to find the max path
    return findMaxSteps(graph, vertexMap[start], vertexMap[finish], BooleanArray(vertices.size))
}

private fun findVertices(grid: CharGrid, start: Coordinate, finish: Coordinate): Set<Coordinate> {
    val nodes = mutableSetOf<Coordinate>()
    nodes += start
    nodes += finish
    val pending = ArrayDeque<Coordinate>()
    pending += start
    val seen = hashSetOf<Coordinate>()
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
        val seen = hashSetOf<Coordinate>()
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
    graph: Map<Int, List<Pair<Int, Int>>>,
    current: Int,
    to: Int,
    seen: BooleanArray
): Int {
    if (current == to) {
        // Found destination
        return 0
    } else if (seen[current]) {
        // Cannot reach same coordinate twice
        return -1
    }
    seen[current] = true
    var maxSteps = -1
    for ((neighbour, length) in graph[current]!!) {
        val steps = findMaxSteps(graph, neighbour, to, seen)
        if (steps >= 0) {
            maxSteps = max(maxSteps, steps + length)
        }
    }
    seen[current] = false
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