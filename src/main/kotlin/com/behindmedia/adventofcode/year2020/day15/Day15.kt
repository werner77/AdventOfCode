package com.behindmedia.adventofcode.year2020.day15

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val numbers = read("/2020/day15.txt").trim().trim('[', ']').split(",").map { it.toInt() }
    timing {
        for (limit in listOf(2020, 30000000)) {
            val seen = IntArray(limit)
            var last = 0
            var turn = 0
            while (turn < limit) {
                turn++
                val value = if (turn <= numbers.size) {
                    numbers[turn - 1]
                } else {
                    val previousTurn = seen[last]
                    if (previousTurn == 0) {
                        // Not yet seen before
                        0
                    } else {
                        // Already seen before, take the diff between its turn and last
                        turn - 1 - previousTurn
                    }
                }
                seen[last] = turn - 1
                last = value
            }
            println(last)
        }
    }
}