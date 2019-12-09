package com.behindmedia.adventofcode2019
import kotlin.math.max

class Day7 {

    fun execute(state: List<Int>, phases: List<Int>): Int {
        val amplifiers = Array(phases.size) { Computer(state.toLongList()) }
        var lastOutput = 0

        for ((i, phase) in phases.withIndex()) {
            val inputs = listOf(phase, lastOutput)
            val amplifier = amplifiers[i]
            val output = amplifier.process(inputs.toLongList())
            assert(amplifier.status == Computer.Status.Done)
            lastOutput = output.toInt()
        }
        return lastOutput
    }

    fun executeWithFeedback(state: List<Int>, phases: List<Int>): Int {
        val amplifiers = Array(phases.size) { Computer(state.toLongList()) }
        var lastOutput = 0
        var i = 0

        while(true) {
            val phase = phases[i]
            val amplifier = amplifiers[i]

            val inputs = if (amplifier.status == Computer.Status.Initial) listOf(phase, lastOutput) else listOf(lastOutput)
            val output = amplifier.process(inputs.toLongList())
            lastOutput = output.toInt()

            if (i == amplifiers.size - 1 && amplifier.status == Computer.Status.Done) {
                return lastOutput
            }
            i = if (i < amplifiers.size - 1) i + 1 else 0
        }
    }

    fun optimize(state: List<Int>): Int {
        var maxOutput = Integer.MIN_VALUE
        permutate(5, 0..4) {
            if (it.toSet().size == 5) {
                val output = execute(state, it)
                maxOutput = max(output, maxOutput)
            }
            null
        }
        return maxOutput
    }

    fun optimizeWithFeedback(state: List<Int>): Int {
        var maxOutput = Integer.MIN_VALUE
        permutate(5, 5..9) {
            if (it.toSet().size == 5) {
                val output = executeWithFeedback(state, it)
                maxOutput = max(output, maxOutput)
            }
            null
        }
        return maxOutput
    }
}