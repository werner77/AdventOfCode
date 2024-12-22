package com.behindmedia.adventofcode.common

import kotlin.math.abs
import java.util.PriorityQueue

/**
 * minLength to target is an optional heuristic to use A* instead of Dijkstra
 */
class Path<N: Any> @JvmOverloads constructor(
    val destination: N,
    val length: Int,
    val parent: Path<N>?,
    val minLengthToTarget: Int = 0
) : Comparable<Path<N>> {
    val nodeCount: Int by lazy { if (parent == null) 1 else parent.nodeCount + 1 }

    override fun compareTo(other: Path<N>): Int {
        return (this.length + minLengthToTarget).compareTo(other.length + other.minLengthToTarget)
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

    inline fun nodes(where: (N) -> Boolean): List<N> {
        val result = ArrayDeque<N>()
        any {
            if (where(it)) result.addFirst(it)
            false
        }
        return result
    }

    val allNodes: List<N>
        get() {
            return nodes { true }
        }
}

val Path<Coordinate>.completeDirections: List<Coordinate>
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


val List<Point2D<*, *>>.doubleEnclosedSurfaceArea: Long
    get() {
        var result = 0L
        for (i in 1..this.size) {
            result += this[i % this.size].y.toLong() * (this[(i - 1) % this.size].x.toLong() - this[(i + 1) % this.size].x.toLong())
        }
        return abs(result)
    }

fun List<Point2D<*, *>>.insidePointCount(boundaryPointCount: Long = this.size.toLong()): Long {
    val area = doubleEnclosedSurfaceArea
    return (area - boundaryPointCount) / 2L + 1L
}

data class CoordinatePath(val coordinate: Coordinate, val pathLength: Int) : Comparable<CoordinatePath> {
    override fun compareTo(other: CoordinatePath): Int {
        return this.pathLength.compareTo(other.pathLength)
    }
}

/**
 * Breadth first search algorithm to find the shortest paths between unweighted nodes.
 */
inline fun <reified N: Any, T> shortestPath(
    from: N,
    neighbours: (Path<N>) -> Collection<N>,
    reachable: (Path<N>, N) -> Boolean = { _, _ -> true },
    process: (Path<N>) -> T?
): T? {
    val pending = ArrayDeque<Path<N>>()
    val visited = mutableSetOf<N>()
    pending.add(Path(from, 0, null))
    visited.add(from)
    while (true) {
        val current = pending.removeFirstOrNull() ?: return null
        process(current)?.let {
            return it
        }
        for (neighbour in neighbours(current)) {
            if (reachable(current, neighbour) && visited.add(neighbour)) {
                pending.add(Path(neighbour, current.length + 1, current))
            }
        }
    }
}

/**
 * Dijkstra's algorithm to find the shortest path between weighted nodes.
 */
inline fun <N: Any, T> shortestWeightedPath(
    from: N,
    neighbours: (N) -> Collection<Pair<N, Int>>,
    minLengthToTarget: (N) -> Int = { _ -> 0 },
    findAll: Boolean = false,
    process: (Path<N>) -> T?
): T? {
    val pending = PriorityQueue<Path<N>>()
    pending.add(Path(from, 0, null))
    val settled = mutableMapOf<N, Int>(from to 0)
    var minLength = Int.MAX_VALUE
    while (true) {
        val current = pending.poll() ?: break
        if (current.length > minLength) {
            break
        }
        val result = process(current)
        if (result != null) {
            minLength = current.length
            if (findAll) {
                continue
            } else {
                return result
            }
        }
        for ((neighbour, neighbourWeight) in neighbours(current.destination)) {
            val newWeight = current.length + neighbourWeight
            val existingWeight = settled[neighbour] ?: Int.MAX_VALUE
            val valid = if (findAll) newWeight <= existingWeight else newWeight < existingWeight
            if (valid) {
                if (newWeight < existingWeight) {
                    settled[neighbour] = newWeight
                }
                pending.add(Path(neighbour, newWeight, current, minLengthToTarget.invoke(neighbour)))
            }
        }
    }
    return null
}

/**
 * Breadth first search to find the shortest path to all reachable coordinates in a single sweep
 */
inline fun <T> Coordinate.reachableCoordinates(reachable: (Coordinate) -> Boolean, process: (CoordinatePath) -> T?): T? {
    val list = ArrayDeque<CoordinatePath>()
    val visited = mutableSetOf<Coordinate>()
    list.add(CoordinatePath(this, 0))
    var start = true
    while (true) {
        val current = list.removeFirstOrNull() ?: return null
        if (start) {
            start = false
        } else {
            process(current)?.let {
                return it
            }
        }
        visited.add(current.coordinate)
        current.coordinate.directNeighbourSequence().forEach { neighbour ->
            if (!visited.contains(neighbour) && reachable(neighbour)) {
                list.add(CoordinatePath(neighbour, current.pathLength + 1))
            }
        }
    }
}
