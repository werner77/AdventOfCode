package com.behindmedia.adventofcode.year2018.day6

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parseLines("/2018/day6.txt") { line ->
        val (x, y) = line.splitNonEmptySequence(" ", ",").map { it.toInt() }.toList()
        Coordinate(x, y)
    }
    println(data.size)
}