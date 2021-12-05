package com.behindmedia.adventofcode.year2016.day15

import com.behindmedia.adventofcode.common.*

private data class Disc(val id: Int, val count: Int, val startTime: Int, val startPos: Int) {
    fun isAligned(t: Int): Boolean {
        val time = t - startTime
        val numberOfTurns = time + id
        return (startPos + numberOfTurns) % count == 0
    }
}

// Disc #1 has 5 positions; at time=0, it is at position 2.
fun main() {
    val regex = """Disc #([0-9]+) has ([0-9]+) positions; at time=([0-9]+), it is at position ([0-9]+)\.""".toRegex()
    val discs = parseLines("/2016/day15.txt") { line ->
        val matchedValues = regex.matchEntire(line)?.groupValues ?: error("line does not match regex: $line")
        Disc(matchedValues[1].toInt(), matchedValues[2].toInt(), matchedValues[3].toInt(), matchedValues[4].toInt())
    } + Disc(7, 11, 0, 0)

    // Find time x where all discs are aligned at the appropriate time
    var t = 0
    while (true) {
        if (discs.all { it.isAligned(t) }) break
        t++
    }
    println(t)
}