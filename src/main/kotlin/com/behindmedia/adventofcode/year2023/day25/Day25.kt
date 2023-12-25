package com.behindmedia.adventofcode.year2023.day25

import com.behindmedia.adventofcode.common.*

fun main() {
    val connections = parseInput(read("/2023/day25.txt"))
    val total = connections.size
    val counter = defaultMutableMapOf<Set<String>, Int> { 0 }
    for (start in connections.keys) {
        reachAll(graph = connections, start = start, counter = counter)
    }
    val mostReached = counter.entries.sortedByDescending { it.value }
    for (i in 0 until mostReached.size) {
        for (j in i + 1 until mostReached.size) {
            for (k in j + 1 until mostReached.size) {
                val prunedConnections = connections.removingEdges(mostReached[i].key, mostReached[j].key, mostReached[k].key)
                val reached = reachAll(graph = prunedConnections, start = connections.keys.first(), counter = null)
                if (reached < total) {
                    println(reached * (total - reached))
                    return
                }
            }
        }
    }
}

private fun parseInput(string: String): Map<String, Set<String>> {
    return string.split("\n").filter { it.isNotBlank() }.fold(mutableMapOf<String, MutableSet<String>>()) { connections, line ->
        connections.apply {
            val (source, rest) = line.split(":")
            val destinations = rest.split(" ").filter { it.isNotBlank() }
            connections.getOrPut(source) { mutableSetOf() }.addAll(destinations)
            for (d in destinations) {
                connections.getOrPut(d) { mutableSetOf() }.add(source)
            }
        }
    }
}

private fun Map<String, Set<String>>.removingEdges(vararg edges: Set<String>): Map<String, Set<String>> {
    return this.mapValues { (k, v) ->
        val edge = edges.firstOrNull { k in it }
        if (edge == null) {
            v
        } else {
            v - (edge - k)
        }
    }
}

private fun reachAll(graph: Map<String, Set<String>>, start: String, counter: DefaultMutableMap<Set<String>, Int>?): Int {
    val seen = mutableSetOf<String>()
    val pending = ArrayDeque<Path<String>>()
    pending += Path(start, 0, null)
    seen += start
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        val edges = graph[current.destination]!!
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
