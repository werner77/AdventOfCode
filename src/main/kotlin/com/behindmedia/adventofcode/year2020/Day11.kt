package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.parseMap

class Day11 {

    private fun process(input: String, simulate: (Map<Coordinate, Char>) -> Map<Coordinate, Char>?): Int {
        var map = parseMap(input) { it }
        while (true) {
            val newMap = simulate(map) ?: break
            map = newMap
        }
        return map.values.count { it == '#' }
    }

    fun part1(input: String): Int {
        return process(input, this::simulate1)
    }

    fun part2(input: String): Int {
        return process(input, this::simulate2)
    }

    private fun simulate1(map: Map<Coordinate, Char>): Map<Coordinate, Char>? {
        val result = map.toMutableMap()
        var changed = false
        for (entry in result) {
            if (entry.value == '.') continue

            val occupiedNeighbourCount = entry.key.allNeighbours.count { '#' == map[it] }
            if (entry.value == 'L' && occupiedNeighbourCount == 0) {
                entry.setValue('#')
                changed = true
            } else if (entry.value == '#' && occupiedNeighbourCount >= 4) {
                entry.setValue('L')
                changed = true
            }
        }
        return if (changed) result else null
    }

    private fun simulate2(map: Map<Coordinate, Char>): Map<Coordinate, Char>? {
        val result = map.toMutableMap()
        var changed = false
        for (entry in result.entries) {
            if (entry.value == '.') continue

            var occupiedNeighbourCount = 0
            for (vector in Coordinate.allNeighbourDirections) {
                var candidate = entry.key
                do {
                    candidate += vector
                    val stop = when (map[candidate]) {
                        '#' -> {
                            occupiedNeighbourCount += 1
                            true
                        }
                        'L' -> true
                        null -> true
                        else -> false
                    }
                } while(!stop)
            }
            if (entry.value == 'L' && occupiedNeighbourCount == 0) {
                entry.setValue('#')
                changed = true
            } else if (entry.value == '#' && occupiedNeighbourCount >= 5) {
                entry.setValue('L')
                changed = true
            }
        }
        return if (changed) result else null
    }
}