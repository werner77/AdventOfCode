package com.behindmedia.adventofcode.year2022.day4

import com.behindmedia.adventofcode.common.*

private data class Range(val start: Int, val end: Int) {
    fun contains(other: Range) : Boolean {
        return other.start in start..end && other.end in start..end
    }

    fun containsPart(other: Range) : Boolean {
        return other.start in start..end || other.end in start..end
    }
}

fun main() {
    val data = parseLines("/2022/day4.txt") { line ->
        val d = line.splitToSequenceByCharactersInString("- ,") { it.toInt() }.toList()
        require(d.size == 4)
        Pair(Range(d[0], d[1]), Range(d[2], d[3]))
    }
    part1(data)
    part2(data)
}

private fun part1(data: List<Pair<Range, Range>>) {
    val ans = data.count { (r1, r2) -> r1.contains(r2) || r2.contains(r1) }
    println(ans)
}

private fun part2(data: List<Pair<Range, Range>>) {
    val ans = data.count { (r1, r2) -> r1.containsPart(r2) || r2.containsPart(r1) }
    println(ans)
}