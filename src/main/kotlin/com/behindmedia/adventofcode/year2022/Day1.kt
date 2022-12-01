package com.behindmedia.adventofcode.year2022

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val sums = mutableListOf<Int>()
    val data = parseLines("/2022/day1.txt") { line ->
        line.toIntOrNull() ?: -1
    }
    var sum = 0
    for (i in data) {
        if (i < 0) {
            sums += sum
            sum = 0
        } else {
            sum += i
        }
    }
    if (sum > 0) sums += sum

    sums.sortDescending()

    // part 1
    println(sums.first())

    // part 2
    println((0 until 3).sumOf { sums[it] })
}