package com.behindmedia.adventofcode2019

class Day2 {

    fun processOpcodes(opcodesIn: List<Int>, nounAndVerb: Pair<Int, Int>? = null): List<Int> {
        var position = 0
        val opcodes = opcodesIn.toMutableList()

        if (nounAndVerb != null) {
            opcodes[1] = nounAndVerb.first
            opcodes[2] = nounAndVerb.second
        }

        while (true) {
            val opcode = opcodes[position]

            if (opcode == 1) {
                // Add
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val pos3 = opcodes[position + 3]
                opcodes[pos3] = opcodes[pos1] + opcodes[pos2]
            } else if (opcode == 2) {
                // Multiply
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val pos3 = opcodes[position + 3]
                opcodes[pos3] = opcodes[pos1] * opcodes[pos2]
            } else if (opcode == 99) {
                // Done
                break
            } else {
                throw IllegalStateException("Found unknown opcode $opcode at position $position")
            }
            position += 4
        }
        return opcodes
    }

    fun findNounAndVerb(opcodes: List<Int>, expectedResult: Int = 19690720): Int? {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val result = processOpcodes(opcodes, Pair(noun, verb))
                if (result[0] == expectedResult) {
                    return 100 * noun + verb
                }
            }
        }
        return null
    }

}