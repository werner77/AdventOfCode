package com.behindmedia.adventofcode.year2018old

import java.util.*

class Day16 {

    var oppCodeMappings: Map<Int, OppCode>? = null

    data class Sample(val registersBefore: IntArray,
                      val registersAfter: IntArray,
                      val instruction: IntArray) {
        /*
        Before: [0, 1, 2, 1]
        14 1 3 3
        After:  [0, 1, 2, 1]
        */

        companion object {
            fun fromString(s: String): List<Sample> {
                val ret = mutableListOf<Sample>()
                val lines = s.split("\n")
                var instruction: IntArray? = null
                var registersBefore: IntArray? = null
                val beforePrefix = "Before:"
                val afterPrefix = "After:"

                for (line in lines) {
                    val trimmedLine = line.trim()
                    if (trimmedLine.isEmpty()) continue

                    if (trimmedLine.startsWith(beforePrefix)) {
                        instruction = null
                        registersBefore = trimmedLine.substring(beforePrefix.length).split("[", "]", ",", " ").filter { !it.isEmpty() }.map { it.toInt() }.toIntArray()

                    } else if (trimmedLine.startsWith(afterPrefix)) {

                        val registersAfter = trimmedLine.substring(afterPrefix.length).split("[", "]", ",", " ").filter { !it.isEmpty() }.map { it.toInt() }.toIntArray()

                        if (registersBefore == null || instruction == null) {
                            throw IllegalArgumentException("Incorrect parser state")
                        }

                        //Add to list
                        ret.add(Sample(registersBefore, registersAfter, instruction))
                    } else {

                        instruction = trimmedLine.split("[", "]", ",", " ").filter { !it.isEmpty() }.map { it.toInt() }.toIntArray()
                    }
                }
                return ret
            }
        }
    }

    sealed class OppCode {
        companion object {
            val values = arrayOf(addr, addi, mulr, muli, banr, bani, borr, bori, setr, seti, gtir, gtri, gtrr, eqir, eqri, eqrr)
        }

        abstract fun perform(a: Int, b: Int, c: Int, registers: IntArray)

        object addr: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a] + registers[b]
            }
        }

        object addi: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a] + b
            }
        }

        object mulr: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a] * registers[b]
            }
        }

        object muli: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a] * b
            }
        }

        object banr: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a].and(registers[b])
            }
        }

        object bani: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a].and(b)
            }
        }

        object borr: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a].or(registers[b])
            }
        }

        object bori: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a].or(b)
            }
        }

        object setr: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = registers[a]
            }
        }

        object seti: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = a
            }
        }

        object gtir: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = if (a > registers[b]) 1 else 0
            }
        }

        object gtri: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = if (registers[a] > b) 1 else 0
            }
        }

        object gtrr: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = if (registers[a] > registers[b]) 1 else 0
            }
        }

        object eqir: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = if (a == registers[b]) 1 else 0
            }
        }

        object eqri: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = if (registers[a] == b) 1 else 0
            }
        }

        object eqrr: OppCode() {
            override fun perform(a: Int, b: Int, c: Int, registers: IntArray) {
                registers[c] = if (registers[a] == registers[b]) 1 else 0
            }
        }
    }

    fun process(samples: List<Sample>): Int {
        val whitelistMappings = mutableMapOf<Int, MutableSet<OppCode>>()
        val blacklistMappings= mutableMapOf<Int, MutableSet<OppCode>>()
        var count = 0
        for (sample in samples) {
            var matchCount = 0
            for (oppCode in OppCode.values) {
                val registers = sample.registersBefore.copyOf()
                val oppCodeValue = sample.instruction[0]
                oppCode.perform(sample.instruction[1], sample.instruction[2], sample.instruction[3], registers)
                val mappings: MutableMap<Int, MutableSet<OppCode>>
                if (Arrays.equals(registers, sample.registersAfter)) {
                    mappings = whitelistMappings
                    matchCount++
                } else {
                    mappings = blacklistMappings
                }
                val oppCodeSet = mappings.getOrPut(oppCodeValue) { mutableSetOf() }
                oppCodeSet.add(oppCode)
            }

            if (matchCount >= 3) {
                count++
            }
        }
        for (code in whitelistMappings.keys) {
            whitelistMappings[code]?.let {
                val set = blacklistMappings[code]
                if (set != null) {
                    it.removeAll(set)
                }
            }
        }

        do {
            var removed = false
            for (code in whitelistMappings.keys.sortedWith(kotlin.Comparator { i1, i2 ->
                whitelistMappings[i1]!!.size.compareTo(whitelistMappings[i2]!!.size)
            })) {
                val set = whitelistMappings[code]
                if (set!!.size == 1) {
                    for (otherSet in whitelistMappings.values) {
                        if (otherSet !== set) {
                            removed = otherSet.remove(set.first()) || removed
                        }
                    }
                }
            }
        } while(removed)

        val finalMappings = mutableMapOf<Int, OppCode>()
        for ((key, value) in whitelistMappings.entries) {
            if (value.size > 1) {
                throw IllegalStateException("Non unique opcode mapping found")
            }
            value.firstOrNull()?.let {
                finalMappings[key] = it
            }
        }

        this.oppCodeMappings = finalMappings

        return count
    }

    fun execute(instructions: List<IntArray>): IntArray {
        val registers = IntArray(4) { 0 }

        for (instruction in instructions) {
            val oppCodeValue = instruction[0]
            val oppCode = oppCodeMappings!![oppCodeValue]

            if (oppCode == null) {
                println("Unrecognized oppCode: ${oppCodeValue}")
            } else {
                oppCode.perform(instruction[1], instruction[2], instruction[3], registers)
            }
        }
        return registers
    }

}