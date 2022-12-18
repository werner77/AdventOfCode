package com.behindmedia.adventofcode.common

import java.util.ArrayDeque
import java.util.PriorityQueue

class Path<N>(val destination: N, val pathLength: Long, val parent: Path<N>?) : Comparable<Path<N>> {
    val nodeCount: Int = if (parent == null) 1 else parent.nodeCount + 1

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
    neighbours: (Path<N>) -> Sequence<N>,
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
    neighbours: (N) -> Sequence<Pair<N, Long>>,
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
        for ((neighbour, neighbourWeight) in neighbours(currentNode)) {
            val newDistance = current.pathLength + neighbourWeight
            pending.add(Path(neighbour, newDistance, current))
        }
    }
    return null
}
