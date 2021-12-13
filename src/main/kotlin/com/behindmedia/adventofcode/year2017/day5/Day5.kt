package com.behindmedia.adventofcode.year2017.day5

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parseLines("/2017/day5.txt") { line ->
        line.toInt()
    }
    println(part1(data.toMutableList()))
    println(part2(data.toMutableList()))
}

private fun part1(data: MutableList<Int>): Int {
    var pos = 0
    var steps = 0
    while (true) {
        val j = data.getOrNull(pos) ?: break
        data[pos]++
        pos += j
        steps++
    }
    return steps
}

private fun part2(data: MutableList<Int>): Int {
    var pos = 0
    var steps = 0
    while (true) {
        val j = data.getOrNull(pos) ?: break
        if (j >= 3) {
            data[pos]--
        } else {
            data[pos]++
        }
        pos += j
        steps++
    }
    return steps
}