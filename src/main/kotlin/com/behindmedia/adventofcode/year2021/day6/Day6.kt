package com.behindmedia.adventofcode.year2021.day6

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val fish: LongArray = parse("/2021/day6.txt") { line ->
        line.trim().splitNonEmptySequence(" ", ",").map { it.toInt() }.toList()
    }.fold(LongArray(9) { 0L }) { array, t ->
        array.apply {
            this[t]++
        }
    }

    // Part1
    println(simulate(fish, 80))

    // Part2
    println(simulate(fish, 256))
}

val LongArray.totalSize: Long
    get() = sum()

private fun simulate(fish: LongArray, count: Int): Long {
    val copy = fish.copyOf()
    for (i in 0 until count) {
        simulate(copy)
    }
    return copy.totalSize
}

private fun simulate(fish: LongArray) {
    val countZero = fish[0]
    for (i in 1 until fish.size) {
        fish[i - 1] = fish[i]
    }
    fish[6] += countZero
    fish[8] = countZero
}