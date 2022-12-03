package com.behindmedia.adventofcode.year2018.day2

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parseLines("/2018/day2.txt") { line ->
        line
    }

    part1(data)
    part2(data)
}

private fun part1(data: List<String>) {
    var twoCount = 0
    var threeCount = 0
    for (d in data) {
        val countMap = mutableMapOf<Char, Int>()
        for (c in d) {
            countMap[c] = (countMap[c] ?: 0) + 1
        }
        if (countMap.values.contains(2)) {
            twoCount++
        }
        if (countMap.values.contains(3)) {
            threeCount++
        }
    }
    println(twoCount * threeCount)
}

private fun part2(data: List<String>) {
    for (i in data.indices) {
        for (j in i + 1 until data.size) {
            val d1 = data[i]
            val d2 = data[j]
            val same = StringBuilder()
            for (k in d1.indices) {
                if (d1[k] == d2[k]) same.append(d1[k])
            }
            if (same.length == d1.length - 1) {
                println(same.toString())
                return
            }
        }
    }
}