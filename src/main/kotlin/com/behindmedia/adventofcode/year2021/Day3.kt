package com.behindmedia.adventofcode.year2021

import com.behindmedia.adventofcode.common.*

private fun <T> parseLines(lineParser: (String) -> T): List<T> {
    return parseLines("/2021/day3.txt") {
        lineParser.invoke(it)
    }
}

private fun part1() {
    val map0 = mutableMapOf<Int, Int>()
    val map1 = mutableMapOf<Int, Int>()

    var size = 0

    parseLines { line ->
        size = line.count()
        for ((i, c) in line.withIndex()) {
            if (c == '0') {
                map0[i] = (map0[i] ?: 0) + 1
            } else {
                map1[i] = (map1[i] ?: 0) + 1
            }
        }
    }

    var gamma = 0L
    var epsilon = 0L

    for (i in 0 until size) {
        val zeroCount = map0[i] ?: 0
        val oneCount = map1[i] ?: 0
        val mask = 1L shl size - i - 1
        if (oneCount > zeroCount) {
            gamma = gamma or mask
        } else {
            epsilon = epsilon or mask
        }
    }
    println(gamma * epsilon)
}

private fun part2() {
    var bitCount = 0
    val allNumbers = parseLines { line ->
        bitCount = line.count()
        val number =  Integer.parseInt(line, 2)
        number
    }
    val oxygenCandidates = allNumbers.toMutableSet()
    val scrubCandidates = allNumbers.toMutableSet()
    for (bit in 0 until bitCount) {
        filterCandidates(oxygenCandidates, bitCount - bit - 1, true)
        filterCandidates(scrubCandidates, bitCount - bit - 1, false)
    }
    val oxygenRating = oxygenCandidates.first()
    val scrubberRating = scrubCandidates.first()
    println(oxygenRating * scrubberRating)
}

private fun filterCandidates(candidates: MutableCollection<Int>, bitPosition: Int, takeMost: Boolean) {
    val mask = 1 shl bitPosition
    val oneCount = candidates.count { it and mask == mask }
    val zeroCount = candidates.size - oneCount
    if (candidates.size <= 1) return
    val iterator = candidates.iterator()
    while (iterator.hasNext()) {
        val c = iterator.next()
        val keepBit = if (takeMost) {
            zeroCount <= oneCount
        } else {
            oneCount < zeroCount
        }
        val shouldKeep = (c and mask) == (if (keepBit) mask else 0)
        if (!shouldKeep) {
            iterator.remove()
        }
    }
}

fun main() {
    part1()
    part2()
}