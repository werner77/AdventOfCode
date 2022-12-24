package com.behindmedia.adventofcode.year2015.day23

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private class Program(val instructions: List<Instruction>, initialState: Map<String, Long> = emptyMap()) {
    private var pos = 0
    private val registers = initialState.toMutableMap().withDefaultValue { 0L }

    operator fun get(register: String): Long {
        require(register in setOf("a", "b"))
        return registers[register]
    }

    operator fun set(register: String, value: Long) {
        require(register in setOf("a", "b"))
        registers[register] = max(0L, value)
    }

    fun offset(value: Int) {
        pos += (value - 1)
    }

    fun run(): Long {
        while (pos in instructions.indices) {
            val next = instructions[pos]
            next.execute(this)
            pos++
        }
        return this["b"]
    }

    override fun toString(): String {
        return "State(pos=$pos, registers=$registers)"
    }
}

/*
hlf r sets register r to half its current value, then continues with the next instruction.
tpl r sets register r to triple its current value, then continues with the next instruction.
inc r increments register r, adding 1 to it, then continues with the next instruction.
jmp offset is a jump; it continues with the instruction offset away relative to itself.
jie r, offset is like jmp, but only jumps if register r is even ("jump if even").
jio r, offset is like jmp, but only jumps if register r is 1 ("jump if one", not odd).
 */

private sealed class Instruction {
    companion object {
        operator fun invoke(string: String): Instruction {
            val components = string.splitNonEmptySequence(" ", ",").toList()
            val name = components[0]
            val args = components.subList(1, components.size).map { if (it.startsWith("+")) it.substring(1) else it }
            require(args.size < 3)
            require(args.all { it in setOf("a", "b") || it.toIntOrNull() != null })
            return when (name) {
                "hlf" -> Hlf(args[0])
                "tpl" -> Tpl(args[0])
                "inc" -> Inc(args[0])
                "jmp" -> Jmp(args[0])
                "jie" -> Jie(args[0], args[1])
                "jio" -> Jio(args[0], args[1])
                else -> error("Invalid instruction: $string")
            }
        }
    }

    abstract fun execute(state: Program)

    data class Hlf(val arg1: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] /= 2L
        }
    }

    data class Tpl(val arg1: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] *= 3L
        }
    }

    data class Inc(val arg1: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1]++
        }
    }

    data class Jmp(val arg1: String) : Instruction() {
        override fun execute(state: Program) {
            state.offset(arg1.toInt())
        }
    }

    data class Jie(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            if (state[arg1] % 2L == 0L) {
                state.offset(arg2.toInt())
            }
        }
    }

    data class Jio(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            if (state[arg1] == 1L) {
                state.offset(arg2.toInt())
            }
        }
    }
}

fun main() {
    val data = parseLines("/2015/day23.txt") { line ->
        Instruction(line)
    }

    val program1 = Program(data)
    println(program1.run())

    val program2 = Program(data, mapOf("a" to 1L))
    println(program2.run())
}