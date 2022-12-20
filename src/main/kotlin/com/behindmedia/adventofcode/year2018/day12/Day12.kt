package com.behindmedia.adventofcode.year2018.day12

import com.behindmedia.adventofcode.common.*

fun main() {
    lateinit var initialState: String
    val patterns = mutableListOf<Pair<String, String>>()
    parseLines("/2018/day12.txt") { line ->
        val prefix = "initial state: "
        if (line.startsWith(prefix)) {
            initialState = line.substring(prefix.length).trim()
        } else if (line.isNotBlank()) {
            val (first, second) = line.split(" => ")
            patterns += Pair(first.trim(), second.trim())
        }
    }
    // Part 1
    part1(initialState, patterns)

    part2(initialState, patterns)
}

private fun part1(
    initialState: String,
    patterns: MutableList<Pair<String, String>>
) {
    val ans = simulate(initialState = initialState, patterns = patterns) { state, i ->
        if (i == 20) state.plantIndices.sumOf { it + state.offset } else null
    }
    println(ans)
}

private fun part2(
    initialState: String,
    patterns: MutableList<Pair<String, String>>
) {
    val seen = mutableMapOf<Set<Int>, Pair<Int, Int>>()
    val ans = simulate(initialState = initialState, patterns = patterns) { state, i ->
        val previous = seen[state.plantIndices]
        if (previous != null) {
            val target = 50000000000L
            val numberOfIterations = target - i
            val targetOffset = state.offset.toLong() + (state.offset.toLong() - previous.second.toLong()) * numberOfIterations
            state.plantIndices.sumOf { it.toLong() + targetOffset }
        } else {
            seen[state.plantIndices] = Pair(i, state.offset)
            null
        }
    }
    println(ans)
}

// PlantIndices which start at 0 and an offset for the 0 position. So real index = plantIndex + offset
private data class State(val plantIndices: Set<Int>, val offset: Int)

private fun <T> simulate(initialState: String, patterns: List<Pair<String, String>>, predicate: (State, Int) -> T?): T {
    // State contains the indices of the plants
    var state: Set<Int> = initialState.withIndex().filter { (_, c) -> c == '#' }.map { (i, _) -> i }.toSet()
    val nextState = mutableSetOf<Int>()
    var iteration = 1
    var offset = 0
    while (true) {
        val maxIndex = state.max()
        var offsetDelta = 0
        for (i in -2 until maxIndex + 2) {
            for ((match, outcome) in patterns) {
                if (outcome != "#") continue
                var foundMatch = true
                for (j in 0 until 5) {
                    val c1 = if (state.contains(i - 2 + j)) '#' else '.'
                    val c2 = match[j]
                    if (c1 != c2) {
                        foundMatch = false
                        break
                    }
                }
                if (foundMatch) {
                    // Replace
                    if (nextState.isEmpty()) {
                        // first element
                        offsetDelta = i
                    }
                    nextState.add(i - offsetDelta)
                    break
                }
            }
        }
        offset += offsetDelta
        state = nextState.toSet()
        nextState.clear()
        val s = State(state, offset)
        val result = predicate(s, iteration)
        if (result != null) {
            return result
        }
        iteration++
    }
}

private fun Set<Int>.asString(): String {
    val buffer = StringBuilder(this.size + 4)
    val minIndex = this.min()
    val maxIndex = this.max()
    for (i in minIndex - 2..maxIndex + 2) {
        if (i in this) {
            buffer.append('#')
        } else {
            buffer.append('.')
        }
    }
    return buffer.toString()
}