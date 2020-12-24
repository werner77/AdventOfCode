package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.parseLines

class Day24 {
    enum class Direction(val coordinate: Coordinate) {
        E(Coordinate(1, 0)),
        SE(Coordinate(1, -1)),
        SW(Coordinate(0, -1)),
        W(Coordinate(-1, 0)),
        NW(Coordinate(-1, 1)),
        NE(Coordinate(0, 1))
    }

    fun part1(input: String) : Int {
        return parseBlackCoordinates(input).size
    }

    fun part2(input: String) : Int {
        val blackCoordinates = parseBlackCoordinates(input)
        var current: Set<Coordinate> = blackCoordinates
        repeat(100) {
            current = flip(current)
        }
        return current.size
    }

    private fun parseBlackCoordinates(input: String): MutableSet<Coordinate> {
        return parseLines(input) {
            parseCoordinate(it)
        }.fold(mutableSetOf()) { set, coordinate ->
            set.apply {
                if (!set.remove(coordinate)) {
                    set.add(coordinate)
                }
            }
        }
    }

    private fun parseCoordinate(string: String) : Coordinate {
        val result = mutableListOf<Direction>()
        val buffer = StringBuilder()
        for (c in string) {
            buffer.append(c)

            try {
                val direction = Direction.valueOf(buffer.toString().toUpperCase())
                result.add(direction)
                buffer.clear()
            } catch (e: IllegalArgumentException) {
                //ignore
            }
        }
        return result.coordinate()
    }

    private fun List<Direction>.coordinate(start: Coordinate = Coordinate.origin) : Coordinate {
        return this.fold(start) { current, direction ->
            current + direction.coordinate
        }
    }

    private fun flip(coordinates: Set<Coordinate>): Set<Coordinate> {
        val result = HashSet<Coordinate>(coordinates.size * 2)
        val countMap = coordinates.fold(mutableMapOf<Coordinate, Int>()) { map, coordinate ->
            map.also { m ->
                m.putIfAbsent(coordinate, 0)
                Direction.values().forEach {
                    val neighbour = coordinate + it.coordinate
                    m[neighbour] = (m[neighbour] ?: 0) + 1
                }
            }
        }

        for ((coordinate, neighbourCount) in countMap) {
            val black = coordinates.contains(coordinate)
            val newBlack = if (black && (neighbourCount == 0 || neighbourCount > 2)) {
                false
            } else if (!black && neighbourCount == 2) {
                true
            } else {
                black
            }
            if (newBlack) {
                result.add(coordinate)
            }
        }
        return result
    }
}