package com.behindmedia.adventofcode.year2024.day7

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private infix fun Long.concat(other: Long): Long {
    return (this.toString() + other.toString()).toLong()
}

private data class Equation(val result: Long, val operands: List<Long>) {

    private fun canSolve(index: Int, value: Long?): Boolean {
        if (index == operands.size) {
            // Evaluate the current value
            return value == result
        } else {
            val current = operands[index]
            if (value == null) {
                return canSolve(index + 1, current)
            } else {
                if (canSolve(index + 1, value + current)) return true
                if (canSolve(index + 1, value * current)) return true
                if (canSolve(index + 1, value concat current)) return true
            }
        }
        return false
    }

    fun canSolve(): Boolean {
        return canSolve(0, null)
    }
}

fun main() = timing {
    val data = parseLines("/2024/day7.txt") { line ->
        val (left, right) = line.splitNonEmpty(":").map { it.trim() }
        val result = left.toLong()
        val operands = right.splitNonEmpty(" ").map { it.toLong() }
        Equation(result, operands)
    }
    println(data.sumOf { if (it.canSolve()) it.result else 0L })
}