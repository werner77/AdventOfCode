package com.behindmedia.adventofcode.year2024.day1

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() = timing {
    val data = parseLines("/2024/day1.txt") { line ->
        val (i, j) = line.splitNonEmpty(" ").map { it.toInt() }
        i to j
    }

    val list1 = data.map { it.first }.sorted()
    val list2 = data.map { it.second }.sorted()

    // Part 1
    println(list1.zip(list2).sumOf { (i, j) -> abs(i - j) })

    // Part 2
    val countMap = list2.fold(defaultMutableMapOf<Int, Int> { 0 }) { map, value ->
        map[value]++
        map
    }
    println(list1.sumOf { it * countMap[it] })
}
