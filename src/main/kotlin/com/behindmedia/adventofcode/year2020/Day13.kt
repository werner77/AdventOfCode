package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.leastCommonMultiple
import com.behindmedia.adventofcode.common.parseLines

class Day13 {
    fun part1(input: String): Long {
        val (departure, ids: List<Long>) = parse(input)
        val nearestId = ids.minByOrNull {
            if (it < 0) Long.MAX_VALUE else waitTime(departure, it)
        } ?: error("Expected at least one id")
        return nearestId * waitTime(departure, nearestId)
    }

    private fun waitTime(departure: Long, id: Long): Long {
        return id - departure % id
    }

    private fun parse(input: String): Pair<Long, List<Long>> {
        var lineIndex = 0
        var departure = 0L
        var ids: List<Long> = emptyList()
        parseLines(input) { line ->
            when (lineIndex) {
                0 -> departure = line.trim().toLong()
                1 -> ids = line.trim().split(",").map { if (it == "x") -1L else it.trim().toLong() }
                else -> error("Expected two lines")
            }
            lineIndex += 1
        }
        return Pair(departure, ids)
    }

    fun part2(input: String): Long {
        val (_, ids: List<Long>) = parse(input)

        var lcm = 1L
        var time = 0L

        for ((index, id) in ids.withIndex()) {
            if (id < 0) continue
            while ((time + index.toLong()) % id != 0L) {
                time += lcm
            }
            lcm = leastCommonMultiple(lcm, id)
        }
        return time
    }
}