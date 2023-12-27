package com.behindmedia.adventofcode.year2020.day8

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.year2020.day8.Instruction.Acc
import com.behindmedia.adventofcode.year2020.day8.Instruction.Jmp
import com.behindmedia.adventofcode.year2020.day8.Instruction.NoOp

private class State {
    var accumulator = 0
    var instructionPointer = 0
}

private sealed class Instruction {

    abstract fun execute(state: State)

    data object NoOp: Instruction() {
        override fun execute(state: State) {
            state.instructionPointer++
        }
    }

    data class Jmp(val arg: Int): Instruction() {
        override fun execute(state: State) {
            state.instructionPointer += arg
        }
    }

    data class Acc(val arg: Int): Instruction() {
        override fun execute(state: State) {
            state.accumulator += arg
            state.instructionPointer++
        }
    }
}

fun main() {
    val instructions = parseLines("/2020/day8.txt") { line ->
        val (name, arg) = line.split(" ")
        when (name) {
            "acc" -> Acc(arg.toInt())
            "nop" -> NoOp
            "jmp" -> Jmp(arg.toInt())
            else -> error("Invalid instruction: $name")
        }
    }

    // Part 1
    try {
        runProgram(instructions)
    } catch (e: RecursionException) {
        println(e.state.accumulator)
    }

    // Part 2
    println(trySwap(instructions))
}

private fun trySwap(instructions: List<Instruction>): Int {
    val mutableInstructions = instructions.toMutableList()
    val noOps = instructions.withIndex().mapNotNull { (i, inst) -> if (inst is NoOp) i else null }
    val jumps = instructions.withIndex().mapNotNull { (i, inst) -> if (inst is Jmp) i else null }
    for (i in noOps) {
        for (j in jumps) {
            // Swap instructions
            mutableInstructions[i] = mutableInstructions[j].also {
                mutableInstructions[j] = mutableInstructions[i]
            }
            try {
                return runProgram(mutableInstructions)
            } catch (e: RecursionException) {
                // Bad
            }
            mutableInstructions[i] = mutableInstructions[j].also {
                mutableInstructions[j] = mutableInstructions[i]
            }
        }
    }
    error("No valid result found")
}

private fun runProgram(instructions: List<Instruction>): Int {
    val state = State()
    val seen = mutableSetOf<Int>()
    while (state.instructionPointer < instructions.size) {
        val pointer = state.instructionPointer
        if (pointer in seen) {
            throw RecursionException(state)
        }
        seen += pointer
        val instruction = instructions[pointer]
        instruction.execute(state)
    }
    return state.accumulator
}

private class RecursionException(val state: State): RuntimeException()