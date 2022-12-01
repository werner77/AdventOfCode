package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.compare
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

class Day23 {

    private class BoxEvaluation(val box: Box, val count: Int): Comparable<BoxEvaluation> {

        constructor(box: Box, map: Map<Coordinate3D, Int>): this(box, box.numberOfIntersections(map))

        override fun compareTo(other: BoxEvaluation): Int {
            return compare(
                { other.count.compareTo(this.count) },
                { this.box.length.compareTo(other.box.length) },
                { this.box.distanceToOrigin.compareTo(other.box.distanceToOrigin) }
            )
        }
    }

    private data class Box(val origin: Coordinate3D, val length: Int) {

        init {
            assert(length > 0 && (length == 1 || length % 2 == 0))
        }

        fun manhattenDistance(from: Coordinate3D): Int {
            var distance = 0
            for (component in 0..2) {
                val minValue = origin[component]
                val maxValue = origin[component] + length - 1
                val inside = from[component] in minValue..maxValue
                if (!inside) {
                    distance += min(abs(from[component] - minValue), abs(from[component] - maxValue))
                }
            }
            return distance
        }

        fun numberOfIntersections(map: Map<Coordinate3D, Int>): Int {
            return map.count { (coordinate, range) ->
                this.manhattenDistance(coordinate) <= range
            }
        }

        fun split(): List<Box> {
            if (length == 1) {
                throw IllegalStateException("Cannot split a box of length 1")
            }

            val result = mutableListOf<Box>()
            val newLength = length / 2
            val offset = listOf(0, 1)

            for (i in 0..1) {
                val x = origin[0] + offset[i] * newLength
                for (j in 0..1) {
                    val y = origin[1] + offset[j] * newLength
                    for (k in 0..1) {
                        val z = origin[2] + offset[k] * newLength
                        result.add(Box(Coordinate3D(x, y, z), newLength))
                    }
                }
            }
            return result
        }

        val nearestCornerToOrigin: Coordinate3D
            get() {
                val components = MutableList(3) { 0 }
                for (index in 0..2) {
                    components[index] = if (abs(origin[index]) < abs(origin[index] + length - 1))
                        origin[index] else origin[index] + length - 1
                }
                return Coordinate3D(components)
            }

        val distanceToOrigin: Int
            get() = Coordinate3D.origin.manhattenDistance(this.nearestCornerToOrigin)
    }

    private fun Map<Coordinate3D, Int>.encompassingBox(): Box {
        var length = 0
        val originComponents = mutableListOf(0, 0, 0)
        for (component in 0..2) {
            val minValue = this.keys.map { it[component] }.minOrNull() ?: throw IllegalStateException("No entry present")
            val maxValue = this.keys.map { it[component] }.maxOrNull() ?: throw IllegalStateException("No entry present")

            val range = maxValue - minValue
            var power2Range = 1
            while (power2Range <= range) power2Range *= 2

            length = max(length, power2Range)
            originComponents[component] = minValue
        }
        return Box(Coordinate3D(originComponents), length)
    }

    /**
     * Divides the map into boxes of ever decreasing size (until the length of the box is 1), prioritizing the evaluation
     * of promising boxes over non-promising ones. The number of intersections with a box is an upperbound to the number
     * of intersections of any point within the box. So if a the intersection count of a box < than the intersection count of
     * a single point we can discard the whole box.
     */
    private fun Map<Coordinate3D, Int>.findBestPoint(): Coordinate3D {
        val pending = PriorityQueue<BoxEvaluation>()
        val box = encompassingBox()
        var bestEvaluation: BoxEvaluation? = null
        pending.add(BoxEvaluation(box, this))

        while (pending.isNotEmpty()) {
            val current = pending.poll() ?: throw IllegalStateException("Pending should not be empty")
            if (bestEvaluation != null && current.count < bestEvaluation.count) continue
            if (current.box.length == 1) {
                // Store as final evaluation, single point
                if (bestEvaluation == null || bestEvaluation < current) {
                    bestEvaluation = current
                }
            } else {
                // Divide this box into 8 new smaller boxes, evaluating each of them by counting the number of intersections
                current.box.split().forEach {
                    pending.add(BoxEvaluation(it, this))
                }
            }
        }
        return bestEvaluation?.box?.origin ?: throw IllegalStateException("No best location found")
    }

    private fun parseInput(input: String): Map<Coordinate3D, Int> {
        val regex = """^pos=<(-?\d+),(-?\d+),(-?\d+)>, r=(\d+)$""".toRegex()
        return input.split(('\n')).fold(mutableMapOf()) { map, line ->
            regex.matchEntire(line)?.let {
                val coordinate = Coordinate3D(
                    it.groupValues[1].toInt(),
                    it.groupValues[2].toInt(),
                    it.groupValues[3].toInt()
                )
                val radius = it.groupValues[4].toInt()
                map[coordinate] = radius
            } ?: throw IllegalStateException("Could not parse line: $line")
            map
        }
    }

    // Part 1
    fun strongestCount(input: String): Int {
        val map = parseInput(input)
        val strongest = map.maxByOrNull { it.value } ?: throw IllegalStateException("Not found")
        return map.count {
            strongest.key.manhattenDistance(it.key) <= strongest.value
        }
    }

    // Part 2
    fun bestPoint(input: String): Coordinate3D {
        return parseInput(input).findBestPoint()
    }

}