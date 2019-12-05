package com.behindmedia.adventofcode2019

class Day5 {

    fun processOpcodes(opcodesIn: List<Int>, input: Int = 1): Int {
        var position = 0
        val opcodes = opcodesIn.toMutableList()

        var encounteredInput = false
        var encounteredOutput = false
        var lastOutput = 0

        while (true) {
            var encodedOpcode = opcodes[position]

            val opcode = encodedOpcode % 100
            val operandModes = IntArray(3)

            encodedOpcode /= 100
            for (i in 0 until 3) {
                operandModes[i] = encodedOpcode % 10
                encodedOpcode /= 10
                assert(operandModes[i] < 2)
            }

            if (opcode == 1) {
                // Add
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val pos3 = opcodes[position + 3]
                val first = if (operandModes[0] == 0) opcodes[pos1] else pos1
                val second = if (operandModes[1] == 0) opcodes[pos2] else pos2
                assert(operandModes[2] == 0)
                opcodes[pos3] = first + second
                position += 4
            } else if (opcode == 2) {
                // Multiply
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val pos3 = opcodes[position + 3]

                val first = if (operandModes[0] == 0) opcodes[pos1] else pos1
                val second = if (operandModes[1] == 0) opcodes[pos2] else pos2

                opcodes[pos3] = first * second
                position += 4
            } else if (opcode == 3) {
                // Input
                assert(!encounteredInput)
                val address = opcodes[position + 1]
                opcodes[address] = input
                position += 2
                encounteredInput = true
            } else if (opcode == 4) {
                // Output
                assert(!encounteredOutput)
                val outputAddress = opcodes[position + 1]
                val output = opcodes[outputAddress]
                lastOutput = output

                position += 2
                encounteredOutput = true
            } else if (opcode == 5) {
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val first = if (operandModes[0] == 0) opcodes[pos1] else pos1
                val second = if (operandModes[1] == 0) opcodes[pos2] else pos2

                if (first != 0) {
                    position = second
                } else {
                    position += 3
                }
            } else if (opcode == 6) {
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val first = if (operandModes[0] == 0) opcodes[pos1] else pos1
                val second = if (operandModes[1] == 0) opcodes[pos2] else pos2
                if (first == 0) {
                    position = second
                } else {
                    position += 3
                }
            } else if (opcode == 7) {
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val address = opcodes[position + 3]
                val first = if (operandModes[0] == 0) opcodes[pos1] else pos1
                val second = if (operandModes[1] == 0) opcodes[pos2] else pos2
                assert(operandModes[2] == 0)

                if (first < second) {
                    opcodes[address] = 1
                } else {
                    opcodes[address] = 0
                }
                position += 4
            } else if (opcode == 8) {
                val pos1 = opcodes[position + 1]
                val pos2 = opcodes[position + 2]
                val address = opcodes[position + 3]
                val first = if (operandModes[0] == 0) opcodes[pos1] else pos1
                val second = if (operandModes[1] == 0) opcodes[pos2] else pos2
                assert(operandModes[2] == 0)
                if (first == second) {
                    opcodes[address] = 1
                } else {
                    opcodes[address] = 0
                }
                position += 4
            } else if (opcode == 99) {
                // Done
                break
            } else {
                throw IllegalStateException("Found unknown opcode $opcode at position $position")
            }

        }
        return lastOutput
    }
}