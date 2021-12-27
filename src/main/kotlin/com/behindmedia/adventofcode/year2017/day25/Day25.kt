package com.behindmedia.adventofcode.year2017.day25

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parseLines("/2017/day25.txt") { line ->
        line
    }
    println(data)

    //println(sample('A', 6))
    println(machine('A', 12172063))
}

private fun machine(startState: Char, iterationCount: Int): Int {
    var pos = 0
    var state = startState
    val values = defaultMutableMapOf<Int, Int> { 0 }

    repeat (iterationCount) {
        when (state) {
            'A' -> {
                if (values[pos] == 0) {
                    values[pos] = 1
                    pos++
                    state = 'B'
                } else {
                    values[pos] = 0
                    pos--
                    state = 'C'
                }
            }

            'B' -> {
                if (values[pos] == 0) {
                    values[pos] = 1
                    pos--
                    state = 'A'
                } else {
                    values[pos] = 1
                    pos--
                    state = 'D'
                }
            }
            'C' -> {
                if (values[pos] == 0) {
                    values[pos] = 1
                    pos++
                    state = 'D'
                } else {
                    values[pos] = 0
                    pos++
                    state = 'C'
                }
            }
            'D' -> {
                if (values[pos] == 0) {
                    values[pos] = 0
                    pos--
                    state = 'B'
                } else {
                    values[pos] = 0
                    pos++
                    state = 'E'
                }
            }
            'E' -> {
                if (values[pos] == 0) {
                    values[pos] = 1
                    pos++
                    state = 'C'
                } else {
                    values[pos] = 1
                    pos--
                    state = 'F'
                }
            }
            'F' -> {
                if (values[pos] == 0) {
                    values[pos] = 1
                    pos--
                    state = 'E'
                } else {
                    values[pos] = 1
                    pos++
                    state = 'A'
                }
            }
        }

    }
    return values.values.count { it == 1 }

}

private fun sample(startState: Char, iterationCount: Int): Int {
    var pos = 0
    var state = startState
    val values = defaultMutableMapOf<Int, Int> { 0 }

    repeat (iterationCount) {
        when (state) {
            'A' -> {
                if (values[pos] == 0) {
                    values[pos] = 1
                    pos++
                    state = 'B'
                } else {
                    values[pos] = 0
                    pos--
                    state = 'B'
                }
            }

            'B' -> {
                if (values[pos] == 0) {
                    values[pos] = 1
                    pos--
                    state = 'A'
                } else {
                    values[pos] = 1
                    pos++
                    state = 'A'
                }
            }
        }
        println(values)
    }
    return values.values.count { it == 1 }

}