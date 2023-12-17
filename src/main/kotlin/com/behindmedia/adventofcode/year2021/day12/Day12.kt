package com.behindmedia.adventofcode.year2021.day12

import com.behindmedia.adventofcode.common.*

val String.isUppercase: Boolean
    get() = all { it.isUpperCase() }

val String.isLowercase: Boolean
    get() = all { it.isLowerCase() }

data class Node(val location: String, val visited: List<String>)

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

    timing {
        // Part 1
        println(findPaths(connections, false))

        // Part 2
        println(findPaths(connections, true))
    }
}

private fun findPaths(
    connections: MutableMap<String, MutableSet<String>>,
    allowSingleLowercaseLocationTwice: Boolean
): Int {
    var pathCount = 0
    val pending = ArrayDeque<Path<String>>()
    pending.add(Path("start", 0, null))
    while (pending.isNotEmpty()) {
        val currentPath = pending.removeFirst()
        if (currentPath.destination == "end") {
            pathCount++
            continue
        }
        val currentTraversed = currentPath.nodes { it.isLowercase }
        val currentTraversedSet = currentTraversed.toSet()
        val neighbours = connections[currentPath.destination] ?: emptySet()
        for (neighbour in neighbours) {
            if (neighbour !in currentTraversedSet ||
                (allowSingleLowercaseLocationTwice && neighbour !in listOf(
                    "start",
                    "end"
                ) && currentTraversed.size == currentTraversedSet.size)
            ) {
                pending.addFirst(Path(neighbour, currentPath.length + 1, currentPath))
            }
        }
    }
    return pathCount
}
