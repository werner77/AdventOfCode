package com.behindmedia.adventofcode.year2021.day13

import com.behindmedia.adventofcode.common.*

private data class FoldInstruction(val horizontal: Boolean, val line: Int)

private fun fold(map: Map<Coordinate, Char>, instruction: FoldInstruction): Map<Coordinate, Char> {
    val ret = mutableMapOf<Coordinate, Char>()
    for ((c, v) in map) {
        val c1 = if (instruction.horizontal && c.y > instruction.line) {
            c.copy(y = 2 * instruction.line - c.y)
        } else if (!instruction.horizontal && c.x > instruction.line) {
            c.copy(x = 2 * instruction.line - c.x)
        } else {
            c
        }
        ret[c1] = v
    }
    return ret
}

private val instructionRegex = """fold along ([xy])=(\d+)""".toRegex()

fun main() {
    var parsingInstructions = false
    val map = mutableMapOf<Coordinate, Char>()
    val foldInstructions = mutableListOf<FoldInstruction>()
    parseLines("/2021/day13.txt") { line ->
        if (line.isBlank()) {
            // start of fold instructions
            parsingInstructions = true
        } else if (parsingInstructions) {
            val match = instructionRegex.matchEntire(line) ?: error("Could not parse instruction: $line")
            foldInstructions.add(FoldInstruction(match.groupValues[1] == "y", match.groupValues[2].toInt()))
        } else {
            val (x, y) = line.split(",").map { it.toInt() }
            map[Coordinate(x, y)] = '#'
        }
    }
    part1(map, foldInstructions)
    part2(map, foldInstructions)
}

private fun part1(
    map: Map<Coordinate, Char>,
    foldInstructions: MutableList<FoldInstruction>
) {
    val m = fold(map, foldInstructions.first())
    println(m.count { it.value == '#' })
}

private fun part2(
    map: Map<Coordinate, Char>,
    foldInstructions: MutableList<FoldInstruction>
) {
    var m = map
    for (inst in foldInstructions) {
        m = fold(m, inst)
    }
    m.printMap(' ')
}