package com.behindmedia.adventofcode.year2024.day17

import com.behindmedia.adventofcode.common.*
import java.io.OutputStream

private class State {
    private val values = LongArray(3)

    operator fun get(register: Char): Long {
        return values[register - 'A']
    }

    operator fun set(register: Char, value: Long) {
        values[register - 'A'] = value
    }

    override fun toString(): String {
        return values.contentToString()
    }
}

private sealed class Operation {

    companion object {
        operator fun invoke(code: Int): Operation {
            return when (code) {
                0 -> Adv
                1 -> Bxl
                2 -> Bst
                3 -> Jnz
                4 -> Bxc
                5 -> Out
                6 -> Bdv
                7 -> Cdv
                else -> error("Invalid operation: $code")
            }
        }
    }

    abstract fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int

    protected fun getComboValue(state: State, operand: Int): Long {
        return when (operand) {
            in 0..3 -> operand.toLong()
            4 -> state['A']
            5 -> state['B']
            6 -> state['C']
            else -> error("Invalid operand $operand")
        }
    }

    data object Adv : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            val comboValue = getComboValue(state, operand)
            state['A'] = state['A'] shr comboValue.toInt()
            return instructionPointer + 2
        }
    }

    data object Bxl : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            val value = state['B'] xor operand.toLong()
            state['B'] = value
            return instructionPointer + 2
        }
    }

    data object Bst : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            val comboValue = getComboValue(state, operand)
            val value = comboValue % 8
            state['B'] = value
            return instructionPointer + 2
        }
    }

    data object Jnz : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            return if (state['A'] == 0L) {
                instructionPointer + 2
            } else {
                operand
            }
        }
    }

    data object Bxc : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            val value = state['B'] xor state['C']
            state['B'] = value
            return instructionPointer + 2
        }
    }

    data object Out : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            val value = getComboValue(state, operand) % 8
            out.write(value.toInt())
            return instructionPointer + 2
        }
    }

    data object Bdv : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            val comboValue = getComboValue(state, operand)
            state['B'] = state['A'] shr comboValue.toInt()
            return instructionPointer + 2
        }
    }

    data object Cdv : Operation() {
        override fun execute(instructionPointer: Int, state: State, operand: Int, out: OutputStream): Int {
            val comboValue = getComboValue(state, operand)
            state['C'] = state['A'] shr comboValue.toInt()
            return instructionPointer + 2
        }
    }
}

private val pattern =
    """\s*Register A: (\d+)\s*Register B: (\d+)\s*Register C: (\d+)\s*Program: ([\d,]+)\s*""".toRegex()

private fun stateOf(a: Long): State {
    return State().also {
        it['A'] = a
        it['B'] = 0
        it['C'] = 0
    }
}

fun main() = timing {
    val data = read("/2024/day17.txt")
    val (a, _, _, program) = pattern.matchEntire(data)?.destructured ?: error("Invalid input")

    val instructions = program.split(",").map(String::toInt)

    // Part 1
    println(runProgram(instructions, a.toLong()).second.joinToString(","))

    // Part 2
    println(findBestInput(instructions))
}

private fun runProgram(
    instructions: List<Int>,
    input: Long,
    predicate: (Operation) -> Boolean = { true }
): Pair<Long, List<Int>> {
    val stream = Out()
    val state = stateOf(input)
    var instructionPointer = 0
    while (instructionPointer < instructions.size) {
        val instruction = instructions[instructionPointer]
        val operand = instructions[instructionPointer + 1]
        val operation = Operation(instruction)
        if (!predicate(operation)) {
            return state['A'] to stream.buffer
        }
        instructionPointer = operation.execute(instructionPointer, state, operand, stream)
    }
    return state['A'] to stream.buffer
}

private fun runProgramLoop(instructions: List<Int>, input: Long): Pair<Long, Long> {
    val result = runProgram(instructions, input) {
        it !is Operation.Jnz
    }
    return result.first to result.second.first().toLong()
}

/*
  Decompiled program, returns the next value of a and the output the program generated as a pair.
 */
private fun decompiledProgram(a: Long): Pair<Long, Long> {
    var b = (a and 7) xor 2
    b = (b xor 3) xor (a shr b.toInt())
    return (a shr 3) to (b and 7)
}

private class Out : OutputStream() {
    val buffer: MutableList<Int> = mutableListOf()

    override fun write(b: Int) {
        buffer.add(b)
    }

    override fun toString(): String {
        return buffer.joinToString(",")
    }
}

private data class Value(val index: Int, val value: Long)

private fun findBestInput(instructions: List<Int>): Long {
    val pending = ArrayDeque<Value>()
    pending.add(Value(instructions.size, 0L))
    val valid = mutableListOf<Long>()
    while (pending.isNotEmpty()) {
        val (index, value) = pending.removeFirst()
        if (index == 0) {
            valid += value
            continue
        }
        val nextIndex = index - 1
        val instruction = instructions[nextIndex].toLong()
        for (i in 0L until 8L) {
            val nextValue = (value shl 3) or i
            val (output, instructionFound) = runProgramLoop(instructions, nextValue)
            if (instructionFound == instruction && output == value) {
                pending.addFirst(Value(nextIndex, nextValue))
            }
        }
    }
    return valid.min()
}
