package com.behindmedia.adventofcode.year2015.day25

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private val firstCode = 20151125L

private fun generateNextCode(code: Long): Long {
    return (code * 252533L) % 33554393L
}

fun main() {
    val (y, x) = parse("/2015/day25.txt") { line ->
        line.trim().splitNonEmptySequence(" ", ",", ".").mapNotNull { it.toIntOrNull() }.toList()
    }
    val y0 = y + x - 1
    val n = y0 - 1
    val indexOfY0 = 1 + ((n + 1) * n)/2
    val stepsToCoordinate =  y0 - y
    val totalSteps = indexOfY0 + stepsToCoordinate
    var current = firstCode
    for (i in 2 .. totalSteps) {
        current = generateNextCode(current)
    }
    println(current)
}