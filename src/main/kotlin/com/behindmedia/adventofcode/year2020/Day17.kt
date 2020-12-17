package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseMap

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

    private fun activeNeighbourCount(
        coordinate: HyperCoordinate,
        activeCoordinates: Set<HyperCoordinate>
    ): Int {
        var activeNeighbourCount = 0
        permutate(
            List(coordinate.dimensionCount) { d -> IntRange(coordinate[d] - 1, coordinate[d] + 1) }
        ) {
            val c = HyperCoordinate(it)
            if (c != coordinate && activeCoordinates.contains(c)) {
                activeNeighbourCount += 1
            }
        }
        return activeNeighbourCount
    }

    private fun simulate(
        activeCoordinates: Set<HyperCoordinate>,
        dimensionCount: Int,
        times: Int = 6
    ): Set<HyperCoordinate> {
        var currentActiveCoordinates = activeCoordinates
        for (i in 0 until times) {
            val ranges = List(dimensionCount) { d ->
                IntRange(
                    currentActiveCoordinates.minOf { it[d] } - 1,
                    currentActiveCoordinates.maxOf { it[d] } + 1
                )
            }
            val newActiveCoordinates = mutableSetOf<HyperCoordinate>()
            permutate(ranges, 0) {
                val coordinate = HyperCoordinate(it)
                val activeNeighbourCount = activeNeighbourCount(coordinate, currentActiveCoordinates)
                if (activeNeighbourCount == 3 ||
                    (currentActiveCoordinates.contains(coordinate) && activeNeighbourCount == 2)
                ) {
                    newActiveCoordinates.add(coordinate)
                }
            }
            currentActiveCoordinates = newActiveCoordinates
        }
        return currentActiveCoordinates
    }

    private fun permutate(
        ranges: List<IntRange>,
        dimension: Int = 0,
        values: IntArray = IntArray(ranges.size),
        perform: (IntArray) -> Unit
    ) {
        if (dimension == values.size) {
            perform(values)
            return
        }
        for (i in ranges[dimension].first..ranges[dimension].last) {
            values[dimension] = i
            permutate(ranges, dimension + 1, values, perform)
        }
    }

    private class HyperCoordinate(components: IntArray) {
        private val components = components.copyOf()

        val dimensionCount: Int
            get() = components.size

        operator fun get(index: Int): Int {
            return components[index]
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is HyperCoordinate) return false
            return components.contentEquals(other.components)
        }

        override fun hashCode(): Int {
            return components.contentHashCode()
        }

        override fun toString(): String {
            return components.contentToString()
        }

        operator fun plus(delta: IntArray): HyperCoordinate {
            return HyperCoordinate(IntArray(dimensionCount) { i -> this[i] + delta[i] })
        }

        companion object {
            fun of(vararg components: Int): HyperCoordinate {
                return HyperCoordinate(components)
            }
        }
    }
}