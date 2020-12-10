package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.popFirst

class Day8 {

    fun part1(input: String): Int {
        val instructions = parseLines(input) {
            val components = it.split(" ")
            if (components.size != 2) error("Illegal input")
            val instruction = Pair(components[0], components[1].toInt())
            instruction
        }
        return runProgram(instructions).first
    }

    fun part2(input: String): Int {
        val swappableInstructions = setOf("jmp", "nop")
        val instructions = parseLines(input) {
            val components = it.split(" ")
            if (components.size != 2) error("Illegal input")
            Pair(components[0], components[1].toInt())
        }.toMutableList()

        val swappableIndexes = instructions
            .mapIndexedNotNull { index, pair ->
                if (swappableInstructions.contains(pair.first)) index else null
            }
            .toMutableSet()

        while (swappableIndexes.isNotEmpty()) {
            val index = swappableIndexes.popFirst() ?: error("No index found")
            val originalInstruction = swap(instructions, index)
            val result = runProgram(instructions)
            if (result.second) {
                return result.first
            }
            instructions[index] = originalInstruction
        }
        error("No swappable indexes found")
    }

    private fun swap(instructions: MutableList<Pair<String, Int>>, index: Int): Pair<String, Int> {
        val instr = instructions[index]
        val swappedInstr = instr.copy(
            first = when (instr.first) {
                "nop" -> "jmp"
                "jmp" -> "nop"
                else -> error("Non-swappable instruction: ${instr.first}")
            }
        )
        instructions[index] = swappedInstr
        return instr
    }

    private fun runProgram(instructions: List<Pair<String, Int>>): Pair<Int, Boolean> {
        var instructionPointer = 0
        val visitedInstructions = mutableSetOf<Int>()
        var accumulator = 0

        while (true) {
            val instruction = instructions.getOrNull(instructionPointer) ?: break
            if (visitedInstructions.contains(instructionPointer)) {
                return Pair(accumulator, false)
            }
            visitedInstructions.add(instructionPointer)
            when (instruction.first) {
                "jmp" -> instructionPointer += instruction.second
                "nop" -> instructionPointer++
                "acc" -> {
                    accumulator += instruction.second
                    instructionPointer++
                }
                else -> error("Invalid instruction")
            }
        }
        return Pair(accumulator, instructionPointer == instructions.size)
    }
}