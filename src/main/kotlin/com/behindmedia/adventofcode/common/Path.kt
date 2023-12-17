package com.behindmedia.adventofcode.common

import kotlin.math.abs
import java.util.ArrayDeque
import java.util.PriorityQueue

class Path<N>(val destination: N, val pathLength: Int, val parent: Path<N>?) : Comparable<Path<N>> {
    val nodeCount: Int by lazy { if (parent == null) 1 else parent.nodeCount + 1 }

    override fun compareTo(other: Path<N>): Int {
        return this.pathLength.compareTo(other.pathLength)
    }

    operator fun contains(node: N): Boolean {
        return any { it == node }
    }

    inline fun any(where: (N) -> Boolean): Boolean {
        var current: Path<N>? = this
        while (current != null) {
            if (where.invoke(current.destination)) return true
            current = current.parent
        }
        return false
    }

    inline fun nodes(where: (N) -> Boolean): Collection<N> {
        val result = ArrayDeque<N>()
        any {
            if (where(it)) result.addFirst(it)
            false
        }
        return result
    }

    val allNodes: Collection<N>
        get() {
            return nodes { true }
        }
}

val Path<Coordinate>.completeDirections: Collection<Coordinate>
    get() {
        val result = ArrayDeque<Coordinate>()
        var current: Path<Coordinate>? = this
        while (true) {
            val parent = current?.parent ?: break
            val direction = current.destination - parent.destination
            result.addFirst(direction)
            current = parent
        }
        return result
    }


val List<Coordinate>.doubleEnclosedSurfaceArea: Long
    get() {
        var result = 0L
        for (i in 1..this.size) {
            result += this[i % this.size].y * (this[(i - 1) % this.size].x - this[(i + 1) % this.size].x)
        }
        return abs(result)
    }

val List<Coordinate>.insidePointCount: Int
    get() {
        val area = doubleEnclosedSurfaceArea
        val boundaryPointCount = this.size
        return (area.toInt() - boundaryPointCount) / 2 + 1
    }

class CoordinatePath(val coordinate: Coordinate, val pathLength: Int) : Comparable<CoordinatePath> {
    override fun compareTo(other: CoordinatePath): Int {
        return this.pathLength.compareTo(other.pathLength)
    }
}

/**
 * Breadth first search algorithm to find the shortest paths between unweighted nodes.
 */
inline fun <reified N, T> shortestPath(
    from: N,
    neighbours: (Path<N>) -> Collection<N>,
    reachable: (Path<N>, N) -> Boolean = { _, _ -> true },
    process: (Path<N>) -> T?
): T? {
    val pending = ArrayDeque<Path<N>>()
    val visited = mutableSetOf<N>()
    pending.add(Path(from, 0, null))
    while (true) {
        val current = pending.pollFirst() ?: return null
        if (visited.contains(current.destination)) continue
        visited += current.destination
        process(current)?.let {
            return it
        }
        for (neighbour in neighbours(current)) {
            if (neighbour in visited) continue
            if (reachable(current, neighbour)) {
                pending.add(Path(neighbour, current.pathLength + 1, current))
            }
        }
    }
}

/**
 * Dijkstra's algorithm to find the shortest path between weighted nodes.
 */
inline fun <N, T> shortestWeightedPath(
    from: N,
    neighbours: (Path<N>) -> Collection<Pair<N, Int>>,
    process: (Path<N>) -> T?
): T? {
    val pending = PriorityQueue<Path<N>>()
    pending.add(Path(from, 0, null))
    val settled = mutableSetOf<N>()
    while (true) {
        val current = pending.poll() ?: break
        if (settled.contains(current.destination)) continue
        process(current)?.let {
            return it
        }
        val currentNode = current.destination
        settled.add(currentNode)
        for ((neighbour, neighbourWeight) in neighbours(current)) {
            val newDistance = current.pathLength + neighbourWeight
            pending.add(Path(neighbour, newDistance, current))
        }
    }
    return null
}
