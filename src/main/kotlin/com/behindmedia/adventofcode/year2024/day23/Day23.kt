package com.behindmedia.adventofcode.year2024.day23

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val data = parseLines("/2024/day23.txt") { line ->
        line.splitTrimmed("-")
    }

    val connections = mutableMapOf<String, MutableSet<String>>()
    for ((first, second) in data) {
        connections.getOrPut(first) {
            mutableSetOf()
        }.add(second)
        connections.getOrPut(second) {
            mutableSetOf()
        }.add(first)
    }
    val groups = findGroups(connections, 3)

    // Part 1
    println(groups.count { group -> group.size == 3 && group.any { it.startsWith("t") }})

    // Part 2
    println(findAllMaximalCliques(connections).maxBy { it.size }.sorted().joinToString(","))
}

private data class Group(val last: String, val contents: Set<String>)

private fun findGroups(connections: Map<String, Set<String>>, maxSize: Int = Int.MAX_VALUE): Set<Set<String>> {
    val groups = mutableSetOf<Set<String>>()
    val seen = mutableSetOf<Set<String>>()
    for (start in connections.keys) {
        val pending = ArrayDeque<Group>()
        pending.add(Group(start, setOf(start)))
        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            if (current.contents.size > maxSize) {
                continue
            }
            if (!seen.add(current.contents)) {
                continue
            }
            val lastNode = current.last
            val destinations = connections[lastNode]!!
            for (destination in destinations) {
                if (destination in current.contents) {
                    // Recursion, save this set
                    if (current.contents.size in 3..maxSize) {
                        groups.add(current.contents)
                    }
                    continue
                }
                // The destination should have connections to all other nodes in the set
                val destinationConnections = connections[destination]!!
                if (destinationConnections.containsAll(current.contents)) {
                    // Ok valid destination
                    pending.add(Group(destination, current.contents + destination))
                }
            }
        }
    }
    return groups
}