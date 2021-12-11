package com.behindmedia.adventofcode.year2016.day23

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2016.common.OpCode
import com.behindmedia.adventofcode.year2016.common.Program

private fun runProgram(instructions: List<OpCode>, input: Int): Int {
    val state = Program(instructions)
    state["a"] = input
    state.run()
    return state["a"]
}

fun main() {
    val instructions = parseLines("/2016/day23.txt") { line ->
        OpCode(line)
    }
    // Part 1
    println(runProgram(instructions, 7))

    // Part 2
    println(runProgram(instructions, 12))
}