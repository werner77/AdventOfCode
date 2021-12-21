package com.behindmedia.adventofcode.year2017.day16

import com.behindmedia.adventofcode.common.*

sealed class Move {
    abstract fun execute(input: IntArray): IntArray

    data class Spin(val arg1: Int) : Move() {
        override fun execute(input: IntArray): IntArray {
            val size = input.size
            return IntArray(size) { i ->
                val j = (size - arg1 + i) % size
                input[j]
            }
        }
    }

    data class Exchange(val arg1: Int, val arg2: Int) : Move() {
        override fun execute(input: IntArray): IntArray {
            return IntArray(input.size) { i ->
                val j = if (arg1 == i) arg2 else if (arg2 == i) arg1 else i
                input[j]
            }
        }
    }

    data class Partner(val arg1: Int, val arg2: Int) : Move() {
        override fun execute(input: IntArray): IntArray {
            return IntArray(input.size) { i ->
                if (arg1 == input[i]) arg2 else if (arg2 == input[i]) arg1 else input[i]
            }
        }
    }
}

private fun IntArray.asString(): String {
    return map { 'a' + it }.joinToString("")
}

private inline fun <T: Any> perform(initial: IntArray, moves: List<Move>, perform: (Int, IntArray) -> T?): T {
    var current = initial
    var count = 0
    while (true) {
        moves.forEach {
            current = it.execute(current)
        }
        return perform.invoke(++count, current) ?: continue
    }
}

fun main() {
    val moves = parse("/2017/day16.txt") { line ->
        line.split(",").map { instr ->
            when (instr[0]) {
                's' -> Move.Spin(instr.substring(1).trim().toInt())
                'x' -> {
                    val components = instr.substring(1).trim().split("/").map { it.toInt() }
                    Move.Exchange(components[0], components[1])
                }
                'p' -> {
                    val components = instr.substring(1).trim().split("/").map { it }
                    Move.Partner(components[0].single() - 'a', components[1].single() - 'a')
                }
                else -> error("Could not parse line: $instr")
            }
        }
    }

    val initial = IntArray(16) { it }
    val mapping = perform(initial, moves) { _, out -> out }

    // Part 1
    println(mapping.asString())

    // Part 2
    val period = perform(initial, moves) { i, out ->
        if (initial.contentEquals(out)) i else null
    }

    val iterations = 1_000_000_000 % period
    val output = perform(initial, moves) { i, out ->
        if (i == iterations) out else null
    }

    println(output.asString())
}