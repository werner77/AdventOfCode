package com.behindmedia.adventofcode.year2015.day17

import com.behindmedia.adventofcode.common.parseLines

private fun solve(target: Int, current: Int, indexesLeft: Set<Int>, indexesDone: Set<Int>, values: IntArray, found: MutableSet<Set<Int>>) {
    if (current == target) {
        found += indexesDone
    } else if (current > target) {
        return
    }
    for (index in indexesLeft) {
        val next = values[index]
        solve(target, current + next, indexesLeft - index, indexesDone + index, values, found)
    }
}

private fun solve(target: Int, values: List<Int>): Set<Set<Int>> {
    val found = mutableSetOf<Set<Int>>()
    solve(target, 0, values.indices.toSet(), setOf(), values.toIntArray(), found)
    return found
}

fun main() {
    val data = parseLines("/2015/day17.txt") { line ->
        line.toInt()
    }
    val result = solve(150, data)
    println(result.size)

    val minCount = result.minOf { it.size }
    println(result.count { it.size == minCount })
}