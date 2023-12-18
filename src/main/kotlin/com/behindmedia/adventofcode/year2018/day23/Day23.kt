package com.behindmedia.adventofcode.year2018.day23

import com.behindmedia.adventofcode.common.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import java.util.PriorityQueue

private data class Nanobot(val coordinate: Coordinate3D, val radius: Int)

fun main() {
    val regex = """pos=<(-?\d+),(-?\d+),(-?\d+)>, r=(\d+)""".toRegex()
    val data = parseLines("/2018/day23.txt") { line ->
        regex.matchEntire(line)?.destructured?.let { (x, y, z, r) ->
            Nanobot(Coordinate3D(x.toLong(), y.toLong(), z.toLong()), r.toInt())
        } ?: error("Invalid line $line")
    }
    println(part1(data))
    val c = part2(data)
    println(c)
    println(c.manhattenDistance(Coordinate3D.origin))
}

private class BoxEvaluation(val box: Box, val count: Int): Comparable<BoxEvaluation> {

    constructor(box: Box, data: List<Nanobot>): this(box, box.numberOfIntersections(data))

    override fun compareTo(other: BoxEvaluation): Int {
        return compare(
            { other.count.compareTo(this.count) },
            { this.box.length.compareTo(other.box.length) },
            { this.box.distanceToOrigin.compareTo(other.box.distanceToOrigin) }
        )
    }
}

private data class Box(val origin: Coordinate3D, val length: Long) {

    init {
        assert(length > 0 && (length == 1L || length % 2L == 0L))
    }

    fun manhattenDistance(from: Coordinate3D): Long {
        var distance = 0L
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

    fun numberOfIntersections(data: List<Nanobot>): Int {
        return data.count { (coordinate, range) ->
            this.manhattenDistance(coordinate) <= range
        }
    }

    fun split(): List<Box> {
        if (length == 1L) {
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
            val components = MutableList(3) { 0L }
            for (index in 0..2) {
                components[index] = if (abs(origin[index]) < abs(origin[index] + length - 1L))
                    origin[index] else origin[index] + length - 1L
            }
            return Coordinate3D(components)
        }

    val distanceToOrigin: Long
        get() = Coordinate3D.origin.manhattenDistance(this.nearestCornerToOrigin)
}

private fun encompassingBox(data: List<Nanobot>): Box {
    var length = 0L
    val originComponents = mutableListOf(0L, 0L, 0L)
    for (component in 0..2) {
        val minValue = data.minOfOrNull { it.coordinate[component] } ?: throw IllegalStateException("No entry present")
        val maxValue = data.maxOfOrNull { it.coordinate[component] } ?: throw IllegalStateException("No entry present")
        val range = maxValue - minValue
        var power2Range = 1L
        while (power2Range <= range) power2Range *= 2
        length = max(length, power2Range)
        originComponents[component] = minValue
    }
    return Box(Coordinate3D(originComponents), length)
}

/**
 * Divides the map into boxes of ever decreasing size (until the length of the box is 1), prioritizing the evaluation
 * of promising boxes over non-promising ones. The number of intersections with a box is an upperbound to the number
 * of intersections of any point within the box. So if the intersection count of a box < than the intersection count of
 * a single point we can discard the whole box.
 */
private fun findBestPoint(data: List<Nanobot>): Coordinate3D {
    val pending = PriorityQueue<BoxEvaluation>()
    val box = encompassingBox(data)
    var bestEvaluation: BoxEvaluation? = null
    pending.add(BoxEvaluation(box, data))

    while (pending.isNotEmpty()) {
        val current = pending.poll() ?: throw IllegalStateException("Pending should not be empty")
        if (bestEvaluation != null && current.count < bestEvaluation.count) continue
        if (current.box.length == 1L) {
            // Store as final evaluation, single point
            if (bestEvaluation == null || bestEvaluation < current) {
                bestEvaluation = current
            }
        } else {
            // Divide this box into 8 new smaller boxes, evaluating each of them by counting the number of intersections
            current.box.split().forEach {
                pending.add(BoxEvaluation(it, data))
            }
        }
    }
    return bestEvaluation?.box?.origin ?: throw IllegalStateException("No best location found")
}

// Part 1
private fun part1(data: List<Nanobot>): Int {
    val strongest = data.maxByOrNull { it.radius } ?: throw IllegalStateException("Not found")
    return data.count { bot ->
        strongest.coordinate.manhattenDistance(bot.coordinate) <= strongest.radius
    }
}

// Part 2
private fun part2(data: List<Nanobot>): Coordinate3D {
    return findBestPoint(data)
}