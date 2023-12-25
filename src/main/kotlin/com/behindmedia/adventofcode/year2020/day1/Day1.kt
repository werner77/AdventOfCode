package com.behindmedia.adventofcode.year2020.day1

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2020/day1.txt") { line ->
        line.toInt()
    }
    for (size in listOf(2, 3)) {
        println(data.permute(maxSize = size, unique = true) { values ->
            if (values.sum() == 2020) {
                values.product()
            } else {
                null
            }
        })
    }
}