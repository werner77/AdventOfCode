package com.behindmedia.adventofcode.year2017.day18

import com.behindmedia.adventofcode.common.*

class Program(private val instructions: List<Instruction>, private val id: Long) {
    private val registers: MutableMap<String, Long> = mutableMapOf("p" to id)
    private val sentValues = ArrayDeque<Long>()
    private val receivedValues = ArrayDeque<Long>()
    private var waiting = false
    private var pos = 0
    var sendCount = 0
        private set

    operator fun set(register: String, value: Long) {
        registers[register] = value
    }

    operator fun get(register: String): Long {
        return registers[register] ?: 0
    }

    fun send(value: Long) {
        sendCount++
        sentValues += value
    }

    fun receive(): Long? {
        val value = receivedValues.removeFirstOrNull()
        if (value == null) waiting = true
        return value
    }

    fun offset(value: Long) {
        pos += value.toInt()
    }

    fun execute(queue: List<Long>): List<Long>? {
        if (waiting && queue.isEmpty()) {
            return null
        }
        waiting = false
        receivedValues += queue
        while (pos in instructions.indices) {
            val next = instructions[pos]
            next.execute(this)
            if (waiting) {
                return this.sentValues.toList().also { this.sentValues.clear() }
            } else {
                pos++
            }
        }
        if (this.sentValues.size > 0) {
            return this.sentValues.toList().also { this.sentValues.clear() }
        }
        return null
    }

    override fun toString(): String {
        return "Program(id=$id, registers=$registers)"
    }
}

sealed class Instruction {
    companion object {
        operator fun invoke(name: String, args: List<String>): Instruction {
            try {
                return when (name) {
                    "set" -> Set(args[0], args[1])
                    "mul" -> Mul(args[0], args[1])
                    "jgz" -> Jgz(args[0], args[1])
                    "add" -> Add(args[0], args[1])
                    "snd" -> Snd(args[0])
                    "mod" -> Mod(args[0], args[1])
                    "rcv" -> Rcv(args[0])
                    else -> error("Unparsable string: $name")
                }
            } catch (t: Throwable) {
                error("Unparseable input: $name $args")
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

    data class Jgz(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            if ((arg1.toLongOrNull() ?: state[arg1]) > 0) {
                val value = arg2.toLongOrNull() ?: state[arg2]
                state.offset(value - 1)
            }
        }
    }

    data class Mod(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] %= (arg2.toLongOrNull() ?: state[arg2])
        }
    }

    data class Add(val arg1: String, val arg2: String) : Instruction() {
        override fun execute(state: Program) {
            state[arg1] += arg2.toLongOrNull() ?: state[arg2]
        }
    }

    data class Snd(val arg1: String) : Instruction() {
        override fun execute(state: Program) {
            state.send(arg1.toLongOrNull() ?: state[arg1])
        }
    }

    data class Rcv(val arg1: String) : Instruction() {
        override fun execute(state: Program) {
            state.receive()?.let { state[arg1] = it }
        }
    }
}

fun main() {
    val data = parseLines("/2017/day18.txt") { line ->
        val components = line.split(" ")
        Instruction.invoke(components[0], components.subList(1, components.size))
    }

    // Part 1
    println(Program(data, 0L).execute(emptyList())?.last())

    // Part 2
    val program0 = Program(data, 0L)
    val program1 = Program(data, 1L)
    val activePrograms = mutableListOf(program0, program1)
    var current = 0
    var sentValues: List<Long> = emptyList()
    while (true) {
        val currentProgram = activePrograms[current]
        val result = currentProgram.execute(sentValues)
        sentValues = result ?: emptyList()
        if (result == null) {
            activePrograms.removeAt(current)
        }
        if (activePrograms.size == 0) break
        current = (current + 1) % activePrograms.size
    }
    println(program1.sendCount)
}