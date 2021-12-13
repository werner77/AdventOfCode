package com.behindmedia.adventofcode.year2021.day13

import com.behindmedia.adventofcode.common.*

private data class FoldInstruction(val horizontal: Boolean, val line: Int)

private fun fold(map: Map<Coordinate, Char>, instruction: FoldInstruction): Map<Coordinate, Char> {
    val result = mutableMapOf<Coordinate, Char>()
    for ((coordinate, value) in map) {
        val (x, y) = coordinate
        val newCoordinate = if (instruction.horizontal && coordinate.y > instruction.line) {
            Coordinate(x, 2 * instruction.line - y)
        } else if (!instruction.horizontal && coordinate.x > instruction.line) {
            Coordinate(2 * instruction.line - x, y)
        } else {
            coordinate
        }
        result[newCoordinate] = value
    }
    return result
}

private val instructionRegex = """fold along (x|y)=([0-9]+)""".toRegex()

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
    map: MutableMap<Coordinate, Char>,
    foldInstructions: MutableList<FoldInstruction>
) {
    val m = fold(map, foldInstructions.first())
    println(m.count { it.value == '#' })
}

private fun part2(
    map: MutableMap<Coordinate, Char>,
    foldInstructions: MutableList<FoldInstruction>
) {
    var m = map.toMap()
    for (inst in foldInstructions) {
        m = fold(m, inst)
    }
    m.printMap(' ')
}