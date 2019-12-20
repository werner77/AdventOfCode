package com.behindmedia.adventofcode2019

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.*

/**
 * Describes the rotation direction (left or right)
 */
enum class RotationDirection {
    Left, Right;

    companion object { }
}

/**
 * Describes a three-dimensional coordinate or vector
 */
data class Coordinate3D(val x: Int, val y: Int, val z: Int) {

    companion object {
        val origin = Coordinate3D(0, 0, 0)
    }

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

data class CoordinatePath(val coordinate: Coordinate, val pathLength: Int)

/**
 * Describes a two-dimensional coordinate or vector.
 */
data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {

    companion object {
        val origin = Coordinate(0, 0)
        val up = Coordinate(0, -1)
        val down = Coordinate(0, 1)
        val left = Coordinate(-1, 0)
        val right = Coordinate(1, 0)
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

    fun inverted(): Coordinate {
        return Coordinate(-x, -y)
    }

    /**
     * Normalizes the coordinate by dividing both x and y by their greatest common divisor
     */
    fun normalized(): Coordinate {
        val factor = greatestCommonDivisor(abs(this.x.toLong()), abs(this.y.toLong())).toInt()
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

    fun optionalRotate(direction: RotationDirection?): Coordinate {
        return when(direction) {
            null -> this
            else -> rotate(direction)
        }
    }

    /**
     * Returns the direct neighbours of this coordinate
     */
    val neighbours: List<Coordinate>
        get() {
            return List<Coordinate>(4) {
                val xOffset = it % 2
                val sign = if (it < 1 || it > 2) -1 else 1
                val yOffset = (it + 1) % 2
                Coordinate(this.x + sign * xOffset, this.y + sign * yOffset)
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

    /**
     * Breadth first search to find the shortest path to all reachable coordinates in a single sweep
     */
    inline fun <T>reachableCoordinates(from: Coordinate, reachable: (Coordinate) -> Boolean, process: (CoordinatePath) -> T?): T? {

//        return reachableNodes(from,
//            neighbours = {
//                it.neighbours
//            },
//            process = process, reachable = reachable
//        )

        val list = ArrayDeque<CoordinatePath>()
        val visited = mutableSetOf<Coordinate>()
        visited.add(from)
        list.add(CoordinatePath(from, 0))

        while(true) {
            val current = try {
                list.pop()
            } catch (e: NoSuchElementException) {
                return null
            }
            if (current.coordinate != from) {
                val result = process(current)
                if (result != null) {
                    return result
                }
            }

            for (neighbour in current.coordinate.neighbours) {
                if (reachable(neighbour) && !visited.contains(neighbour)) {
                    list.add(CoordinatePath(neighbour, current.pathLength + 1))
                }
            }
            visited.add(current.coordinate)
        }
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

fun leastCommonMultiple(a: Long, b: Long): Long {
    return if (a == 0L || b == 0L) 0 else {
        val gcd = greatestCommonDivisor(a, b)
        abs(a * b) / gcd
    }
}

fun Long.divideCeil(other: Long): Long {
    return (this + other - 1) / other
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

data class NodePath<N>(val node: N, val pathLength: Int)

inline fun <N, T>reachableNodes(from: N, neighbours: (N) -> Iterable<N>, reachable: (N) -> Boolean, process: (NodePath<N>) -> T?): T? {
    val list = ArrayDeque<NodePath<N>>()
    val visited = mutableSetOf<N>()
    visited.add(from)
    list.add(NodePath(from, 0))

    while(true) {
        val current = try {
            list.pop()
        } catch (e: NoSuchElementException) {
            return null
        }
        if (current.node != from) {
            val result = process(current)
            if (result != null) {
                return result
            }
        }
        for (neighbour in neighbours(current.node)) {
            if (!visited.contains(neighbour) && reachable(neighbour)) {
                list.add(NodePath(neighbour, current.pathLength + 1))
            }
        }
        visited.add(current.node)
    }
}