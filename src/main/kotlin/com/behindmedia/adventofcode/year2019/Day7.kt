package com.behindmedia.adventofcode.year2019
import com.behindmedia.adventofcode.common.permutate
import com.behindmedia.adventofcode.common.toLongList
import kotlin.math.max

class Day7 {

    fun execute(state: List<Int>, phases: List<Int>): Int {
        val amplifiers = Array(phases.size) { Computer(state.toLongList()) }
        var lastOutput = 0

        for ((i, phase) in phases.withIndex()) {
            val inputs = listOf(phase, lastOutput)
            val amplifier = amplifiers[i]
            val result = amplifier.process(inputs.toLongList())
            assert(result.status == Computer.Status.Finished)
            lastOutput = result.lastOutput.toInt()
        }
        return lastOutput
    }

    /**
     * Method to execute the program on all amplifiers with feedback from last to first. Uses conversion from int to long
     * since the computer was refactored at a later point to work with 64 bit values and addresses.
     */
    fun executeWithFeedback(state: List<Int>, phases: List<Int>): Int {
        val amplifiers = Array(phases.size) { Computer(state.toLongList()) }
        var lastOutput = 0
        var i = 0

        while(true) {
            val phase = phases[i]
            val amplifier = amplifiers[i]

            // Initially feed the amplifier two inputs (including the phase), otherwise just feed it the last output
            val inputs = if (amplifier.status == Computer.Status.Initial) listOf(phase, lastOutput) else listOf(lastOutput)
            val result = amplifier.process(inputs.toLongList())
            lastOutput = result.lastOutput.toInt()

            // When the last amplifier is finished, return the output
            if (i == amplifiers.size - 1 && amplifier.status == Computer.Status.Finished) {
                return lastOutput
            }

            // Feed back to the first amplifier if we're at the last one
            i = if (i < amplifiers.size - 1) i + 1 else 0
        }
    }

    fun optimize(state: List<Int>): Int {
        var maxOutput = Integer.MIN_VALUE
        permutate(5, 0..4) {
            if (it.toSet().size == 5) {
                val output = execute(state, it.toList())
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
                val output = executeWithFeedback(state, it.toList())
                maxOutput = max(output, maxOutput)
            }
            null
        }
        return maxOutput
    }
}