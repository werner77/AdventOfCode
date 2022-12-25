package com.behindmedia.adventofcode.year2016.day24

import com.behindmedia.adventofcode.common.*

private val Char.isTarget: Boolean
    get() = this - '0' in 0 until 10

private data class Node(val coordinate: Coordinate, val items: Set<Char>)

private fun Map<Coordinate, Char>.itemsAt(coordinate: Coordinate): Set<Char> {
    val c = this[coordinate] ?: return emptySet()
    return if (c.isTarget) setOf(c) else emptySet()
}

fun main() {
    val map = parseLines("/2016/day24.txt") { line ->
        line
    }.foldIndexed(mutableMapOf<Coordinate, Char>()) { y, m, line ->
        m.apply {
            for ((x, c) in line.withIndex()) {
                put(Coordinate(x, y), c)
            }
        }
    }
    val targets = map.entries.filter { it.value.isTarget }.map { it.value }.toSet()
    val startCoordinate = map.entries.first { it.value == '0' }.key
    val startNode = Node(coordinate = startCoordinate, items = setOf('0'))

    // Part 1
    println(findMinimumPath(startNode, map, targets, null).pathLength)

    // Part 2
    println(findMinimumPath(startNode, map, targets, startCoordinate).pathLength)
}

private fun findMinimumPath(
    startNode: Node,
    map: MutableMap<Coordinate, Char>,
    targets: Set<Char>,
    targetCoordinate: Coordinate?
): Path<Node> {
    return shortestPath(
        from = startNode,
        neighbours = { path ->
            path.destination.coordinate.directNeighbours.map {
                Node(it, path.destination.items + map.itemsAt(it))
            }
        },
        reachable = { _, node ->
            map[node.coordinate].let { it != null && it != '#' }
        },
        process = { path ->
            if (path.destination.items == targets && (targetCoordinate?.let { it == path.destination.coordinate } != false)) {
                path
            } else {
                null
            }
        }
    ) ?: error("Could not find path")
}