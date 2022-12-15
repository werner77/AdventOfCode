package com.behindmedia.adventofcode.year2022.day15

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.whenNotNull
import kotlin.math.abs
import kotlin.math.max

fun main() {
    val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
    val data = parseLines("/2022/day15.txt") { line ->
        whenNotNull(regex.matchEntire(line)?.destructured) { (sensorX, sensorY, beaconX, beaconY) ->
            val sensor = Coordinate(sensorX.toInt(), sensorY.toInt())
            val beacon = Coordinate(beaconX.toInt(), beaconY.toInt())
            Pair(sensor, beacon)
        } ?: error("Could not match line")
    }
    val size = 4000000
    part1(data, size / 2)
    part2(data, size)
}

private val Coordinate.tuningFrequency: Long
    get() = 4000000L * x.toLong() + y.toLong()

private fun findRanges(data: List<Pair<Coordinate, Coordinate>>, y: Int): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    for ((sensor, beacon) in data) {
        val maxDistance = sensor.manhattenDistance(beacon)
        val maxDistanceAtY = maxDistance - abs(y - sensor.y)
        if (maxDistanceAtY >= 0) {
            result += Pair(sensor.x - maxDistanceAtY, sensor.x + maxDistanceAtY)
        }
    }
    result.sortWith(compareBy({ it.first }, { it.second }))
    return result
}

private fun part1(data: List<Pair<Coordinate, Coordinate>>, y: Int) {
    val beacons = data.map { it.second }.toSet()
    val ranges = findRanges(data, y)
    var count = 0
    var x = Int.MIN_VALUE
    for ((x1, x2) in ranges) {
        if (x2 >= x) {
            count += x2 - max(x, x1) + 1
        }
        x = max(x, x2 + 1)
    }
    // Subtract the number of beacons that are on this y-coordinate
    count -= beacons.count { it.y == y }
    println(count)
}

private fun part2(data: List<Pair<Coordinate, Coordinate>>, size: Int) {
    val candidates = mutableSetOf<Coordinate>()
    for (y in 0..size) {
        val ranges = findRanges(data, y)
        var x = 0
        for ((start, end) in ranges) {
            // Check whether there is a free point
            while (x < start && x <= size) {
                candidates.add(Coordinate(x++, y))
            }
            x = max(x, end + 1)
            if (x > size) break
        }
        while (x <= size) {
            candidates += Coordinate(x++, y)
        }
    }
    // There should be only one coordinate
    val coordinate = candidates.single()
    println(coordinate)
    println(coordinate.tuningFrequency)
}