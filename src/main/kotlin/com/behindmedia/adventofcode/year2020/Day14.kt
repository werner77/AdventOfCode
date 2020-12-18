package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines

class Day14 {

    fun part1(input: String): Long {
        return parse(input, this::process1)
    }

    fun part2(input: String): Long {
        return parse(input, this::process2)
    }

    private fun parse(input: String, process: (String, Long, Long, MutableMap<Long, Long>) -> Unit): Long {
        val maskRegex = Regex("""^mask = ([0-1X]+)$""")
        val memRegex = Regex("""^mem\[([0-9]+)\] = ([0-9]+)$""")
        var currentMask = ""
        val state = mutableMapOf<Long, Long>()
        parseLines(input) { line ->
            val maskMatch = maskRegex.matchEntire(line)
            val memMatch = memRegex.matchEntire(line)
            when {
                maskMatch != null -> currentMask = maskMatch.groupValues[1]
                memMatch != null -> {
                    val address = memMatch.groupValues[1].toLong()
                    val value = memMatch.groupValues[2].toLong()
                    process(currentMask, address, value, state)
                }
                else -> error("No match found for line: $line")
            }
        }
        return state.values.sum()
    }

    private fun process1(mask: String, address: Long, value: Long, values: MutableMap<Long, Long>) {
        assert(mask.length == 36)
        var currentValue = value
        for (i in 0 until 36) {
            val m = (1L shl i)
            val j = 35 - i
            if (mask[j] == '0') {
                // Set this bit to 0
                currentValue = currentValue and m.inv()
            } else if (mask[j] == '1') {
                // Set this bit to 1
                currentValue = currentValue or m
            }
        }
        values[address] = currentValue
    }

    private fun process2(mask: String, address: Long, value: Long, values: MutableMap<Long, Long>) {
        assert(mask.length == 36)
        var modifiedAddress = address
        val floatingBits = mutableListOf<Int>()
        for (i in 0 until 36) {
            val m = (1L shl i)
            val j = 35 - i
            if (mask[j] == '1') {
                // set bit to one
                modifiedAddress = modifiedAddress or m
            } else if (mask[j] == 'X') {
                // float
                floatingBits.add(i)
            }
        }
        permutate(modifiedAddress, value, floatingBits, values)
    }

    private fun permutate(address: Long, value: Long, bits: List<Int>, values: MutableMap<Long, Long>) {
        if (bits.isEmpty()) {
            values[address] = value
        } else {
            val bit = bits.first()
            val newBits = bits.subList(1, bits.size)
            val mask = 1L shl bit
            val address1 = address or mask
            val address2 = address and mask.inv()
            permutate(address1, value, newBits, values)
            permutate(address2, value, newBits, values)
        }
    }
}