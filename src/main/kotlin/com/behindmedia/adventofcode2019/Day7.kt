package com.behindmedia.adventofcode2019
import kotlin.math.max

class Computer(val initialState: List<Int>) {

    enum class Status {
        initial, processing, waitingForInput, exit
    }

    private var position = 0
    private var state = initialState.toMutableList()
    var lastOutput = 0
    var status = Status.initial

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

    fun process(inputs: List<Int>): Int {
        val inputStack = inputs.toMutableList()
        status = Status.processing

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
                    val address = getValue(0, position, state)

                    val input = inputStack.popFirst()

                    if (input == null) {
                        // reset position and return status
                        status = Status.waitingForInput
                        position--
                        return lastOutput
                    }

                    state[address] = input
                    position += 1
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
                    status = Status.exit
                    return lastOutput
                }
                else -> {
                    throw IllegalStateException("Found unknown opcode $opcode at position $position")
                }
            }
        }
    }
}

class Day7 {

    fun execute(phases: List<Int>, state: List<Int>): Int {

        val amplifiers = listOf(Computer(state), Computer(state), Computer(state), Computer(state), Computer(state))

        assert(phases.size == 5)
        var lastOutput = 0

        for ((i, phase) in phases.withIndex()) {
            val inputs = listOf(phase, lastOutput)
            val amplifier = amplifiers[i]
            val output = amplifier.process(inputs)
            assert(amplifier.status == Computer.Status.exit)
            lastOutput = output
        }

        return lastOutput
    }

    fun executeWithFeedback(phases: List<Int>, state: List<Int>): Int {

        val amplifiers = listOf(Computer(state), Computer(state), Computer(state), Computer(state), Computer(state))
        assert(phases.size == 5)

        var lastOutput = 0
        var i = 0

        while(true) {
            val phase = phases[i]
            val amplifier = amplifiers[i]

            val inputs = if (amplifier.status == Computer.Status.initial) listOf(phase, lastOutput) else listOf(lastOutput)
            val output = amplifier.process(inputs)
            lastOutput = output

            if (i == 4 && amplifier.status == Computer.Status.exit) {
                return lastOutput
            }

            if (i < 4) {
                i++
            } else {
                i = 0
            }
        }
    }

    fun optimize(state: List<Int>): Int {
        var maxOutput = Integer.MIN_VALUE
        for (phase0 in 0..4) {
            for (phase1 in 0..4) {
                for (phase2 in 0..4) {
                    for (phase3 in 0..4) {
                        for (phase4 in 0..4) {
                            val phases = listOf(phase0, phase1, phase2, phase3, phase4)
                            if (phases.toSet().size == 5) {
                                val output = execute(phases, state)
                                maxOutput = max(output, maxOutput)
                            }
                        }
                    }
                }
            }
        }
        return maxOutput
    }

    fun optimizeWithFeedback(state: List<Int>): Int {
        var maxOutput = Integer.MIN_VALUE
        for (phase0 in 5..9) {
            for (phase1 in 5..9) {
                for (phase2 in 5..9) {
                    for (phase3 in 5..9) {
                        for (phase4 in 5..9) {
                            val phases = listOf(phase0, phase1, phase2, phase3, phase4)
                            if (phases.toSet().size == 5) {
                                val output = executeWithFeedback(phases, state)
                                maxOutput = max(output, maxOutput)
                            }
                        }
                    }
                }
            }
        }
        return maxOutput
    }
}