package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.read
import org.junit.Test

class Day23Test {

    fun Map<Coordinate3D, Int>.countInRange(coordinate: Coordinate3D): Int {
        return this.entries.count {
            val distance = coordinate.manhattenDistace(it.key)
            distance <= it.value
        }
    }

    @Test
    fun puzzle1() {
        val map = parseInput(read("/2018/day23.txt"))

        val strongest = map.maxBy { it.value } ?: throw IllegalStateException("Not found")
        var count = 0

        for (entry in map.entries) {
            val distance = strongest.key.manhattenDistace(entry.key)
            if (distance <= strongest.value) {
                count++
            }
        }
        println(count)
    }

    @Test
    fun puzzle2() {
        val map = parseInput(read("/2018/day23.txt"))

        for (component in 0..2) {
            val entryList = map.entries.fold(mutableListOf<Pair<Int, Int>>()) { list, entry ->
                val minValue = entry.key[component] - entry.value
                val maxValue = entry.key[component] + entry.value
                list.add(Pair(minValue, 1))
                list.add(Pair(maxValue, -1))
                list
            }

            var currentCount = 0
            var maxCount = 0
            var lowerBound = Int.MAX_VALUE
            var upperBound = Int.MIN_VALUE
            for (entry in entryList.sortedBy { it.first }) {
                currentCount += entry.second

                if (currentCount > maxCount) {
                    maxCount = currentCount
                    lowerBound = entry.first
                } else if (currentCount == maxCount - 1 && entry.second < 0) {
                    upperBound = entry.first
                }
            }

            val componentName = when(component) {
                0 -> "x"
                1 -> "y"
                2 -> "z"
                else -> throw IllegalStateException("Invalid")
            }

            println("Found $componentName region: [$lowerBound, $upperBound] of size ${upperBound - lowerBound} with count: $maxCount")
        }
    }

    fun parseInput(input: String): Map<Coordinate3D, Int> {
        val map = mutableMapOf<Coordinate3D, Int>()

        val regex = """pos=<(-?\d+),(-?\d+),(-?\d+)>, r=(\d+)""".toRegex()

        for (line in  input.split('\n')) {
            regex.matchEntire(line)?.let {

                val coordinate = Coordinate3D(it.groupValues[1].toInt(), it.groupValues[2].toInt(), it.groupValues[3].toInt())
                val radius = it.groupValues[4].toInt()
                map[coordinate] = radius
            }

        }
        return map
    }

}