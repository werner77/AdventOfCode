package com.behindmedia.adventofcode.common

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Describes a two-dimensional coordinate or vector.
 */
data class LongCoordinate(override val x: Long, override val y: Long) : Comparable<LongCoordinate>, Point2D<Long> {

    companion object {
        val origin = LongCoordinate(0, 0)
        val up = LongCoordinate(0, -1)
        val down = LongCoordinate(0, 1)
        val left = LongCoordinate(-1, 0)
        val right = LongCoordinate(1, 0)
        val upLeft = up + left
        val downLeft = down + left
        val upRight = up + right
        val downRight = down + right
        val directNeighbourDirections = arrayOf(up, left, right, down)
        val indirectNeighbourDirections = arrayOf(upLeft, downLeft, upRight, downRight)
        val allNeighbourDirections = directNeighbourDirections + indirectNeighbourDirections
    }

    /**
     * Comparison, ordering from top-left to bottom-right
     */
    override fun compareTo(other: LongCoordinate): Int {
        return compare(
            { this.y.compareTo(other.y) },
            { this.x.compareTo(other.x) }
        )
    }

    fun offset(xOffset: Long, yOffset: Long): LongCoordinate {
        return LongCoordinate(x + xOffset, y + yOffset)
    }

    fun offset(vector: LongCoordinate): LongCoordinate {
        return offset(vector.x, vector.y)
    }

    /**
     * Returns the diff with the supplied coordinate as a new coordinate (representing the vector)
     */
    fun vector(to: LongCoordinate): LongCoordinate {
        return LongCoordinate(to.x - this.x, to.y - this.y)
    }

    /**
     * Returns the Manhatten distance to the specified coordinate
     */
    fun manhattenDistance(to: LongCoordinate): Long {
        return abs(x - to.x) + abs(y - to.y)
    }

    /**
     * Returns the shortest Euclidean distance to the specified coordinate
     */
    fun distance(to: LongCoordinate): Double {
        val deltaX = (x - to.x).toDouble()
        val deltaY = (y - to.y).toDouble()
        return sqrt(deltaX * deltaX + deltaY * deltaY)
    }

    /**
     * The invers
     */
    fun inverted(): LongCoordinate {
        return LongCoordinate(-x, -y)
    }

    /**
     * Normalizes the coordinate by dividing both x and y by their greatest common divisor
     */
    fun normalized(): LongCoordinate {
        val factor = greatestCommonDivisor(abs(this.x), abs(this.y))
        return LongCoordinate(this.x / factor, this.y / factor)
    }

    /**
     * Rotates this coordinate (representing a vector) using the specified rotation direction
     */
    fun rotate(direction: RotationDirection): LongCoordinate {
        return when (direction) {
            RotationDirection.Left -> LongCoordinate(this.y, -this.x)
            RotationDirection.Right -> LongCoordinate(-this.y, this.x)
        }
    }

    /**
     * Optionally rotates this coordinate (representing a vector). Does nothing if the supplied direction is null.
     */
    fun optionalRotate(direction: RotationDirection?): LongCoordinate {
        return when (direction) {
            null -> this
            else -> rotate(direction)
        }
    }

    fun directNeighbourSequence(): Sequence<LongCoordinate> {
        return sequence {
            repeat(4) {
                yield(this@LongCoordinate + directNeighbourDirections[it])
            }
        }
    }

    fun indirectNeighbourSequence(): Sequence<LongCoordinate> {
        return sequence {
            repeat(4) {
                yield(this@LongCoordinate + indirectNeighbourDirections[it])
            }
        }
    }

    fun allNeighbourSequence(): Sequence<LongCoordinate> {
        return sequence {
            repeat(8) {
                yield(this@LongCoordinate + allNeighbourDirections[it])
            }
        }
    }

    /**
     * Returns the direct neighbours of this coordinate
     */
    val directNeighbours: List<LongCoordinate>
        get() {
            return List(4) {
                this + directNeighbourDirections[it]
            }
        }

    /**
     * Returns the indirect neighbours of this coordinate, defined as the diagonal neighbours
     */
    val indirectNeighbours: List<LongCoordinate>
        get() {
            return List(4) {
                this + indirectNeighbourDirections[it]
            }
        }

    val allNeighbours: List<LongCoordinate>
        get() = List(8) {
            if (it < 4) directNeighbours[it] else indirectNeighbours[it - 4]
        }

    val isHorizontal: Boolean
        get() = this.y == 0L

    val isVertical: Boolean
        get() = this.x == 0L

    /**
     * Returns the angle between 0 and 2 * PI relative to the specified vector
     */
    fun positiveAngle(to: LongCoordinate): Double = positiveAngle(angle(to))

    /**
     * Returns the angle between -PI and PI relative to the specified vector
     */
    fun normalizedAngle(to: LongCoordinate): Double = normalizedAngle(angle(to))

    fun angle(to: LongCoordinate): Double {
        val a = to.x.toDouble()
        val b = to.y.toDouble()
        val c = this.x.toDouble()
        val d = this.y.toDouble()

        val atanA = atan2(a, b)
        val atanB = atan2(c, d)

        return (atanA - atanB)
    }

    /**
     * Returns the angle between 0 and 2 * PI relative to the specified vector
     */
    fun positiveAngle(to: Coordinate): Double = positiveAngle(angle(to))

    /**
     * Returns the angle between -PI and PI relative to the specified vector
     */
    fun normalizedAngle(to: Coordinate): Double = normalizedAngle(angle(to))

    fun angle(to: Coordinate): Double {
        val a = to.x.toDouble()
        val b = to.y.toDouble()
        val c = this.x.toDouble()
        val d = this.y.toDouble()

        val atanA = atan2(a, b)
        val atanB = atan2(c, d)

        return (atanA - atanB)
    }

    operator fun get(index: Int): Long {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IllegalArgumentException("Invalid index supplied")
        }
    }

    operator fun plus(other: LongCoordinate): LongCoordinate {
        return offset(other)
    }

    operator fun unaryMinus(): LongCoordinate {
        return inverted()
    }

    operator fun minus(other: LongCoordinate): LongCoordinate {
        return offset(other.inverted())
    }

    operator fun times(other: LongCoordinate): LongCoordinate {
        return LongCoordinate(this.x * other.x, this.y * other.y)
    }

    operator fun times(scalar: Long): LongCoordinate {
        return LongCoordinate(this.x * scalar, this.y * scalar)
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}

val Collection<LongCoordinate>.minX: Long
    get() = this.minOf { it.x }

val Collection<LongCoordinate>.minY: Long
    get() = this.minOf { it.y }

val Collection<LongCoordinate>.maxX: Long
    get() = this.maxOf { it.x }

val Collection<LongCoordinate>.maxY: Long
    get() = this.maxOf { it.y }

val Collection<LongCoordinate>.sizeX: Long
    get() = this.maxX + 1

val Collection<LongCoordinate>.sizeY: Long
    get() = this.maxY + 1

val <E> Map<LongCoordinate, E>.minX: Long
    get() = this.keys.minX

val <E> Map<LongCoordinate, E>.minY: Long
    get() = this.keys.minY

val <E> Map<LongCoordinate, E>.maxX: Long
    get() = this.keys.maxX

val <E> Map<LongCoordinate, E>.maxY: Long
    get() = this.keys.maxY

val <E> Map<LongCoordinate, E>.sizeX: Long
    get() = this.maxX + 1

val <E> Map<LongCoordinate, E>.sizeY: Long
    get() = this.maxY + 1

val <E> Map<LongCoordinate, E>.minCoordinate: LongCoordinate
    get() = LongCoordinate(minX, minY)

val <E> Map<LongCoordinate, E>.maxCoordinate: LongCoordinate
    get() = LongCoordinate(maxX, maxY)


