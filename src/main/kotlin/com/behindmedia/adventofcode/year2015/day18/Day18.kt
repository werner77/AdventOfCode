package com.behindmedia.adventofcode.year2015.day18

import com.behindmedia.adventofcode.common.*

private fun simulate(input: ValueGrid<Char>, part2: Boolean): ValueGrid<Char> {
    return CharGrid(input.sizeX, input.sizeY) { x, y ->
        val coordinate = Coordinate(x, y)
        val neighbourOnCount = coordinate.allNeighbourSequence().count {
            input.getOrNull(it) == '#' || (part2 && input.isExtreme(it))
        }
        val on = if (part2 && input.isExtreme(coordinate)) {
            true
        } else if (input[coordinate] == '#') {
            neighbourOnCount in 2..3
        } else {
            neighbourOnCount == 3
        }
        if (on) '#' else '.'
    }
}

private fun part1(data: ValueGrid<Char>): Int {
    var map = data
    repeat(100) {
        map = simulate(map, false)
    }
    return map.count { it.value == '#' }
}

private fun part2(data: ValueGrid<Char>): Int {
    var map = data
    repeat(100) {
        map = simulate(map, true)
    }
    return map.count { it.value == '#' }
}

private val testInput = """
    .#.#.#
    ...##.
    #....#
    ..#...
    #.#..#
    ####..
""".trimIndent()

fun main() {
    val data: ValueGrid<Char> = parse("/2015/day18.txt") { line ->
        CharGrid(line)
    }
    require(data.size == 100 * 100)
    println(part1(data))
    println(part2(data))
}