package com.behindmedia.adventofcode.year2024.day16

import com.behindmedia.adventofcode.common.*
import java.util.*

private typealias PositionDirection = Pair<Coordinate, Coordinate>

fun main() = timing {
    val grid = CharGrid(read("/2024/day16.txt"))
    val bestPaths = findPaths(grid)

    // Part1
    println(bestPaths.first().length)

    // Part2
    println(bestPaths.flatMap { it.allNodes }.map { it.first }.toSet().size)
}

private fun findPaths(
    grid: CharGrid
): List<Path<PositionDirection>>  {
    val start = grid.single { it.value == 'S' }.key
    return findShortestWeightedPaths(from = start to Coordinate.right, neighbours = { (pos, dir) ->
        val result = ArrayList<Pair<PositionDirection, Int>>(4)
        for (newDir in Coordinate.directNeighbourDirections) {
            val newPos = pos + newDir
            if (grid[newPos] == '#') continue
            val weight = if (newDir == dir) {
               1
            } else if (newDir.isVertical != dir.isVertical) {
                1001
            } else {
                2001
            }
            result += (newPos to newDir) to weight
        }
        result
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
    val settled = hashMapOf<N, Int>()
    val paths = mutableListOf<Path<N>>()
    while (true) {
        val current = pending.poll() ?: break
        if (paths.isNotEmpty() && paths.first().length < current.length) continue
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