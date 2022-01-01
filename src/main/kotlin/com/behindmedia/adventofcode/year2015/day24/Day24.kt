package com.behindmedia.adventofcode.year2015.day24

import com.behindmedia.adventofcode.common.*

// All values in values left should be < current value
private fun findFittingSets(
    target: Long,
    values: LongArray,
    startIndex: Int,
    sum: Long,
    currentSet: Set<Long>,
    perform: (Set<Long>) -> Unit
) {
    if (sum == target) {
        perform(currentSet)
    } else {
        for (i in startIndex until values.size) {
            val next = values[i]
            val nextSum = sum + next
            if (nextSum <= target) {
                findFittingSets(
                    target,
                    values,
                    i + 1,
                    nextSum,
                    currentSet + next,
                    perform
                )
            }
        }
    }
}

private fun solve(data: List<Long>, groupCount: Long): Long {
    val totalSum = data.sum()

    if (totalSum % groupCount != 0L) error("Cannot divide in exactly $groupCount groups")

    val target = totalSum / groupCount
    val possibleFirstSets = mutableListOf<Set<Long>>()
    var minLength = Int.MAX_VALUE

    findFittingSets(target, data.sortedDescending().toLongArray(), 0, 0L, emptySet()) {
        if (it.size <= minLength) {
            minLength = it.size
            possibleFirstSets += it
        }
    }
    possibleFirstSets.removeIf { it.size != minLength }
    possibleFirstSets.sortBy { it.product() }
    return possibleFirstSets.first().product()
}

fun main() {
    val data = parseLines("/2015/day24.txt") { line ->
        line.toLong()
    }

    println(solve(data, 3))
    println(solve(data, 4))
}