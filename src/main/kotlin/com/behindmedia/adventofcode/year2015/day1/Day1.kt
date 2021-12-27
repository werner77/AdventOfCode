package com.behindmedia.adventofcode.year2015.day1

import com.behindmedia.adventofcode.common.*

fun main() {
    val line = read("/2015/day1.txt").trim()
    var level = 0
    var i = 1
    var basementPos: Int? = null
    for (c in line) {
        when (c) {
            '(' -> level++
            ')' -> level--
        }
        if (level < 0 && basementPos == null) basementPos = i
        i++
    }
    println(level)
    println(basementPos ?: error("No basement found"))
}