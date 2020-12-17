package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseMap
import com.behindmedia.adventofcode.common.permutate

class Day17 {

    fun part1(input: String): Int {
        // 3-D map
        val activeCoordinates =
            parseMap(input) { it == '#' }.filter { it.value }.keys.map { HyperCoordinate.of(it.x, it.y, 0) }.toSet()
        val output = simulate(activeCoordinates, 3)
        return output.count()
    }

    fun part2(input: String): Int {
        // 4-D map
        val activeCoordinates =
            parseMap(input) { it == '#' }.filter { it.value }.keys.map { HyperCoordinate.of(it.x, it.y, 0, 0) }.toSet()
        val output = simulate(activeCoordinates, 4)
        return output.count()
    }

    private fun simulate(
        activeCoordinates: Set<HyperCoordinate>,
        dimensionCount: Int,
        times: Int = 6
    ): Set<HyperCoordinate> {
        var currentActiveCoordinates = activeCoordinates
        val permutationRanges = List(dimensionCount) { IntRange(-1, 1) }
        repeat(times) {
            val counts = mutableMapOf<HyperCoordinate, Int>()
            for (coordinate in currentActiveCoordinates) {
                permutate(permutationRanges) {
                    val c = coordinate + it
                    if (c != coordinate) {
                        counts[c] = (counts[c] ?: 0) + 1
                    }
                }
            }
            currentActiveCoordinates = counts.entries.fold(mutableSetOf()) { set, entry ->
                set.apply {
                    if (entry.value == 3 || (entry.value == 2 && currentActiveCoordinates.contains(entry.key))) {
                        add(entry.key)
                    }
                }
            }
        }
        return currentActiveCoordinates
    }

    private data class HyperCoordinate(private val components: List<Int>) {
        val dimensionCount: Int
            get() = components.size

        operator fun get(index: Int): Int {
            return components[index]
        }

        operator fun plus(delta: IntArray): HyperCoordinate {
            return HyperCoordinate(components.mapIndexed { index, value -> value + delta[index] })
        }

        companion object {
            fun of(vararg components: Int): HyperCoordinate {
                return HyperCoordinate(components.toList())
            }
        }
    }
}