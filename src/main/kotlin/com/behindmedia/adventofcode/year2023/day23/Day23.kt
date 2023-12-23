package com.behindmedia.adventofcode.year2023.day23

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.Path
import com.behindmedia.adventofcode.common.read
import kotlin.math.max

fun main() {
    val grid = CharGrid(read("/2023/day23.txt"))

    val start = grid.single { it.key.y == 0 && it.value == '.' }.key
    val finish = grid.single { it.key.y == grid.maxY && it.value == '.' }.key

    // Part 1, direct implementation
    println(part1(grid, start, finish))

    // Part 2, weighted graph
    println(part2(grid, start, finish))
}

private fun part2(grid: CharGrid, start: Coordinate, finish: Coordinate): Int {
    // Determine start and finish point

    val graph = findWeightedGraph(grid, start, finish)

    // Process the connections with a dfs to find the max path
    return findMaxSteps(graph, start, finish, mutableSetOf())
}

private fun findWeightedGraph(
    grid: CharGrid,
    start: Coordinate,
    finish: Coordinate,
): Map<Coordinate, List<Pair<Coordinate, Int>>> {
    val result = mutableMapOf<Coordinate, MutableList<Pair<Coordinate, Int>>>()
    val pendingNodes = ArrayDeque<Coordinate>()
    pendingNodes += start
    pendingNodes += finish
    while (pendingNodes.isNotEmpty()) {
        val currentNode = pendingNodes.removeFirst()
        if (result.contains(currentNode)) continue
        val currentNodeList = mutableListOf<Pair<Coordinate, Int>>()
        result[currentNode] = currentNodeList

        // Find all reachable nodes from the current node, stop on any node
        val pending = ArrayDeque<Path<Coordinate>>()
        val seen = mutableSetOf<Coordinate>()
        pending += Path(currentNode, 0, null)
        while (pending.isNotEmpty()) {
            val path = pending.removeFirst()
            val pos = path.destination
            val length = path.length
            if (pos in seen) continue
            seen += pos
            val lastDirection = path.parent?.let { pos - it.destination }
            val candidates = pos.directNeighbours.filter { grid.isReachable(it, it - pos, false) && pos - it != lastDirection }
            if (pos != currentNode && (candidates.size > 1 || pos == start || pos == finish)) {
                // We found a node
                currentNodeList += pos to length
                pendingNodes += pos
                continue
            }
            for (next in candidates) {
                pending += Path(next, length + 1, path)
            }
        }
    }
    return result.also { println(it) }
}


private fun findMaxSteps(
    connections: Map<Coordinate, List<Pair<Coordinate, Int>>>,
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

    // Mark as seen
    seen += current
    var maxSteps = -1
    for ((neighbour, length) in connections.getOrDefault(current, emptyList())) {
        val steps = findMaxSteps(connections, neighbour, to, seen).takeIf { it >= 0 } ?: continue
        maxSteps = max(maxSteps, steps + length)
    }
    // Unwind
    seen -= current
    return maxSteps
}

private fun part1(
    grid: CharGrid,
    current: Coordinate,
    to: Coordinate,
    seen: MutableSet<Coordinate> = mutableSetOf()
): Int {
    if (current == to) {
        // Found destination
        return 0
    } else if (current in seen) {
        // Cannot reach same coordinate twice
        return -1
    }

    // Mark as seen
    seen += current
    var maxSteps = -1
    for (neighbour in current.directNeighbours) {
        if (!grid.isReachable(neighbour, neighbour - current, true)) continue
        val steps = part1(grid, neighbour, to, seen).takeIf { it >= 0 } ?: continue
        maxSteps = max(maxSteps, steps + 1)
    }
    // Unwind
    seen -= current
    return maxSteps
}

private fun CharGrid.isReachable(coordinate: Coordinate, direction: Coordinate?, strict: Boolean): Boolean {
    val value = this.getOrNull(coordinate) ?: return false
    if (value == '#') {
        return false
    } else if (value != '.' && strict) {
        if (direction == null) {
            return false
        } else if (value == '>' && direction != Coordinate.right) {
            return false
        } else if (value == '<' && direction != Coordinate.left) {
            return false
        } else if (value == 'v' && direction != Coordinate.down) {
            return false
        } else if (value == '^' && direction != Coordinate.up) {
            return false
        }
    }
    return true
}