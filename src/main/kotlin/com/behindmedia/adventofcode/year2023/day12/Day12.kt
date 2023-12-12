package com.behindmedia.adventofcode.year2023.day12

import com.behindmedia.adventofcode.common.DefaultList
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.timing
import com.behindmedia.adventofcode.common.withDefaultValue

data class State(
    val charIndex: Int,
    val lastGroupIndex: Int,
    val lastGroupSize: Int,
    val isGroupActive: Boolean,
    val remainingQuestionMarks: Int
) {
    fun nextState(inGroup: Boolean, isQuestionMark: Boolean): State {
        return copy(
            charIndex = charIndex + 1,
            lastGroupIndex = if (!isGroupActive && inGroup) lastGroupIndex + 1 else lastGroupIndex,
            lastGroupSize = if (!isGroupActive && inGroup) 1 else if (inGroup) lastGroupSize + 1 else lastGroupSize,
            isGroupActive = inGroup,
            remainingQuestionMarks = if (isQuestionMark) remainingQuestionMarks - 1 else remainingQuestionMarks
        )
    }
}

data class Record(val value: String, val groups: DefaultList<Int>) {
    companion object {
        operator fun invoke(line: String): Record {
            val (value, counts) = line.split(" ")
            return Record(value = value, groups = counts.split(",").map { it.toInt() }.withDefaultValue { 0 })
        }
    }

    operator fun times(multiplier: Int): Record {
        return Record(value = value * multiplier, groups = (groups * multiplier).withDefaultValue { 0 })
    }

    fun possibleArrangements(): Long {
        return process(
            state = State(
                charIndex = 0,
                lastGroupIndex = -1,
                lastGroupSize = 0,
                isGroupActive = false,
                remainingQuestionMarks = value.count { it == '?' }),
            cache = hashMapOf()
        )
    }

    private fun process(state: State, cache: MutableMap<State, Long>): Long {
        if (state.lastGroupIndex >= groups.size) {
            return 0L
        }
        if (!state.isGroupActive && state.lastGroupSize < groups[state.lastGroupIndex]) {
            return 0L
        }
        if (state.lastGroupSize > groups[state.lastGroupIndex]) {
            return 0L
        }
        if (state.remainingQuestionMarks < 0) {
            return 0L
        }
        if (state.charIndex == value.length) {
            // Check for completeness
            return if (state.remainingQuestionMarks == 0 && state.lastGroupSize == groups[state.lastGroupIndex] && state.lastGroupIndex == groups.size - 1) {
                1L
            } else {
                0L
            }
        }
        return cache[state] ?: run {
            var total = 0L
            when (value[state.charIndex]) {
                '?' -> {
                    // Wildcard, process both options
                    total += process(state = state.nextState(inGroup = false, isQuestionMark = true), cache = cache)
                    total += process(state = state.nextState(inGroup = true, isQuestionMark = true), cache = cache)
                }

                '.' -> {
                    // Finish group if possible
                    total += process(state = state.nextState(inGroup = false, isQuestionMark = false), cache = cache)
                }

                '#' -> {
                    // Increase group count
                    total += process(state = state.nextState(inGroup = true, isQuestionMark = false), cache = cache)
                }
            }
            // Store result in cache
            total.also {
                cache[state] = it
            }
        }
    }
}

fun main() {
    val data = parseLines("/2023/day12.txt") { line ->
        Record(line)
    }
    timing {
        // Part 1
        println(data.sumOf { it.possibleArrangements() })

        // Part 2
        println(data.map { it * 5 }.sumOf { it.possibleArrangements() })
    }
}

private operator fun List<Int>.times(multiplier: Int): List<Int> {
    val result = ArrayList<Int>(this.size * multiplier)
    repeat(multiplier) {
        result += this
    }
    return result
}

private operator fun String.times(multiplier: Int): String {
    val result = StringBuilder()
    repeat(multiplier) {
        if (result.isNotEmpty()) {
            result.append('?')
        }
        result.append(this)
    }
    return result.toString()
}