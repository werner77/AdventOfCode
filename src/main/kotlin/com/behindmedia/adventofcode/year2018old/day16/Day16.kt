package com.behindmedia.adventofcode.year2018old.day16

import com.behindmedia.adventofcode.common.*

private class State {
    private val registers = defaultMutableMapOf<String, Long> { 0L }

    operator fun set(register: String, value: Long) {
        registers[register] = value
    }

    operator fun get(register: String): Long {
        return registers[register]
    }
}

private sealed class OpCode {

    abstract fun execute(state: State, args: List<String>)

    object Addr : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] + state[args[1]]
        }
    }

    object Addi : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] + args[1].toLong()
        }
    }

    object Mulr : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] * state[args[1]]
        }
    }

    object Muli : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] * args[1].toLong()
        }
    }

    object Banr : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] and state[args[1]]
        }
    }

    object Bani : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] and args[1].toLong()
        }
    }

    object Borr : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] or state[args[1]]
        }
    }

    object Bori : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]] or args[1].toLong()
        }
    }

    object Setr : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = state[args[0]]
        }
    }

    object Seti : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = args[0].toLong()
        }
    }

    object Gtir : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = if (args[0].toLong() > state[args[1]]) 1 else 0
        }
    }

    object Gtri : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = if (state[args[0]] > args[1].toLong()) 1 else 0
        }
    }

    object Gtrr : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = if (state[args[0]] > state[args[1]]) 1 else 0
        }
    }

    object Eqir : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = if (args[0].toLong() == state[args[1]]) 1 else 0
        }
    }

    object Eqri : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = if (state[args[0]] == args[1].toLong()) 1 else 0
        }
    }

    object Eqrr : OpCode() {
        override fun execute(state: State, args: List<String>) {
            state[args[2]] = if (state[args[0]] == state[args[1]]) 1 else 0
        }
    }
}

private data class Instruction(val before: State, val after: State, val codes: List<Long>)

/*
Before: [2, 1, 0, 1]
14 1 3 1
After:  [2, 1, 0, 1]
 */

fun main() {
    val data = parse("/2018/day16.txt") { line ->
        val (part1, part2) = line.split("\n\n\n\n")
        val instructions = mutableListOf<Instruction>()
        for (line in part1.split("\n")) {
            if (line.isBlank()) {

            }
        }
    }
    println(data)
}