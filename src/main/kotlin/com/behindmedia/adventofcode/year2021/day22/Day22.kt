package com.behindmedia.adventofcode.year2021.day22

import com.behindmedia.adventofcode.common.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Instruction(val on: Boolean, val range: Range)

data class Range(private val minMax: List<Int>) {
    fun min(index: Int): Int {
        return minMax[2 * index]
    }

    fun max(index: Int): Int {
        return minMax[2 * index + 1]
    }

    fun volume(): Long {
        var product = 1L
        for (i in 0 until 3) {
            val l = (max(i) - min(i) + 1)
            product *= l
        }
        return product
    }

    fun intersection(other: Range): Range? {
        val result = IntArray(6) { 0 }
        for (i in 0 until 3) {
            val min = max(min(i), other.min(i))
            val max = min(max( i ), other.max(i))
            if (max >= min) {
                result[2 * i] = min
                result[2 * i + 1] = max
            } else {
                return null
            }
        }
        return Range(result.toList())
    }
}

private fun solve(instructions: List<Instruction>): Long {
    val cubes = defaultMutableMapOf<Range, Int> { 0 }
    for (instruction in instructions) {
        val update = defaultMutableMapOf<Range, Int> { 0 }
        cubes.forEach { entry ->
            val (otherRange, count) = entry
            instruction.range.intersection(otherRange)?.let {
                update[it] -= count
            }
        }
        if (instruction.on) {
            cubes[instruction.range] += 1
        }
        update.forEach {
            cubes[it.key] += it.value
        }
    }
    return cubes.entries.sumOf { it.key.volume() * it.value }
}

fun main() {
    val instructions = parseLines("/2021/day22.txt") { line ->
        val components = line.splitNonEmptySequence(" ", ",", "=", ".").toList()
        val on = components[0] == "on"
        val minMax = components.mapNotNull { it.toIntOrNull() }
        require(minMax.size == 6) { "Expected size to 6" }
        Instruction(on, Range(minMax))
    }

    val smallInstructions = instructions.filter {
            instr -> (0..2).all { abs(instr.range.min(it)) <= 50 && abs(instr.range.max(it)) <= 50 }
    }

    // Part 1
    println(solve(smallInstructions))

    // Part 2
    timing {
        println(solve(instructions))
    }
}