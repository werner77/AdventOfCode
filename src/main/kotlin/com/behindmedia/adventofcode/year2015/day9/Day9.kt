package com.behindmedia.adventofcode.year2015.day9

import com.behindmedia.adventofcode.common.*
import kotlin.math.max
import kotlin.math.min

data class Connection(val source: String, val destination: String, val weight: Int) {
    fun other(city: String): String {
        return if (city == source) destination else source
    }
}

fun main() {
    val data = parseLines("/2015/day9.txt") { line ->
        val components = line.splitNonEmptySequence(" ", "to", "=").toList()
        require(components.size == 3)
        Connection(components[0], components[1], components[2].toInt())
    }
    val allCities: Set<String> = data.fold(mutableSetOf<String>()) { set, value ->
        set += value.source
        set += value.destination
        set
    }

    val connections = data.fold(defaultMutableMapOf<String, MutableSet<Connection>> { mutableSetOf() }) { m, v ->
        m.getOrPutDefault(v.source) += v
        m.getOrPutDefault(v.destination) += v
        m
    }

    var minPath = Int.MAX_VALUE
    for (start in allCities) {
        minPath = min(minPath, minPath(start, allCities.toMutableSet(), connections))
    }
    println(minPath)

    var maxPath = Int.MIN_VALUE
    for (start in allCities) {
        maxPath = max(maxPath, maxPath(start, allCities.toMutableSet(), connections))
    }
    println(maxPath)
}

private fun maxPath(
    current: String,
    left: MutableSet<String>,
    connections: Map<String, Set<Connection>>
): Int {
    left -= current
    try {
        if (left.isEmpty()) {
            return 0
        }
        var maxPath = Int.MIN_VALUE
        for (conn in connections[current] ?: emptySet()) {
            val destination = conn.other(current)
            val weight = conn.weight
            if (destination in left) {
                maxPath = max(maxPath, maxPath(destination, left, connections) + weight)
            }
        }
        return maxPath
    } finally {
        left += current
    }
}

private fun minPath(
    current: String,
    left: MutableSet<String>,
    connections: Map<String, Set<Connection>>
): Int {
    left -= current
    try {
        if (left.isEmpty()) {
            return 0
        }
        var minPath = Int.MAX_VALUE
        for (conn in connections[current] ?: emptySet()) {
            val destination = conn.other(current)
            val weight = conn.weight
            if (destination in left) {
                minPath = min(minPath, minPath(destination, left, connections) + weight)
            }
        }
        return minPath
    } finally {
        left += current
    }
}