package com.behindmedia.adventofcode.year2018.day17

import com.behindmedia.adventofcode.common.*

fun main() {
    val regex = """(x|y)=(\d+), (x|y)=(\d+)\.\.(\d+)""".toRegex()
    val map = mutableMapOf<Coordinate, Char>()
    parseLines("/2018/day17.txt") { line ->
        regex.matchEntire(line)?.destructured?.let { (sc, sv, dc, dvl, dvh) ->
            when (sc) {
                "x" -> {
                    val x = sv.toInt()
                    require(dc == "y")
                    for (y in dvl.toInt()..dvh.toInt()) {
                        map[Coordinate(x, y)] = '#'
                    }
                }

                "y" -> {
                    val y = sv.toInt()
                    require(dc == "x")
                    for (x in dvl.toInt()..dvh.toInt()) {
                        map[Coordinate(x, y)] = '#'
                    }
                }

                else -> {
                    error("Invalid static coordinate")
                }
            }
        } ?: error("Could not match line: $line")
    }

    val range = map.keys.range()
    val origin = Coordinate(500, 0)
    flow(map, origin, Coordinate.down, range.endInclusive.y)

    map.printMap('.')

    // Part1
    println(map.entries.count { (c, v) -> c.y in range.start.y..range.endInclusive.y && v in listOf('~', '|') })

    // Part2
    println(map.entries.count { (c, v) -> c.y in range.start.y..range.endInclusive.y && v == '~' })
}

// Returns false if flowing came to a halt, true if it continues
private fun flow(map: MutableMap<Coordinate, Char>, coordinate: Coordinate, direction: Coordinate, maxY: Int): Boolean {
    val current = coordinate + direction

    // Endless flowing from here, don't go any further
    if (current.y > maxY) {
        return true
    }

    val value = map[current]
    if (value == '|') {
        return true
    }

    // Cannot flow through rock or settled water
    if (value in listOf('#', '~')) {
        return false
    }

    // Mark as flowing
    map[current] = '|'

    // Try down, if not flowing, try left and right

    return if (!flow(map, current, Coordinate.down, maxY)) {
        val flowLeft = if (direction != Coordinate.right) {
            flow(map, current, Coordinate.left, maxY)
        } else {
            false
        }
        val flowRight = if (direction != Coordinate.left) {
            flow(map, current, Coordinate.right, maxY)
        } else {
            false
        }
        val couldFlow = flowLeft || flowRight
        if (!couldFlow && direction == Coordinate.down) {
            // Settle left and right
            map[current] = '~'
            for (d in listOf(Coordinate.left, Coordinate.right)) {
                var c = current + d
                while (map[c] == '|') {
                    map[c] = '~'
                    c += d
                }
            }
        }
        couldFlow
    } else {
        true
    }
}

