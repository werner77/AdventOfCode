package com.behindmedia.adventofcode.year2018.day11

import com.behindmedia.adventofcode.common.*
import kotlin.math.min

private const val gridSize = 300

fun main() {
    val serialNumber = parseLines("/2018/day11.txt") { line ->
        line.toInt()
    }.first()
    part1(serialNumber)
    part2(serialNumber)
}

private fun part2(serialNumber: Int) {
    val prefixSums = calculatePrefixSums(serialNumber)
    var maxRegion: Coordinate3D? = null
    var maxSum = 0
    for (y in 1 until gridSize) {
        for (x in 1 until gridSize) {
            val maxSize = min(gridSize - y + 1, gridSize - x + 1)
            for (k in 1..maxSize) {
                val sum = sumForBlock(x, y, k, prefixSums)
                if (sum > maxSum) {
                    maxSum = sum
                    maxRegion = Coordinate3D(x, y, k)
                }
            }
        }
    }
    println(maxRegion)
}

private fun part1(serialNumber: Int) {
    val minCoordinate = Coordinate(1, 1)
    val maxCoordinate = Coordinate(gridSize, gridSize)
    var maxPower = 0
    var foundCoordinate: Coordinate? = null
    for (c in CoordinateRange(minCoordinate, maxCoordinate)) {
        var power = 0
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                power += calculatePower(Coordinate(c.x + i, c.y + j), serialNumber)
            }
        }
        if (power > maxPower) {
            maxPower = power
            foundCoordinate = c
        }
    }
    println(foundCoordinate ?: error("No coordinate found"))
}

private fun calculatePrefixSums(serialNumber: Int): Array<IntArray> {
    // Calculate prefix sums
    val prefixSums = Array(gridSize + 1) {
        IntArray(gridSize + 1)
    }
    for (y in 1..gridSize) {
        for (x in 1..gridSize) {
            val power = calculatePower(Coordinate(x, y), serialNumber)
            prefixSums[y][x] = prefixSums[y - 1][x] + prefixSums[y][x - 1] -
                    prefixSums[y - 1][x - 1] + power
        }
    }
    return prefixSums
}

/*
Find the fuel cell's rack ID, which is its X coordinate plus 10.
Begin with a power level of the rack ID times the Y coordinate.
Increase the power level by the value of the grid serial number (your puzzle input).
Set the power level to itself multiplied by the rack ID.
Keep only the hundreds digit of the power level (so 12345 becomes 3; numbers with no hundreds digit become 0).
Subtract 5 from the power level.
 */

private fun calculatePower(coordinate: Coordinate, serialNumber: Int): Int {
    val rackId = coordinate.x + 10
    var value = rackId * coordinate.y
    value += serialNumber
    value *= rackId
    value /= 100
    value %= 10
    value -= 5
    return value
}

private fun sumForBlock(x: Int, y: Int, blockSize: Int, prefixSums: Array<IntArray>): Int {
    val maxX = x + blockSize - 1
    val maxY = y + blockSize - 1
    val minX = x - 1
    val minY = y - 1

    val area1 = prefixSums[minX][minY]
    val area2 = prefixSums[maxX][minY]
    val area3 = prefixSums[minX][maxY]

    return prefixSums[maxX][maxY] - area2 - area3 + area1
}