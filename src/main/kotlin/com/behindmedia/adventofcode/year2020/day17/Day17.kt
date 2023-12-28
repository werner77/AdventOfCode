package com.behindmedia.adventofcode.year2020.day17

import com.behindmedia.adventofcode.common.parseMapFromString
import com.behindmedia.adventofcode.common.permute
import com.behindmedia.adventofcode.common.read

fun main() {
    val data = read("/2020/day17.txt")
    println(part1(data))
    println(part2(data))
}

private fun part1(input: String): Int {
    // 3-D map
    val activeCoordinates =
        parseMapFromString(input) { it == '#' }.filter { it.value }.keys.map { HyperCoordinate.of(it.x, it.y, 0) }.toSet()
    val output = simulate(activeCoordinates, 3)
    return output.count()
}

private fun part2(input: String): Int {
    // 4-D map
    val activeCoordinates =
        parseMapFromString(input) { it == '#' }.filter { it.value }.keys.map { HyperCoordinate.of(it.x, it.y, 0, 0) }.toSet()
    val output = simulate(activeCoordinates, 4)
    return output.count()
}

private fun simulate(
    activeCoordinates: Set<HyperCoordinate>,
    dimensionCount: Int,
    times: Int = 6
): Set<HyperCoordinate> {
    var result = activeCoordinates
    val permutationRanges = List(dimensionCount) { -1..1 }
    repeat(times) {
        val counts = mutableMapOf<HyperCoordinate, Int>()
        for (coordinate in result) {
            permute(permutationRanges) {
                val c = coordinate + it
                if (c != coordinate) {
                    counts[c] = (counts[c] ?: 0) + 1
                }
            }
        }
        result = counts.entries.fold(mutableSetOf()) { set, entry ->
            set.apply {
                if (entry.value == 3 || (entry.value == 2 && result.contains(entry.key))) {
                    add(entry.key)
                }
            }
        }
    }
    return result
}

private data class HyperCoordinate(private val components: List<Int>) {
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