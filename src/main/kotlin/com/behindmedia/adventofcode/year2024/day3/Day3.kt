package com.behindmedia.adventofcode.year2024.day3

import com.behindmedia.adventofcode.common.*
import java.util.*

private val mulPattern = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
private val doPattern = """do\(\)""".toRegex()
private val dontPattern = """don't\(\)""".toRegex()

fun main() = timing {
    val data = read("/2024/day3.txt")
    val multiplications = mulPattern.findAll(data)
    val enableIndices = doPattern.findAll(data).map { it.range.first }.toCollection(sortedSetOf())
    val disableIndices = dontPattern.findAll(data).map { it.range.first }.toCollection(sortedSetOf())

    // Part 1
    println(process(multiplications, sortedSetOf(), sortedSetOf()))

    // Part 2
    println(process(multiplications, enableIndices, disableIndices))
}

private fun process(
    multiplications: Sequence<MatchResult>,
    enableIndices: TreeSet<Int>,
    disableIndices: TreeSet<Int>
): Long {
    return multiplications.sumOf { match ->
        val first = (match.groups[1]!!.value).toLong()
        val second = (match.groups[2]!!.value).toLong()
        val index = match.range.first
        val enabledIndex = enableIndices.lower(index)
        val disabledIndex = disableIndices.lower(index)
        // Take the max of the two
        val enabled = if (enabledIndex == null && disabledIndex == null) {
            true
        } else if (disabledIndex == null) {
            true
        } else if (enabledIndex == null) {
            false
        } else {
            enabledIndex > disabledIndex
        }
        if (enabled) {
            first * second
        } else {
            0L
        }
    }
}
