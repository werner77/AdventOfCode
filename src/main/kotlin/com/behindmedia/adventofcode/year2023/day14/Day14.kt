package com.behindmedia.adventofcode.year2023.day14

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing

fun main() {
    val map = CharGrid(read("/2023/day14.txt"))
    timing {
        // Part 1
        println(process(map, Coordinate.up).score)
    }

    timing {
        // Part 2
        println(processAllDirectionsFast(map).score)
    }
}

private fun processAllDirectionsFast(
    map: CharGrid,
    totalIterations: Int = 1_000_000_000
): CharGrid {
    val seen = mutableMapOf<CharGrid, Int>()
    val (initial, cycle) = processAllDirectionsRepeatedly(map) { iteration, current ->
        seen.put(current, iteration)?.let { existing ->
            existing to iteration - existing
        }
    }
    val remaining = (totalIterations - initial) % cycle
    val index = initial + remaining
    return seen.entries.first { it.value == index }.key
}

fun <T> processAllDirectionsRepeatedly(
    map: CharGrid,
    predicate: (Int, CharGrid) -> T?
): T {
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

private val CharGrid.score: Int
    get() {
        val sizeY = this.sizeY
        return fold(0) { score, (c, v) ->
            if (v == 'O') {
                score + (sizeY - c.y)
            } else {
                score
            }
        }
    }

private fun processAllDirections(map: CharGrid): CharGrid {
    var current = map
    for (direction in listOf(Coordinate.up, Coordinate.left, Coordinate.down, Coordinate.right)) {
        current = process(current, direction)
    }
    return current
}

private fun process(
    map: CharGrid,
    direction: Coordinate
): CharGrid {
    val result = map.mutableCopy()
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