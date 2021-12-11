package com.behindmedia.adventofcode.year2016.day23

import com.behindmedia.adventofcode.common.*

class State(codes: List<OpCode>) {
    var pos: Int = 0
    private val registers = mutableMapOf<String, Int>().withDefault { 0 }
    private val instructions = codes.toMutableList()

    operator fun set(index: String, element: Int) {
        registers[index] = element
    }

    operator fun get(index: String): Int {
        return registers.getValue(index)
    }

    fun getInstruction(pos: Int) : OpCode? {
        return instructions.getOrNull(pos)
    }

    fun setInstruction(pos: Int, instruction: OpCode) {
        instructions[pos.toInt()] = instruction
    }

    fun getCurrentInstruction(): OpCode? {
        return instructions.getOrNull(this.pos)
    }

    override fun toString(): String {
        return "State(pos=$pos, registers=$registers)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (pos != other.pos) return false
        if (registers != other.registers) return false
        if (instructions != other.instructions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pos
        result = 31 * result + registers.hashCode()
        result = 31 * result + instructions.hashCode()
        return result
    }

    fun copy(): State {
        val copiedState = State(instructions)
        copiedState.pos = this.pos
        copiedState.registers.putAll(this.registers)
        return copiedState
    }
}

sealed class OpCode {

    companion object {
        operator fun invoke(encoded: String): OpCode {
            val components = encoded.splitNonEmptySequence(" ").toList()

            return when (components[0]) {
                "cpy" -> Copy(components[1], components[2])
                "inc" -> Inc(components[1])
                "dec" -> Dec(components[1])
                "jnz" -> Jnz(components[1], components[2])
                "tgl" -> Tgl(components[1])
                else -> error("Could not interpret: $encoded")
            }
        }
    }

    abstract fun execute(state: State)
    abstract fun inverted(): OpCode

    fun executeWithLogging(state: State) {
        println("Executing: $this")
        execute(state)
        println("State after: $state")
    }

    data class Copy(val arg1: String, val arg2: String) : OpCode() {
        override fun execute(state: State) {
            if (arg2.toIntOrNull() != null) error("Invalid arg")
            val value = arg1.toIntOrNull() ?: state[arg1]
            state[arg2] = value
            state.pos++
        }

        override fun inverted(): OpCode {
            return Jnz(arg1, arg2)
        }
    }

    data class Inc(val arg1: String) : OpCode() {
        override fun execute(state: State) {
            if (arg1.toIntOrNull() != null) error("Invalid arg")
            val current = state[arg1]
            if (current == Int.MAX_VALUE) {
                error("Overflow!")
            }
            state[arg1] = current + 1
            state.pos++
        }

        override fun inverted(): OpCode {
            return Dec(arg1)
        }
    }

    data class Dec(val arg1: String) : OpCode() {
        override fun execute(state: State) {
            if (arg1.toIntOrNull() != null) error("Invalid arg")
            state[arg1]--
            state.pos++
        }

        override fun inverted(): OpCode {
            return Inc(arg1)
        }
    }

    data class Jnz(val arg1: String, val arg2: String) : OpCode() {
        override fun execute(state: State) {
            val value = arg1.toIntOrNull() ?: state[arg1]
            val increment = arg2.toIntOrNull() ?: state[arg2]
            if (value != 0) {
                state.pos += increment
            } else {
                state.pos++
            }
        }

        override fun inverted(): OpCode {
            return Copy(arg1, arg2)
        }
    }

    data class Tgl(val arg1: String): OpCode() {
        override fun execute(state: State) {
            val increment = arg1.toIntOrNull() ?: state[arg1]
            val pos = state.pos + increment
            val instruction = state.getInstruction(pos)
            if (instruction != null) {
                state.setInstruction(pos, instruction.inverted())
                state.pos++
            } else {
                state.pos++
            }
        }

        override fun inverted(): OpCode {
            return Inc(arg1)
        }
    }
}

private fun runProgram(instructions: List<OpCode>, input: Int): Int {
    val state = State(instructions)
    state["a"] = input

    println("Starting state: $state")

    while(true) {
        val instruction = state.getCurrentInstruction() ?: break
        try {
            instruction.execute(state)
        } catch (e: Exception) {
            println("Invalid instruction: $instruction: $e")
            // Skipped
            state.pos++
        }
    }
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