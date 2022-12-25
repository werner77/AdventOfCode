package com.behindmedia.adventofcode.year2017.day23

import com.behindmedia.adventofcode.common.*

class Program(
    val instructions: List<Instruction>,
    val initialState: Map<String, Long> = mapOf(),
    val initialPos: Int = 0
) {
    private var pos = initialPos
    private val state = initialState.toMutableMap()

    operator fun get(register: String): Long {
        return state[register] ?: 0L
    }

    operator fun set(register: String, value: Long) {
        state[register] = value
    }

    fun offset(offset: Long) {
        pos += offset.toInt()
    }

    fun execute(): Int {
        var ret = 0
        while (pos in instructions.indices) {
            val instruction = instructions[pos]
            if (instruction is Instruction.Mul) {
                ret++
            }
            instruction.execute(this)
            pos++
        }
        return ret
    }

    override fun toString(): String {
        return "Program(pos=$pos, state=$state)"
    }
}

// Decompiled optimized function
private fun decompiled(a: Long = 0L): Long {
    var b = 65L
    var c = b
    var d: Long
    var f: Long
    var g: Long
    var h = 0L
    if (a != 0L) {
        b = b * 100 + 100_000
        c = b + 17_000
    }
    while (true) {
        f = 1
        d = 2
        do {
            // START OPTIMIZATION
            val x = b / d
            val y = b % d
            if (x in 2..b && y == 0L) f = 0
            // END OPTIMIZATION
            d++
            g = d - b
        } while (g != 0L)
        if (f == 0L) {
            h++
        }
        g = b - c
        if (g == 0L) break
        b += 17
    }
    return h
}

sealed class Instruction {
    companion object {
        operator fun invoke(name: String, args: List<String>): Instruction {
            try {
                return when (name) {
                    "set" -> Set(args[0], args[1])
                    "mul" -> Mul(args[0], args[1])
                    "jnz" -> Jnz(args[0], args[1])
                    "sub" -> Sub(args[0], args[1])
                    else -> error("Invalid")
                }
            } catch (t: Throwable) {
                error("Invalid instruction: $name $args")
            }
        }
    }

    abstract fun execute(state: Program)

    data class Set(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] = arg2.toLongOrNull() ?: state[arg2]
        }
    }

    data class Mul(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] *= (arg2.toLongOrNull() ?: state[arg2])
        }
    }

    data class Jnz(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            if ((arg1.toLongOrNull() ?: state[arg1]) != 0L) {
                val value = arg2.toLongOrNull() ?: state[arg2]
                state.offset(value - 1)
            }
        }
    }

    data class Sub(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] -= (arg2.toLongOrNull() ?: state[arg2])
        }
    }

    data class Add(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] += arg2.toLongOrNull() ?: state[arg2]
        }
    }
}

fun main() {
    val instructions = parseLines("/2017/day23.txt") { line ->
        val components = line.split(" ")
        Instruction(components[0], components.subList(1, components.size))
    }

    // Part 1
    val program = Program(instructions)
    println(program.execute())

    // Part 2
    println(decompiled(1))
}