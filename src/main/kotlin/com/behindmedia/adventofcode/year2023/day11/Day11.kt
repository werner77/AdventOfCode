package com.behindmedia.adventofcode.year2023.day11

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.maxX
import com.behindmedia.adventofcode.common.maxY
import com.behindmedia.adventofcode.common.parseMap
import com.behindmedia.adventofcode.common.timing

fun main() {
    val map = parseMap("/2023/day11.txt") { line ->
        line
    }

    timing {
        // Find empty rows:
        println(process(map, 2))
        println(process(map, 1_000_000))
    }
}

private fun process(
    map: Map<Coordinate, Char>,
    multiplier: Int
): Long {
    val emptyCols = (0..map.maxX).filter { x ->
        map.filter { it.key.x == x }.values.all { it == '.' }
    }.toSortedSet()
    val emptyRows = (0..map.maxY).filter { y ->
        map.filter { it.key.y == y }.values.all { it == '.' }
    }.toSortedSet()
    val galaxies = map.filter { (_, value) -> value == '#' }.keys.toList()
    var sum: Long = 0
    for (i in 0 until galaxies.size) {
        for (j in i + 1 until galaxies.size) {
            val start = galaxies[i]
            val end = galaxies[j]

            val modifiedStart = Coordinate(
                x = start.x + emptyCols.headSet(start.x).size * (multiplier - 1),
                y = start.y + emptyRows.headSet(start.y).size * (multiplier - 1)
            )
            val modifiedEnd = Coordinate(
                x = end.x + emptyCols.headSet(end.x).size * (multiplier - 1),
                y = end.y + emptyRows.headSet(end.y).size * (multiplier - 1)
            )

            val distance = modifiedStart.manhattenDistance(modifiedEnd)
            sum += distance.toLong()
        }
    }
    return sum
}
