package com.behindmedia.adventofcode.year2020.day14

import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.read

private fun applyBitMask1(mask: String, value: Long): Long {
    var result = value
    for (i in mask.indices) {
        val v = mask[mask.length - 1 - i]
        val m = 1L shl i
        when {
            v == '1' -> {
                // Set bit to one
                result = result or m
            }
            v == '0' -> {
                // Set bit to zero
                result = result and m.inv()
            }
            v != 'X' -> {
                error("Invalid char: $v")
            }
        }
    }
    return result
}

private fun applyBitMask2(mask: String, value: Long, bit: Int, process: (Long) -> Unit) {
    if (bit == 36) {
        process(value)
        return
    }
    val v = mask[36 - 1 - bit]
    val m = 1L shl bit
    when (v) {
        '1' -> {
            applyBitMask2(mask, value or m, bit + 1, process)
        }
        '0' -> {
            applyBitMask2(mask, value, bit + 1, process)
        }
        'X' -> {
            applyBitMask2(mask, value or m, bit + 1, process)
            applyBitMask2(mask, value and m.inv(), bit + 1, process)
        }
        else -> {
            error("Invalid mask: $mask")
        }
    }
}

fun main() {
    val data = read("/2020/day14.txt")
    val lines = data.split("\n")
    val state1 = parse(lines) { state, mask, address, value ->
        state[address] = applyBitMask1(mask, value)
    }
    println(state1.values.sum())
    val state2 = parse(lines) { state, mask, address, value ->
        applyBitMask2(mask, address, 0) {
            state[it] = value
        }
    }
    println(state2.values.sum())
}

private fun parse(lines: List<String>, process: (MutableMap<Long, Long>, String, Long, Long) -> Unit): Map<Long, Long> {
    val state = defaultMutableMapOf<Long, Long> { 0L }
    val maskRegex = """mask = ([X01]+)""".toRegex()
    val valueRegex = """mem\[([\-\d]+)] = ([\-\d]+)""".toRegex()
    var mask: String? = null
    for (line in lines) {
        if (line.isEmpty()) continue
        val maskMatch = maskRegex.matchEntire(line)?.destructured
        if (maskMatch != null) {
            mask = maskMatch.component1()
        } else if (mask != null) {
            val (address, value) = valueRegex.matchEntire(line)?.destructured ?: error("Could not match line: $line")
            process(state, mask, address.toLong(), value.toLong())
        } else {
            error("mask should be defined")
        }
    }
    return state
}