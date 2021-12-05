package com.behindmedia.adventofcode.year2016.day12

import com.behindmedia.adventofcode.common.*

class State {
    var pos: Long = 0
    private val registers = mutableMapOf<String, Long>().withDefault { 0L }

    operator fun set(index: String, element: Long) {
        registers[index] = element
    }

    operator fun get(index: String): Long {
        return registers.getValue(index)
    }

    override fun toString(): String {
        return "State(pos=$pos, registers=$registers)"
    }
}

/*
cpy 41 a
inc a
inc a
dec a
jnz a 2
dec a
 */
sealed class OpCode {

    companion object {
        operator fun invoke(encoded: String): OpCode {
            val components = encoded.splitNonEmptySequence(" ").toList()

            return when (components[0]) {
                "cpy" -> Copy(components[1], components[2])
                "inc" -> Inc(components[1])
                "dec" -> Dec(components[1])
                "jnz" -> Jnz(components[1], components[2].toLong())
                else -> error("Could not interpret: $encoded")
            }
        }
    }

    abstract fun execute(state: State)

    fun executeWithLogging(state: State) {
        println("Executing: $this")
        execute(state)
        println("State after: $state")
    }

    data class Copy(val arg1: String, val arg2: String) : OpCode() {
        override fun execute(state: State) {
            val value = arg1.toLongOrNull() ?: state[arg1]
            state[arg2] = value
            state.pos++
        }
    }

    data class Inc(val arg1: String) : OpCode() {
        override fun execute(state: State) {
            state[arg1] += 1L
            state.pos++
        }
    }

    data class Dec(val arg1: String) : OpCode() {
        override fun execute(state: State) {
            state[arg1] -= 1L
            state.pos++
        }
    }

    data class Jnz(val arg1: String, val arg2: Long) : OpCode() {
        override fun execute(state: State) {
            val value = arg1.toLongOrNull() ?: state[arg1]
            if (value != 0L) {
                state.pos += arg2
            } else {
                state.pos++
            }
        }
    }
}

/*
cpy 41 a
inc a
inc a
dec a
jnz a 2
dec a
 */
fun main() {
    // part 1
    solve()

    // part 2
    solve {
        it["c"] = 1
    }
}

private fun solve(init: (State) -> Unit = {}) {
    val opCodes = parseLines("/2016/day12.txt") { line ->
        OpCode(line)
    }
    val state = State()
    init.invoke(state)
    while (true) {
        val opCode = opCodes.getOrNull(state.pos.toInt()) ?: break
        opCode.execute(state)
    }
    println(state)
}