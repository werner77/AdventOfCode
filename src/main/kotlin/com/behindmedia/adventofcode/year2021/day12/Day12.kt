package com.behindmedia.adventofcode.year2021.day12

import com.behindmedia.adventofcode.common.*

data class Node(val location: String, val visited: List<String>) {

    private val maxLowercaseVisitedCount: Int by lazy {
        visited.fold(mutableMapOf<String, Int>()) { map, value ->
            map[value] = (map[value] ?: 0) + 1
            map
        }.asSequence().filter { it.key.lowercase() == it.key }.maxOf { it.value }
    }

    fun canBeVisited(location: String, allowSingleLowercaseLocationTwice: Boolean): Boolean {
        return location.uppercase() == location ||
                !visited.contains(location) ||
                (allowSingleLowercaseLocationTwice && maxLowercaseVisitedCount == 1 && location != "start" && location != "end")
    }

}


fun main() {
    val connections = parseLines("/2021/day12.txt") { line ->
        line.split("-")
    }.fold(mutableMapOf<String, MutableSet<String>>()) { map, components ->
        map.apply {
            for (i in 0..1) {
                getOrPut(components[i % 2]) { mutableSetOf() } += components[(i + 1) % 2]
            }
        }
    }

    // Part 1
    println(findPaths(connections, false))

    // Part 2
    println(findPaths(connections, true))
}

private fun findPaths(
    connections: MutableMap<String, MutableSet<String>>,
    allowSingleLowercaseLocationTwice: Boolean
): Int {
    var pathCount = 0
    val startNode = Node("start", listOf("start"))

    reachableNodes(
        from = startNode,
        neighbours = { path ->
            val currentNode = path.destination
            val currentLocation = currentNode.location
            connections[currentLocation]?.filter { neighbour ->
                currentNode.canBeVisited(neighbour, allowSingleLowercaseLocationTwice)
            }?.map {
                Node(it, currentNode.visited + it)
            } ?: emptySet()
        },
        reachable = { true },
        process = { path ->
            if (path.destination.location == "end") {
                pathCount++
            }
            null
        }
    )
    return pathCount
}
