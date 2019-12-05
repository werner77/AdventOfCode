package com.behindmedia.adventofcode2019

class Day5 {

    data class Opcode(val code: Int, val operandModes: IntArray)

    private fun getValue(index: Int, position: Int, operandModes: IntArray, state: List<Int>): Int {
        val value = getValue(index, position, state)
        return if (operandModes[index] == 0) state[value] else value
    }

    private fun getValue(index: Int, position: Int, state: List<Int>): Int {
        return state[position + index]
    }

    private fun getOpcode(position: Int, state: List<Int>): Opcode {
        var encodedOpcode = state[position]

        val code = encodedOpcode % 100
        val operandModes = IntArray(3)

        encodedOpcode /= 100
        for (i in 0 until 3) {
            operandModes[i] = encodedOpcode % 10
            encodedOpcode /= 10
            assert(operandModes[i] < 2)
        }

        return Opcode(code, operandModes)
    }

    fun processOpcodes(opcodesIn: List<Int>, input: Int = 1): Int {
        var position = 0
        val state = opcodesIn.toMutableList()

        var encounteredInput = false
        var lastOutput = 0

        loop@ while (true) {
            val opcode = getOpcode(position++, state)
            when (opcode.code) {
                1 -> {
                    // Add
                    val first = getValue(0, position, opcode.operandModes, state)
                    val second = getValue(1, position, opcode.operandModes, state)
                    assert(opcode.operandModes[2] == 0)
                    val address = getValue(2, position, state)
                    state[address] = first + second
                    position += 3
                }
                2 -> {
                    // Multiply
                    // Add
                    val first = getValue(0, position, opcode.operandModes, state)
                    val second = getValue(1, position, opcode.operandModes, state)
                    assert(opcode.operandModes[2] == 0)
                    val address = getValue(2, position, state)
                    state[address] = first * second
                    position += 3
                }
                3 -> {
                    // Input
                    assert(!encounteredInput)
                    val address = getValue(0, position, state)
                    state[address] = input
                    position += 1
                    encounteredInput = true
                }
                4 -> {
                    // Output
                    val address = getValue(0, position, state)
                    val output = state[address]
                    lastOutput = output
                    position += 1
                }
                5 -> {
                    val first = getValue(0, position, opcode.operandModes, state)
                    val second = getValue(1, position, opcode.operandModes, state)
                    if (first != 0) {
                        position = second
                    } else {
                        position += 2
                    }
                }
                6 -> {
                    val first = getValue(0, position, opcode.operandModes, state)
                    val second = getValue(1, position, opcode.operandModes, state)
                    if (first == 0) {
                        position = second
                    } else {
                        position += 2
                    }
                }
                7 -> {
                    val first = getValue(0, position, opcode.operandModes, state)
                    val second = getValue(1, position, opcode.operandModes, state)
                    assert(opcode.operandModes[2] == 0)
                    val address = getValue(2, position, state)
                    if (first < second) {
                        state[address] = 1
                    } else {
                        state[address] = 0
                    }
                    position += 3
                }
                8 -> {
                    val first = getValue(0, position, opcode.operandModes, state)
                    val second = getValue(1, position, opcode.operandModes, state)
                    assert(opcode.operandModes[2] == 0)
                    val address = getValue(2, position, state)
                    if (first == second) {
                        state[address] = 1
                    } else {
                        state[address] = 0
                    }
                    position += 3
                }
                99 -> {
                    // Done
                    break@loop
                }
                else -> {
                    throw IllegalStateException("Found unknown opcode $opcode at position $position")
                }
            }
        }
        return lastOutput
    }
}