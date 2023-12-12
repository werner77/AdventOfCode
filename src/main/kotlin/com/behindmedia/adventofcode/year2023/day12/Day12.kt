package com.behindmedia.adventofcode.year2023.day12

import com.behindmedia.adventofcode.common.CacheSupport.withCaching
import com.behindmedia.adventofcode.common.DefaultList
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.times
import com.behindmedia.adventofcode.common.timing
import com.behindmedia.adventofcode.common.withDefaultValue

data class State(
    val charIndex: Int,
    val lastGroupIndex: Int,
    val lastGroupSize: Int,
    val isGroupActive: Boolean
) {
    fun nextState(inGroup: Boolean): State {
        return copy(
            charIndex = charIndex + 1,
            lastGroupIndex = if (!isGroupActive && inGroup) lastGroupIndex + 1 else lastGroupIndex,
            lastGroupSize = if (!isGroupActive && inGroup) 1 else if (inGroup) lastGroupSize + 1 else lastGroupSize,
            isGroupActive = inGroup
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
        return Record(value = value.times(multiplier, "?"), groups = (groups * multiplier).withDefaultValue { 0 })
    }

    fun possibleArrangements(): Long {
        return process(
            state = State(
                charIndex = 0,
                lastGroupIndex = -1,
                lastGroupSize = 0,
                isGroupActive = false)
        )
    }

    private fun process(state: State): Long = withCaching(state) {
        if (state.lastGroupIndex >= groups.size) {
            0L
        } else if (!state.isGroupActive && state.lastGroupSize < groups[state.lastGroupIndex]) {
            0L
        } else if (state.lastGroupSize > groups[state.lastGroupIndex]) {
            0L
        } else if (state.charIndex == value.length) {
            // Check for completeness
            if (state.lastGroupSize == groups[state.lastGroupIndex] && state.lastGroupIndex == groups.size - 1) {
                1L
            } else {
                0L
            }
        } else {
            var total = 0L
            when (value[state.charIndex]) {
                '?' -> {
                    // Wildcard, process both options
                    total += process(state = state.nextState(inGroup = false))
                    total += process(state = state.nextState(inGroup = true))
                }
                '.' -> {
                    // Finish group if possible
                    total += process(state = state.nextState(inGroup = false))
                }
                '#' -> {
                    // Increase group count
                    total += process(state = state.nextState(inGroup = true))
                }
            }
            total
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
    }

    timing {
        // Part 2
        println(data.map { it * 5 }.sumOf { it.possibleArrangements() })
    }
}
