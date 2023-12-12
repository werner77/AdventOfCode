package com.behindmedia.adventofcode.year2023.day11

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.forEachPair
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
        map.count { it.key.x == x && it.value == '#' } == 0
    }.toSortedSet()
    val emptyRows = (0..map.maxY).filter { y ->
        map.count { it.key.y == y && it.value == '#' } == 0
    }.toSortedSet()
    val galaxies = map.filter { (_, value) -> value == '#' }.keys.toList()
    var sum: Long = 0
    galaxies.forEachPair(unique = true) { start, end ->
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
    return sum
}