package com.behindmedia.adventofcode.year2021.day10

import com.behindmedia.adventofcode.common.*

private val errorScoreTable = mapOf<Char, Int>(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)

private val completeScoreTable = mapOf<Char, Int>(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4
)

private val markerTable = mapOf<Char, Char>(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)

private fun validate(line: String): Pair<Long?, Long?> {
    val stack = ArrayDeque<Char>()
    for (c in line) {
        if (markerTable.containsKey(c)) {
            // Opening marker
            stack.add(c)
        } else {
            val last = stack.lastOrNull() ?: error("Non-opening char found while stack is empty: $c")
            if (c == markerTable[last]) {
                // Correct closing char
                stack.removeLast()
            } else {
                // Incorrect closing char
                return errorScoreTable[c]?.let { Pair(it.toLong(), null) }
                    ?: error("Could not find error score for char: $c")
            }
        }
    }
    var score = 0L
    while (stack.isNotEmpty()) {
        val last = stack.removeLast()
        val c = markerTable[last] ?: error("Character not found in marker table: $last")
        score = score * 5 + (completeScoreTable[c] ?: error("No completion score found for character: $c"))
    }
    return Pair(null, score)
}

fun main() {
    val data = parseLines("/2021/day10.txt") { line ->
        validate(line)
    }

    // Part 1
    println(data.mapNotNull { it.first }.sum())

    val validScores = data.mapNotNull { it.second }.sorted()
    // Part 2
    println(validScores[validScores.size / 2])
}