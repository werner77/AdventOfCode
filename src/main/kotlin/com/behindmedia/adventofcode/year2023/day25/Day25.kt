package com.behindmedia.adventofcode.year2023.day25

import com.behindmedia.adventofcode.common.*

fun main() {
    val connections = mutableMapOf<String, MutableSet<String>>()
    parseLines("/2023/day25.txt") { line ->
        val (source, rest) = line.split(":")
        val destinations = rest.split(" ").filter { it.isNotBlank() }
        connections.getOrPut(source) { mutableSetOf() }.addAll(destinations)
        for (d in destinations) {
            connections.getOrPut(d) { mutableSetOf() }.add(source)
        }
    }
    val total = connections.size
    val counter = defaultMutableMapOf<Set<String>, Int> { 0 }
    for (start in connections.keys) {
        reachAll(start = start, connections = connections, counter = counter)
    }
    val mostReached = counter.entries.sortedByDescending { it.value }
    for (i in 0 until mostReached.size) {
        for (j in i + 1 until mostReached.size) {
            for (k in j + 1 until mostReached.size) {
                val prunedConnections = connections.removingEdges(mostReached[i].key, mostReached[j].key, mostReached[k].key)
                val reached = reachAll(start = connections.keys.first(), connections = prunedConnections, counter = null)
                if (reached < total) {
                    println(reached * (total - reached))
                    return
                }
            }
        }
    }
}

private fun Map<String, Set<String>>.removingEdges(vararg edges: Set<String>): Map<String, Set<String>> {
    val result = mutableMapOf<String, Set<String>>()
    for ((k, v) in this) {
        val edge = edges.firstOrNull { k in it }
        if (edge == null) {
            result[k] = v
        } else {
            val (first, last) = edge.toList()
            when (k) {
                first -> {
                    result[k] = v - last
                }
                last -> {
                    result[k] = v - first
                }
                else -> {
                    error("Should never happen")
                }
            }
        }
    }
    return result
}

private fun reachAll(start: String, connections: Map<String, Set<String>>, counter: DefaultMutableMap<Set<String>, Int>?): Int {
    val seen = mutableSetOf<String>()
    val pending = ArrayDeque<Path<String>>()
    pending += Path(start, 0, null)
    seen += start
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        val edges = connections[current.destination]!!
        for (e in edges) {
            if (e in seen) continue
            seen += e
            // Increment the counter of this edge
            if (counter != null) {
                counter[setOf(current.destination, e)]++
            }
            pending += Path(e, current.length + 1, current)
        }
    }
    return seen.size
}
