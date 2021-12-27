package com.behindmedia.adventofcode.year2015.day6

import com.behindmedia.adventofcode.common.*

enum class Action {
    On, Off, Toggle
}

data class Instruction(val range: CoordinateRange, val action: Action)

/*
turn off 838,342 through 938,444
turn on 144,431 through 260,951
toggle 780,318 through 975,495
 */
fun main() {
    val data = parseLines("/2015/day6.txt") { line ->
        val action = if (line.startsWith("turn off")) {
            Action.Off
        } else if (line.startsWith("turn on")) {
            Action.On
        } else if (line.startsWith("toggle")) {
            Action.Toggle
        } else {
            error("Invalid instruction: $line")
        }
        val components = line.splitNonEmptySequence(" ", ",").mapNotNull { it.toIntOrNull() }.toList()
        require(components.size == 4)
        val (x1, y1, x2, y2) = components
        Instruction(Coordinate(x1, y1)..Coordinate(x2, y2), action)
    }

    println(part1(data).count { it.value == '#' })

    println(part2(data).sumOf { it.value })
}

private fun part1(data: List<Instruction>): CharMap {
    val state = CharMap(1000) { _, _ -> '.' }
    for (instr in data) {
        for (c in instr.range) {
            when (instr.action) {
                Action.On -> {
                    state[c] = '#'
                }
                Action.Off -> {
                    state[c] = '.'
                }
                Action.Toggle -> {
                    state[c] = if (state[c] == '.') '#' else '.'
                }
            }
        }
    }
    return state
}

private fun part2(data: List<Instruction>): ValueMap<Long> {
    val state = ValueMap<Long>(squareSize = 1000) { _, _ -> 0L }
    for (instr in data) {
        for (c in instr.range) {
            when (instr.action) {
                Action.On -> {
                    state[c] += 1L
                }
                Action.Off -> {
                    if (state[c] > 0) state[c] -= 1L
                }
                Action.Toggle -> {
                    state[c] += 2L
                }
            }
        }
    }
    return state
}