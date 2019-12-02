package com.behindmedia.adventofcode2019

class Day2 {

    fun processOpcodes(opcodesIn: List<Int>, noun: Int? = null, verb: Int? = null): List<Int> {
        var position = 0
        val opcodes = opcodesIn.toMutableList()

        if (noun != null) {
            opcodes[1] = noun
        }
        if (verb != null) {
            opcodes[2] = verb
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

}