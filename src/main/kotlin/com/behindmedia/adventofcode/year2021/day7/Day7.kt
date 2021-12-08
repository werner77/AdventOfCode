package com.behindmedia.adventofcode.year2021.day7

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parse("/2021/day7.txt") { line ->
        line.splitNonEmptySequence(",", " ", "\n", conversion = String::toInt).toList()
    }
    println(part1(data))
    println(part2(data))
}

private fun findMinSum(data: List<Int>, fuelCalculation: (Int, Int) -> Long): Long {
    return data.minOf { i -> data.asSequence().map { fuelCalculation.invoke(i, it) }.sum() }
}

private fun part1(data: List<Int>): Long = findMinSum(data) { a, b ->
    abs(a - b).toLong()
}

private fun part2(data: List<Int>): Long = findMinSum(data) { a, b ->
    val n = abs(a - b).toLong()
    (n * (n + 1)) / 2
}