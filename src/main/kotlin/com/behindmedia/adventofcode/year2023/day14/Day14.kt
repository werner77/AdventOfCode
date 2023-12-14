package com.behindmedia.adventofcode.year2023.day14

import com.behindmedia.adventofcode.common.Coordinate
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
        val (initial, cycle, state) = findCycle(map)
        val remaining = (1_000_000_000 - initial) % cycle
        println(processAllDirectionsRepeatedly(state) { iteration, current -> current.takeIf { iteration == remaining }?.score })
    }
}

private fun findCycle(map: Map<Coordinate, Char>): Triple<Int, Int, Map<Coordinate, Char>> {
    val seen = mutableMapOf<Map<Coordinate, Char>, Int>()
    return processAllDirectionsRepeatedly(map) { iteration, current ->
        seen.put(current, iteration)?.let { existing ->
            Triple(existing, iteration - existing, current)
        }
    }
}

fun <T>processAllDirectionsRepeatedly(map: Map<Coordinate, Char>, predicate: (Int, Map<Coordinate, Char>) -> T?): T {
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

private val Map<Coordinate, Char>.score: Int
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

private fun processAllDirections(map: Map<Coordinate, Char>): Map<Coordinate, Char> {
    var current = map
    for (direction in listOf(Coordinate.up, Coordinate.left, Coordinate.down, Coordinate.right)) {
        current = process(current, direction)
    }
    return current
}

private fun process(map: Map<Coordinate, Char>, direction: Coordinate): Map<Coordinate, Char> {
    val result = map.toMutableMap()
    val reverse = direction in listOf(Coordinate.down, Coordinate.right)
    val range = map.coordinateRange
    val iterator = if (reverse) range.reverseIterator() else range.iterator()
    while (iterator.hasNext()) {
        val c = iterator.next()
        if (result[c] == 'O') {
            var candidate = c + direction
            var swapCoordinate: Coordinate? = null
            while (candidate in range) {
                if (result[candidate] == '.') {
                    // continue
                    swapCoordinate = candidate
                } else {
                    break
                }
                candidate += direction
            }
            if (swapCoordinate != null) {
                result[c] = '.'
                result[swapCoordinate] = 'O'
            }
        }
    }
    return result
}