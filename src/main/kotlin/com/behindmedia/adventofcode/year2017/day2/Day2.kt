package com.behindmedia.adventofcode.year2017.day2

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun Int.divideEven(other: Int): Int? = if (this % other == 0 && this / other != 0) this / other else null

fun List<Int>.evenDivision(): Int {
    for (i in this.indices) {
        for (j in i + 1 until this.size) {
            val a = this[i]
            val b = this[j]
            return a.divideEven(b) ?: b.divideEven(a) ?: continue
        }
    }
    error("Could not find even divisible pair")
}

fun main() {
    val data = parseLines("/2017/day2.txt") { line ->
        line.splitNonEmptySequence(" ", "\t").map { it.toInt() }.toList()
    }

    // Part 1
    println(data.sumOf { line -> line.max() - line.min() })

    // Part 2
    println(data.sumOf { line -> line.evenDivision() })
}