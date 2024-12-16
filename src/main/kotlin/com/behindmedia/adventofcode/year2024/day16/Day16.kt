package com.behindmedia.adventofcode.year2024.day16

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.Path
import com.behindmedia.adventofcode.common.read
import java.util.*

fun main() {
    val grid = CharGrid(read("/2024/day16.txt"))
    val bestPaths = findPaths(grid)

    // Part1
    println(bestPaths.first().length)

    // Part2
    println(bestPaths.flatMap { it.allNodes }.map { it.first }.toSet().size)
}

private fun findPaths(
    grid: CharGrid
): List<Path<Pair<Coordinate, Coordinate>>>  {
    val start = grid.single { it.value == 'S' }.key
    return findShortestWeightedPaths(from = start to Coordinate.right, neighbours = { (pos, dir) ->
        pos.directNeighbours.mapNotNull { newPos ->
            val item = grid.getOrNull(newPos)
            if (item == null || item == '#') {
                null
            } else {
                val newDir = newPos - pos
                val weight = if (newDir == dir) 1 else 1001
                (newPos to newDir) to weight
            }
        }
    }, process = { path ->
        val (pos, _) = path.destination
        grid[pos] == 'E'
    })
}

/**
 * Dijkstra's algorithm to find the shortest path between weighted nodes by allowing equally weighted paths.
 */
private fun <N: Any> findShortestWeightedPaths(
    from: N,
    neighbours: (N) -> Collection<Pair<N, Int>>,
    process: (Path<N>) -> Boolean
): List<Path<N>> {
    val pending = PriorityQueue<Path<N>>()
    pending.add(Path(from, 0, null))
    val settled = mutableMapOf<N, Int>()
    val paths = mutableListOf<Path<N>>()
    while (true) {
        val current = pending.poll() ?: break
        val seenLength = settled[current.destination]
        if (seenLength != null && seenLength < current.length) continue
        if (process(current)) {
            paths += current
        }
        val currentNode = current.destination
        settled[currentNode] = current.length
        for ((neighbour, neighbourWeight) in neighbours(current.destination)) {
            val newDistance = current.length + neighbourWeight
            pending.add(Path(neighbour, newDistance, current))
        }
    }
    return paths
}