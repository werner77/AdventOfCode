package com.behindmedia.adventofcode.year2018.day18

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseMap("/2018/day18.txt") { line ->
        line
    }
    part1(data)
    part2(data)
}

private fun part1(data: Map<Coordinate, Char>) {
    val result = simulate(data) { i, state ->
        if (i == 10) {
            state.resourceValue
        } else {
            null
        }
    }
    println(result)
}


private fun part2(data: Map<Coordinate, Char>) {
    val seen = mutableMapOf<Map<Coordinate, Char>, Int>(
        data to 0
    )
    val target = 1000000000L
    val numberOfIterationsNeeded = simulate(data) { i, state ->
        val existing = seen[state]
        if (existing != null) {
            val period = i - existing
            val iterationsAfterInitialRepetitionState = target - i
            (iterationsAfterInitialRepetitionState % period + existing).toInt()
        } else {
            seen[state] = i
            null
        }
    }
    val ans = simulate(data) { i, state ->
        if (i == numberOfIterationsNeeded) {
            state.resourceValue
        } else {
            null
        }
    }
    println(ans)
}

private val Map<Coordinate,Char>.resourceValue: Int
    get() {
        val trees = this.count { it.value == '|' }
        val lumberyards = this.count { it.value == '#' }
        return trees * lumberyards
    }

private fun <T: Any>simulate(input: Map<Coordinate, Char>, predicate: (Int, Map<Coordinate, Char>) -> T?): T {
    var state = input
    var i = 0
    while(true) {
        state = simulate(state)
        i++
        return predicate(i, state) ?: continue
    }
}

/*
An open acre will become filled with trees if three or more adjacent acres contained trees. Otherwise, nothing happens.
An acre filled with trees will become a lumberyard if three or more adjacent acres were lumberyards. Otherwise, nothing happens.
An acre containing a lumberyard will remain a lumberyard if it was adjacent to at least one other lumberyard and at least one acre containing trees. Otherwise, it becomes open.
 */
private fun simulate(input: Map<Coordinate, Char>): Map<Coordinate, Char> {
    val result = mutableMapOf<Coordinate, Char>()

    for (c in input.keys.range()) {
        var treeCount = 0
        var lumberyardCount = 0
        for (d in c.allNeighbours) {
            if (input[d] == '|') treeCount++
            if (input[d] == '#') lumberyardCount++
        }
        val value = input[c]
        val nextValue = when (value) {
            '.' -> {
                if (treeCount >= 3) '|' else value
            }

            '|' -> {
                if (lumberyardCount >= 3) '#' else value
            }

            '#' -> {
                if (lumberyardCount >= 1 && treeCount >= 1) '#' else '.'
            }

            else -> error("Unexpected value: $value")
        }
        result[c] = nextValue
    }
    return result
}

