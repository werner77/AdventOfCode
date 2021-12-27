package com.behindmedia.adventofcode.year2015.day2

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun area(p: List<Int>): Int {
    require(p.size == 3)
    val c1 = (p[0] * p[1])
    val c2 = (p[0] * p[2])
    val c3 = (p[1] * p[2])
    val c = min(min(c1, c2), c3)
    return 2 * (c1 + c2 + c3) + c
}

private fun ribbon(p: List<Int>): Int {
    require(p.size == 3)
    val c1 = 2 * (p[0] + p[1])
    val c2 = 2 * (p[0] + p[2])
    val c3 = 2 * (p[1] + p[2])
    val c = min(min(c1, c2), c3)
    return c + (p[0] * p[1] * p[2])
}

fun main() {
    val data = parseLines("/2015/day2.txt") { line ->
        line.split("x").map { it.toInt() }
    }

    var sum = 0
    var ribbon = 0
    for (p in data) {
        sum += area(p)
        ribbon += ribbon(p)
    }
    println(sum)
    println(ribbon)
}