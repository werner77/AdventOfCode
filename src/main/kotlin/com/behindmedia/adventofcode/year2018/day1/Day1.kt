package com.behindmedia.adventofcode.year2018.day1

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    part1()
    part2()
}

private fun part2() {
    val data = parseLines("/2018/day1.txt") { line ->
        line.toInt()
    }
    val reached = mutableSetOf<Int>()
    var value = 0
    var foundValue: Int? = null
    while (foundValue == null) {
        for (i in data) {
            value += i
            if (value in reached) {
                foundValue = value
                break
            }
            reached += value
        }
    }
    println(foundValue)
}

private fun part1() {
    val data = parseLines("/2018/day1.txt") { line ->
        line.toInt()
    }
    var value = 0
    for (i in data) {
        value += i
    }
    println(value)
}