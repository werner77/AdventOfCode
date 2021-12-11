package com.behindmedia.adventofcode.year2016.common

import com.behindmedia.adventofcode.common.splitNonEmptySequence

enum class TransmitState {
    Invalid, Valid, Complete
}

class Program(codes: List<OpCode>) {
    var pos: Int = 0
    private val registers = mutableMapOf<String, Int>().withDefault { 0 }
    private val instructions = codes.toMutableList()
    private val transmittedValues = mutableListOf<Int>()
    var transmitState: TransmitState = TransmitState.Valid

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
        instructions[pos] = instruction
    }

    fun getCurrentInstruction(): OpCode? {
        if (transmitState != TransmitState.Valid) return null
        return instructions.getOrNull(this.pos)
    }

    fun run() {
        while (true) {
            val next = getCurrentInstruction() ?: break
            try {
                next.execute(this)
            } catch (e: Exception) {
                println("Invalid instruction: $next")
                this.pos++
            }
        }
    }

    override fun toString(): String {
        return "State(pos=$pos, registers=$registers)"
    }

    fun transmit(value: Int) {
        if (transmitState == TransmitState.Invalid) return
        val isValidValue = value in setOf(0, 1) && transmittedValues.lastOrNull()?.let { it != value } ?: true
        if (isValidValue) {
            transmittedValues += value
            if (transmittedValues.size > 1000) {
                transmitState = TransmitState.Complete
            }
        } else {
            transmitState = TransmitState.Invalid
        }
    }

    fun copy(): Program {
        return Program(instructions).also {
            it.pos = pos
            it.registers.putAll(registers)
        }
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
                "out" -> Out(components[1])
                else -> error("Could not interpret: $encoded")
            }
        }
    }

    abstract fun execute(state: Program)
    abstract fun inverted(): OpCode

    fun executeWithLogging(state: Program) {
        println("Executing: $this")
        execute(state)
        println("State after: $state")
    }

    data class Copy(val arg1: String, val arg2: String) : OpCode() {
        override fun execute(state: Program) {
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
        override fun execute(state: Program) {
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
        override fun execute(state: Program) {
            if (arg1.toIntOrNull() != null) error("Invalid arg")
            state[arg1]--
            state.pos++
        }

        override fun inverted(): OpCode {
            return Inc(arg1)
        }
    }

    data class Jnz(val arg1: String, val arg2: String) : OpCode() {
        override fun execute(state: Program) {
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
        override fun execute(state: Program) {
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

    data class Out(val arg1: String): OpCode() {
        override fun execute(state: Program) {
            val value = arg1.toIntOrNull() ?: state[arg1]
            state.transmit(value)
            state.pos++
        }

        override fun inverted(): OpCode {
            return Inc(arg1)
        }
    }
}