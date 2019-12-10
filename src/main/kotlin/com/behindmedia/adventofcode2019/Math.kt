package com.behindmedia.adventofcode2019

import kotlin.math.*

data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {

    companion object {
        val origin = Coordinate(0, 0)
    }

    override fun compareTo(other: Coordinate): Int {
        var result = this.y.compareTo(other.y)
        if (result == 0) {
            result = this.x.compareTo(other.x)
        }
        return result
    }

    fun offset(xOffset: Int, yOffset: Int): Coordinate {
        return Coordinate(x + xOffset, y + yOffset)
    }

    fun offset(vector: Coordinate): Coordinate {
        return offset(vector.x, vector.y)
    }

    fun vector(to: Coordinate): Coordinate {
        return Coordinate(to.x - this.x, to.y - this.y)
    }

    fun manhattenDistance(to: Coordinate): Int {
        return abs(x - to.x) + abs(y - to.y)
    }

    fun distance(to: Coordinate): Double {
        val deltaX = (x - to.x).toDouble()
        val deltaY = (y - to.y).toDouble()
        return sqrt( deltaX * deltaX + deltaY * deltaY)
    }

    fun normalized(): Coordinate {
        val factor = greatestCommonDivisor(abs(this.x), abs(this.y))
        return Coordinate(this.x / factor, this.y / factor)
    }

    /**
     * Returns the angle between 0 and 2 * PI relative to the specified vector
     */
    fun angle(to: Coordinate): Double {
        val a = to.x.toDouble()
        val b = to.y.toDouble()
        val c = this.x.toDouble()
        val d = this.y.toDouble()

        val atanA = atan2(a, b)
        val atanB = atan2(c, d)

        val angle = atanA - atanB

        return if (angle < 0) angle + 2 * PI else angle
    }
}

fun Double.isAlmostEqual(other: Double, allowedDifference: Double = 0.000001): Boolean {
    return abs(this - other) < allowedDifference
}

val Long.numberOfDigits: Int
    get() = numberOfDigits(this)

private fun numberOfDigits(number: Long): Int {
    var value = number
    var digitCount = 0
    while (value != 0L) {
        value /= 10
        digitCount++
    }
    return digitCount
}

tailrec fun greatestCommonDivisor(a: Int, b: Int): Int {
    if (b == 0) {
        return a
    }
    return greatestCommonDivisor(b, a % b)
}
