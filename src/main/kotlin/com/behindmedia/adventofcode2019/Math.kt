package com.behindmedia.adventofcode2019

import kotlin.math.*

/**
 * Describes the rotation direction (left or right)
 */
enum class RotationDirection {
    Left, Right;

    companion object { }
}

data class Coordinate3D(val x: Int, val y: Int, val z: Int) {

    fun offset(vector: Coordinate3D): Coordinate3D {
        return Coordinate3D(x + vector.x, y + vector.y, z + vector.z)
    }

    fun offset(xOffset: Int, yOffset: Int, zOffset: Int): Coordinate3D {
        return Coordinate3D(x + xOffset, y + yOffset, z + zOffset)
    }

    operator fun get(index: Int): Int {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IllegalArgumentException("Invalid index supplied")
        }
    }
}

/**
 * Describes a two-dimensional coordinate or vector.
 */
data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {

    companion object {
        val origin = Coordinate(0, 0)
    }

    /**
     * Comparison, ordering from top-left to bottom-right
     */
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

    /**
     * Returns the diff with the supplied coordinate as a new coordinate (representing the vector)
     */
    fun vector(to: Coordinate): Coordinate {
        return Coordinate(to.x - this.x, to.y - this.y)
    }

    /**
     * Returns the Manhatten distance to the specified coordinate
     */
    fun manhattenDistance(to: Coordinate): Int {
        return abs(x - to.x) + abs(y - to.y)
    }

    /**
     * Returns the shortest Euclidean distance to the specified coordinate
     */
    fun distance(to: Coordinate): Double {
        val deltaX = (x - to.x).toDouble()
        val deltaY = (y - to.y).toDouble()
        return sqrt( deltaX * deltaX + deltaY * deltaY)
    }

    /**
     * Normalizes the coordinate by dividing both x and y by their greatest common divisor
     */
    fun normalized(): Coordinate {
        val factor = greatestCommonDivisor(abs(this.x), abs(this.y))
        return Coordinate(this.x / factor, this.y / factor)
    }

    /**
     * Rotates this coordinate (representing a vector) using the specified rotation direction
     */
    fun rotate(direction: RotationDirection): Coordinate {
        return when(direction) {
            RotationDirection.Left -> Coordinate(this.y, -this.x)
            RotationDirection.Right -> Coordinate(-this.y, this.x)
        }
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

    operator fun get(index: Int): Int {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IllegalArgumentException("Invalid index supplied")
        }
    }
}

/**
 * Function to compare doubles with an allowed fractional difference
 */
fun Double.isAlmostEqual(other: Double, allowedDifference: Double = 0.000001): Boolean {
    return abs(this - other) < allowedDifference
}

/**
 * Returns the number of 10-based digits (excluding leading 0-s) of this Long
 */
val Long.numberOfDigits: Int
    get() {
        var value = this
        var digitCount = 0
        while (value != 0L) {
            value /= 10
            digitCount++
        }
        return digitCount
    }

/**
 * Computes the greatest common divisor of two integers.
 */
tailrec fun greatestCommonDivisor(a: Long, b: Long): Long {
    if (b == 0L) {
        return a
    }
    return greatestCommonDivisor(b, a % b)
}

tailrec fun greatestCommonDivisor(a: Int, b: Int): Int {
    if (b == 0) {
        return a
    }
    return greatestCommonDivisor(b, a % b)
}

fun leastCommonMultiple(number1: Long, number2: Long): Long {
    return if (number1 == 0L || number2 == 0L) 0 else {
        val gcd = greatestCommonDivisor(number1, number2)
        abs(number1 * number2) / gcd
    }
}

/**
 * Range to enumerate coordinates between the (minx, miny) and (maxx, maxy) found in a list of coordinates.
 */
class CoordinateRange(collection: Collection<Coordinate>) : Iterable<Coordinate>, ClosedRange<Coordinate> {
    private val minCoordinate: Coordinate
    private val maxCoordinate: Coordinate

    init {
        var minX = Integer.MAX_VALUE
        var minY = Integer.MAX_VALUE
        var maxX = Integer.MIN_VALUE
        var maxY = Integer.MIN_VALUE
        for (coordinate in collection) {
            minX = min(minX, coordinate.x)
            minY = min(minY, coordinate.y)
            maxX = max(maxX, coordinate.x)
            maxY = max(maxY, coordinate.y)
        }
        minCoordinate = Coordinate(minX, minY)
        maxCoordinate = Coordinate(maxX, maxY)
    }

    private class CoordinateIterator(val minCoordinate: Coordinate, val maxCoordinate: Coordinate): Iterator<Coordinate> {
        private var nextCoordinate: Coordinate? = if (minCoordinate <= maxCoordinate) minCoordinate else null

        override fun hasNext(): Boolean {
            return nextCoordinate != null
        }

        override fun next(): Coordinate {
            val next = nextCoordinate ?: throw IllegalStateException("Next called on iterator while there are no more elements to iterate over")
            nextCoordinate = when {
                next.x < maxCoordinate.x -> next.offset(1, 0)
                next.y < maxCoordinate.y -> Coordinate(minCoordinate.x, next.y + 1)
                else -> null
            }
            return next
        }
    }

    override fun iterator(): Iterator<Coordinate> = CoordinateIterator(minCoordinate, maxCoordinate)

    override val endInclusive: Coordinate
        get() = maxCoordinate
    override val start: Coordinate
        get() = minCoordinate
}

fun Collection<Coordinate>.range(): CoordinateRange = CoordinateRange(this)