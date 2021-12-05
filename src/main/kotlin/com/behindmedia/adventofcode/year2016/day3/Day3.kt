package com.behindmedia.adventofcode.year2016.day3

import com.behindmedia.adventofcode.common.parseLines

private fun isValidTriangle(sides: List<Int>): Boolean {
    for (j in sides.indices) {
        val side = sides[j]
        var sum = 0
        for (i in sides.indices) {
            if (i == j) continue
            sum += sides[i]
        }
        if (sum <= side) return false
    }
    return true
}

private fun part1() {
    val triangles = parseLines("/2016/day3.txt") { line ->
        line.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
    }
    var valid = 0
    for (triangle in triangles) {
        if (isValidTriangle(triangle)) valid++
    }
    println(valid)
}

private fun part2() {
    val triangles = parseLines("/2016/day3.txt") { line ->
        line.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
    }
    var i = 0
    var valid = 0
    while (i < triangles.size) {
        for (j in 0 until 3) {
            if (isValidTriangle(listOf(triangles[i][j], triangles[i + 1][j], triangles[i + 2][j]))) valid++
        }
        i += 3
    }
    println(valid)
}

fun main() {
    part1()
    part2()
}