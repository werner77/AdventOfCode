package com.behindmedia.adventofcode.year2021.day14

import com.behindmedia.adventofcode.common.*

private typealias PolymerPair = Pair<Char, Char>

private fun applyRules(
    pairCounts: DefaultMap<PolymerPair, Long>,
    charCounts: DefaultMutableMap<Char, Long>,
    rules: Map<PolymerPair, Char>,
): DefaultMap<PolymerPair, Long> {
    val output = defaultMutableMapOf<PolymerPair, Long> { 0L }
    for ((pair, count) in pairCounts.entries.toList()) {
        val insert = rules[pair]
        if (insert != null) {
            // Split in two pairs
            val p1 = Pair(pair.first, insert)
            val p2 = Pair(insert, pair.second)
            charCounts[insert] += count
            output[p1] += count
            output[p2] += count
        }
    }
    return output
}

private fun extractPolymerPairCounts(input: String): DefaultMap<PolymerPair, Long> {
    val result = defaultMutableMapOf<PolymerPair, Long> { 0L }
    for ((i, c) in input.withIndex()) {
        val j = i + 1
        if (j < input.length) {
            val p = Pair(c, input[j])
            result[p] += 1L
        }
    }
    return result
}

fun main() {
    var parsingRules = false
    lateinit var initialState: String
    val rules = mutableMapOf<Pair<Char, Char>, Char>()
    parseLines("/2021/day14.txt") { line ->
        if (line.isBlank()) {
            parsingRules = true
        } else if (!parsingRules) {
            initialState = line
        } else {
            val components = line.split(" -> ")
            require(components.size == 2) { "Unexpected line: $line" }
            val (first, second) = components
            require(first.length == 2) { "Unexpected line: $line" }
            rules[Pair(first[0], first[1])] = second.single()
        }
    }

    // Part 1
    println(applyRules(initialState, rules, 10))

    // Part 2
    println(applyRules(initialState, rules, 40))
}

private fun applyRules(
    initialState: String,
    rules: Map<PolymerPair, Char>,
    iterationCount: Int
): Long {
    val charCounts = initialState.fold(defaultMutableMapOf<Char, Long> { 0L }) { map, c ->
        map.apply {
            this[c] += 1L
        }
        map
    }
    var pairCounts = extractPolymerPairCounts(initialState)
    for (i in 0 until iterationCount) {
        pairCounts = applyRules(pairCounts, charCounts, rules)
    }
    return charCounts.values.max() - charCounts.values.min()
}