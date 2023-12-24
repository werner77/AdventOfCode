package com.behindmedia.adventofcode.year2023.day24

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.parseLines
import kotlin.math.max
import kotlin.math.min

fun main() {
    val data = parseLines("/2023/day24.txt") { line ->
        val (first, second) = line.split("@")
        val (x, y, z) = first.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        val (vx, vy, vz) = second.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        Coordinate3D(x, y, z) to Coordinate3D(vx, vy, vz)
    }

    val range = 200000000000000L.toDouble()..400000000000000L.toDouble()
    //val range = 7.0..27.0
    var ans = 0L
    for (i in 0 until data.size) {
        for (j in i + 1 until data.size) {
            if (findBox(range to range, data[i], data[j], 0.1) != null) {
                ans++
            }
        }
    }
    println(ans)
}

private fun findBox(box: Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>>, first: Pair<Coordinate3D, Coordinate3D>, second: Pair<Coordinate3D, Coordinate3D>, precision: Double) : Pair<Double, Double>? {
    val (xRange, yRange) = box

    if (xRange.isEmpty() || yRange.isEmpty()) return null

    if (!isValidBox(box, first, second)) return null

    if (xRange.endInclusive - xRange.start < precision) {
        return ((xRange.start + xRange.endInclusive) / 2.0 to (yRange.start + yRange.endInclusive) / 2.0)
    }

    // Divide current box in 4 boxes
    val midX = (xRange.start + xRange.endInclusive) / 2
    val midY = (yRange.start + yRange.endInclusive) / 2

    val xRange1 = xRange.start..midX
    val xRange2 = midX..xRange.endInclusive
    val yRange1 = yRange.start..midY
    val yRange2 = midY..yRange.endInclusive

    for (xr in listOf(xRange1, xRange2)) {
        for (yr in listOf(yRange1, yRange2)) {
            return findBox(xr to yr, first, second, precision) ?: continue
        }
    }
    return null
}


/**
 * If both lines cross through the box it is valid
 */
private fun isValidBox(box: Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>>, first: Pair<Coordinate3D, Coordinate3D>, second: Pair<Coordinate3D, Coordinate3D>): Boolean {
    val firstValid = isWithinInFuture(box, first.first, first.second)
    val secondValid = isWithinInFuture(box, second.first, second.second)
    return firstValid && secondValid
}

private fun isWithinInFuture(box: Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>>, pos: Coordinate3D, velocity: Coordinate3D): Boolean {
    val xRangeWithin = timeRangeWithin(pos.x, velocity.x, box.first)
    val yRangeWithin = timeRangeWithin(pos.y, velocity.y, box.second)
    val intersectionRange = xRangeWithin.intersection(yRangeWithin)
    return !intersectionRange.isEmpty() && intersectionRange.endInclusive >= 0.0
}

private fun ClosedFloatingPointRange<Double>.intersection(other: ClosedFloatingPointRange<Double>): ClosedFloatingPointRange<Double> {
    return max(this.start, other.start)..min(this.endInclusive, other.endInclusive)
}

private fun timeRangeWithin(position: Long, velocity: Long, range: ClosedFloatingPointRange<Double>): ClosedFloatingPointRange<Double> {
    val t1 = (range.start - position) / velocity.toDouble()
    val t2 = (range.endInclusive - position) / velocity.toDouble()
    return min(t1, t2)..max(t1, t2)
}
