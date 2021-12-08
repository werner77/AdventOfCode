package com.behindmedia.adventofcode.year2021.day8

import com.behindmedia.adventofcode.common.*

// All wires represented by a character
private val wires = setOf('a', 'b', 'c', 'd', 'e', 'f', 'g')

// Map of set of characters to corresponding digit
private val digitMap = mapOf<Set<Char>, Int>(
    setOf('a', 'b', 'c', 'e', 'f', 'g') to 0,
    setOf('c', 'f') to 1,
    setOf('a', 'c', 'd', 'e', 'g') to 2,
    setOf('a', 'c', 'd', 'f', 'g') to 3,
    setOf('b', 'c', 'd', 'f') to 4,
    setOf('a', 'b', 'd', 'f', 'g') to 5,
    setOf('a', 'b', 'd', 'e', 'f', 'g') to 6,
    setOf('a', 'c', 'f') to 7,
    setOf('a', 'b', 'c', 'd', 'e', 'f', 'g') to 8,
    setOf('a', 'b', 'c', 'd', 'f', 'g') to 9,
)

private val Char.wireIndex: Int
    get() = this - 'a'

// Mapping of scrambled wires (a -> b) for each wire
typealias WireMapping = List<Char>

private fun WireMapping.validate(input: List<String>): Boolean {
    val allDigits = digitMap.values.toMutableSet()
    for (entry in input) {
        // Try to represent this digit with the mapping
        val s = entry.mapTo(HashSet(entry.length, 1.0f)) { this[it.wireIndex] }
        val d = digitMap[s] ?: return false
        allDigits.remove(d)
    }
    return allDigits.isEmpty()
}

private fun WireMapping.decode(output: List<String>): Int {
    var total = 0
    for (entry in output) {
        val s = entry.mapTo(HashSet(entry.length, 1.0f)) { this[it.wireIndex] }
        val d = digitMap[s] ?: error("Could not decode digit: $s")
        total = total * 10 + d
    }
    return total
}

private fun decode(scrambled: Pair<List<String>, List<String>>): Int {
    val (input, output) = scrambled
    return permutateUnique(wires) { mapping ->
        // Validate the mapping
        if (mapping.validate(input)) {
            // If correct decode
            mapping.decode(output)
        } else {
            null
        }
    } ?: error("Could not find valid mapping")
}

fun main() {
    val data = parseLines("/2021/day8.txt") { line ->
        val (input, output) = line.split("|")
        val inputDigits = input.splitNonEmptySequence(" ").toList()
        val outputDigits = output.splitNonEmptySequence(" ").toList()
        Pair(inputDigits, outputDigits)
    }

    timing {
        // Part 1
        println(part1(data))

        // Part 2
        println(part2(data))
    }
}

private fun part1(data: List<Pair<List<String>, List<String>>>): Int {
    return data.fold(0) { count, line ->
        count + line.second.count { it.length in listOf(2, 3, 4, 7) }
    }
}

private fun part2(data: List<Pair<List<String>, List<String>>>): Int {
    return data.sumOf {
        decode(it)
    }
}

