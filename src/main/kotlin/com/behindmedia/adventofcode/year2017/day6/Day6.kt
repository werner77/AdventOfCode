package com.behindmedia.adventofcode.year2017.day6

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun cycle(input: List<Int>): List<Int> {
    val output = input.toMutableList()
    val maxValue = input.max()
    val maxIndex = input.indexOfFirst { it == maxValue }
    var count = output[maxIndex]
    output[maxIndex] = 0
    var i = maxIndex
    while (count > 0) {
        val j = (++i % input.size)
        output[j]++
        count--
    }
    return output
}

fun main() {
    val data = parse("/2017/day6.txt") { line ->
        line.trim().split(" ", "\t").map { it.toInt() }
    }
    val seen = mutableMapOf(data to 1)
    var current = data
    var count = 0
    var firstRepeat: Int? = null
    while (true) {
        current = cycle(current)
        count++
        val seenCount = seen[current]
        if (seenCount == 1 && firstRepeat == null) {
            // Part 1
            println(count)
            firstRepeat = count
        } else if (seenCount == 2) {
            val cycle = count - firstRepeat!!
            // Part 2
            println(cycle)
            break
        }
        seen[current] = (seen[current] ?: 0) + 1
    }
}