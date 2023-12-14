package com.behindmedia.adventofcode.year2023.day14

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.Grid
import com.behindmedia.adventofcode.common.coordinateRange
import com.behindmedia.adventofcode.common.parseMap
import com.behindmedia.adventofcode.common.sizeY
import com.behindmedia.adventofcode.common.timing

fun main() {
    val map = parseMap("/2023/day14.txt") { line ->
        line
    }

    timing {
        // Part 1
        println(process(map, Coordinate.up).score)
    }

    timing {
        // Part 2
        println(processAllDirectionsFast(map).score)
    }
}

private fun processAllDirectionsFast(map: Grid, totalIterations: Int = 1_000_000_000): Grid {
    val seen = mutableMapOf<Grid, Int>()
    val (initial, cycle) = processAllDirectionsRepeatedly(map) { iteration, current ->
        seen.put(current, iteration)?.let { existing ->
            existing to iteration - existing
        }
    }
    val remaining = (totalIterations - initial) % cycle
    val index = initial + remaining
    return seen.entries.first { it.value == index }.key
}

fun <T>processAllDirectionsRepeatedly(map: Grid, predicate: (Int, Grid) -> T?): T {
    var current = map
    var i = 0
    while (true) {
        val result = predicate(i, current)
        if (result != null) {
            return result
        }
        current = processAllDirections(current)
        i++
    }
}

private val Grid.score: Int
    get() {
        val sizeY = this.sizeY
        return entries.fold(0) { score, (c, v) ->
            if (v == 'O') {
                score + (sizeY - c.y)
            } else {
                score
            }
        }
    }

private fun processAllDirections(map: Grid): Grid {
    var current = map
    for (direction in listOf(Coordinate.up, Coordinate.left, Coordinate.down, Coordinate.right)) {
        current = process(current, direction)
    }
    return current
}

private fun process(map: Grid, direction: Coordinate): Grid {
    val result = map.toMutableMap()
    val reverse = direction in listOf(Coordinate.down, Coordinate.right)
    val range = map.coordinateRange
    val iterator = if (reverse) range.reverseIterator() else range.iterator()
    while (iterator.hasNext()) {
        val source = iterator.next()
        if (result[source] == 'O') {
            var candidate = source + direction
            var destination: Coordinate? = null
            while (candidate in range) {
                if (result[candidate] == '.') {
                    // continue
                    destination = candidate
                } else {
                    break
                }
                candidate += direction
            }
            if (destination != null) {
                result[source] = '.'
                result[destination] = 'O'
            }
        }
    }
    return result
}