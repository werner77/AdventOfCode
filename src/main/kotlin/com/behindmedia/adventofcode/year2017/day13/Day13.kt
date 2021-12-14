package com.behindmedia.adventofcode.year2017.day13

import com.behindmedia.adventofcode.common.*

private fun printState(pos: Int, state: Map<Int, Pair<Int, Boolean>>, data: Map<Int, Int>) {
    val maxPos = state.keys.max()
    val maxRange = data.values.max()
    for (j in 0 .. maxRange) {
        for (i in 0 ..maxPos) {
            val range = data[i] ?: 0
            if (i > 0) print("\t")
            if (j < range) {
                if (i == pos && j == 0) {
                    print(if (state[i]!!.first == j) "(S)" else "( )")
                } else {
                    print(if (state[i]!!.first == j) "[S]" else "[ ]")
                }
            } else if (i == pos && j == 0) {
                print("( )")
            }
        }
        println()
    }
}

private fun simulate(delay: Int = 0, data: Map<Int, Int>): Int {
    val state: MutableMap<Int, Pair<Int, Boolean>> = data.entries.fold(mutableMapOf()) { map, value ->
        val (depth, _) = value
        map[depth] = Pair(0, true)
        map
    }
    val maxPos = state.keys.max()
    var pos = -delay
    var severity = 0
    while (true) {
        //printState(pos, state, data)
        val range = data[pos]
        if (range != null) {
            val s = state[pos] ?: error ("No state found at depth: $pos")
            if (s.first == 0) {
                severity += range * pos
            }
        }
        for (entry in state) {
            val depth = entry.key
            val entryRange = data[depth] ?: error("No range found at depth: ${depth}")
            val scannerState = state[depth] ?: error("No state found at depth: ${depth}")
            val scannerPosition = scannerState.first
            var scannerDirection = scannerState.second
            if ((scannerDirection && scannerPosition == entryRange - 1) || (!scannerDirection && scannerPosition == 0)) {
                scannerDirection = !scannerDirection
            }
            state[depth] = Pair(if (scannerDirection) scannerPosition + 1 else scannerPosition - 1, scannerDirection)
        }
        pos++
        if (pos > maxPos) break
    }
    return severity
}

private fun findOptimum(data: Map<Int, Int>): Int {
    var delay = 0
    while (true) {
        var valid = true
        for (entry in data) {
            val (depth, range) = entry
            val t = depth + delay
            if (t % (range * 2 - 2) == 0) {
                valid = false
                break
            }
        }
        if (valid) return delay
        delay++
    }
}

fun main() {
    val data = parseLines("/2017/day13.txt") { line ->
        val (depth, range) = line.split(":").map { it.trim() }.map { it.toInt() }
        Pair(depth, range)
    }.fold(mutableMapOf<Int, Int>()) { map, pair ->
        map[pair.first] = pair.second
        map
    }
    val severity = simulate(0, data)

    // Part 1
    println(severity)

    // Part 2
    println(findOptimum(data))
}
