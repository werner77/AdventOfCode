package com.behindmedia.adventofcode.year2017.day12

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2017/day12.txt") { line ->
        val components = line.splitNonEmptySequence ("<->").map { it.trim() }.toList()

        val origin = components[0].toInt()
        val destinations = components[1].split(", ").map { it.toInt() }
        Pair(origin, destinations)
    }.fold(mutableMapOf<Int, Set<Int>>()) { map, value ->
        map.apply {
            this[value.first] = value.second.toSet()
        }
    }

    var groupCount = 0
    var zeroGroupSize = 0
    val totalSeen = mutableSetOf<Int>()
    for (start in data.keys) {
        if (start in totalSeen) continue
        val pending = mutableSetOf(start)
        val seen = mutableSetOf(start)
        var size = 0
        while (pending.isNotEmpty()) {
            val current = pending.popFirst() ?: break
            size++
            for (d in data[current] ?: emptySet()) {
                if (d !in seen) {
                    pending.add(d)
                    seen.add(d)
                }
            }
        }
        groupCount++
        if (0 in seen) zeroGroupSize = seen.size
        totalSeen += seen
    }

    println(zeroGroupSize)
    println(groupCount)
}