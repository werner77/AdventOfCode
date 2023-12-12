package com.behindmedia.adventofcode.year2023.day12

import com.behindmedia.adventofcode.common.*

data class Record(val value: String, val counts: List<Int>) {

    val total = counts.sum()
    val questionMarkIndices = value.withIndex().filter { it.value == '?' }.map { it.index }
    fun possibleArrangements(): Long {
        return process(0, MutableList(questionMarkIndices.size) { '.' })
    }

    private fun process(index: Int, questionMarkValues: MutableList<Char>): Long {
        if (index == questionMarkValues.size) {
            val groups = groups(questionMarkValues)
            if (counts == groups) {
                return 1
            } else {
                return 0
            }
        }

        var total = 0L
        questionMarkValues[index] = '#'
        total += process(index + 1, questionMarkValues)
        questionMarkValues[index] = '.'
        total += process(index + 1, questionMarkValues)
        return total
    }

    private fun groups(questionMarkValues: List<Char>): List<Int> {
        var currentGroup = 0
        val result = mutableListOf<Int>()
        var j = 0
        for (i in 0 until value.length) {
            val c = if (j < questionMarkIndices.size && i == questionMarkIndices[j]) {
                questionMarkValues[j++]
            } else {
                value[i]
            }
            if (c == '#') {
                currentGroup++
            } else if (currentGroup > 0) {
                result += currentGroup
                currentGroup = 0
            }
        }
        if (currentGroup > 0) {
            result += currentGroup
        }
        return result
    }
}

fun main() {
    val data = parseLines("/2023/day12.txt") { line ->
        val (value, counts) = line.split(" ")
        Record(value, counts.split(",").map { it.toInt() })
    }

    println(data.sumOf { it.possibleArrangements() })
}