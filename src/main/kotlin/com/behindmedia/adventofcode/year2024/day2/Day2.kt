package com.behindmedia.adventofcode.year2024.day2

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() = timing {
    val data = parseLines("/2024/day2.txt") { line ->
        line.splitNonEmpty(" ") { it.toInt() }
    }

    // Part1
    println(data.count { isValid(it) })

    // Part2
    println(data.count { isValid(it) || it.indices.any { index -> isValid(it, index) } })
}

private fun isValid(levels: List<Int>, removeIndex: Int? = null): Boolean {
    var last: Int? = null
    var increasing: Boolean? = null
    for ((index, level) in levels.withIndex()) {
        if (index == removeIndex) continue
        if (last != null) {
            if (level == last) {
                return false
            }
            val greater = level > last
            if (increasing != null && greater != increasing) {
                return false
            } else if (abs(level - last) !in 1..3) {
                return false
            }
            increasing = greater
        }
        last = level
    }
    return true
}