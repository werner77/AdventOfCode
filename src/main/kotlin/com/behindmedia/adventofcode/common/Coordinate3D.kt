package com.behindmedia.adventofcode.common

import kotlin.math.abs

/**
 * Describes a three-dimensional coordinate or vector
 */
data class Coordinate3D(val x: Long, val y: Long, val z: Long) : Comparable<Coordinate3D> {

    constructor(components: List<Long>) : this(components[0], components[1], components[2])

    companion object {
        val origin = Coordinate3D(0, 0, 0)
        val north = Coordinate3D(1, 0, -1)
        val northEast = Coordinate3D(1, -1, 0)
        val southEast = Coordinate3D(0, -1, 1)
        val south = Coordinate3D(-1, 0, 1)
        val southWest = Coordinate3D(-1, 1, 0)
        val northWest = Coordinate3D(0, 1, -1)
        val components = listOf(0, 1, 2)

        val allDirections: List<Coordinate3D>

        init {
            val values = mutableListOf<Coordinate3D>()
            for (x in listOf(-1L, 1L)) {
                values += Coordinate3D(x, 0, 0)
            }
            for (y in listOf(-1L, 1L)) {
                values += Coordinate3D(0, y, 0)
            }
            for (z in listOf(-1L, 1L)) {
                values += Coordinate3D(0, 0, z)
            }
            allDirections = values
        }
    }

    fun offset(vector: Coordinate3D): Coordinate3D {
        return Coordinate3D(x + vector.x, y + vector.y, z + vector.z)
    }

    fun offset(xOffset: Long, yOffset: Long, zOffset: Long): Coordinate3D {
        return Coordinate3D(x + xOffset, y + yOffset, z + zOffset)
    }

    fun manhattenDistance(other: Coordinate3D): Long {
        return abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
    }

    operator fun plus(other: Coordinate3D): Coordinate3D {
        return offset(other)
    }

    operator fun unaryMinus(): Coordinate3D {
        return Coordinate3D(-x, -y, -z)
    }

    operator fun minus(other: Coordinate3D): Coordinate3D {
        return offset(-other.x, -other.y, -other.z)
    }

    operator fun get(index: Int): Long {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IllegalArgumentException("Invalid index supplied")
        }
    }

    override fun compareTo(other: Coordinate3D): Int {
        return compare(
            { this.z.compareTo(other.z) },
            { this.y.compareTo(other.y) },
            { this.x.compareTo(other.x) }
        )
    }

    override fun toString(): String {
        return "($x,$y,$z)"
    }
}

val Collection<Coordinate3D>.minX: Long
    get() = this.minOf { it.x }

val Collection<Coordinate3D>.minY: Long
    get() = this.minOf { it.y }

val Collection<Coordinate3D>.minZ: Long
    get() = this.minOf { it.z }

val Collection<Coordinate3D>.maxX: Long
    get() = this.maxOf { it.x }

val Collection<Coordinate3D>.maxY: Long
    get() = this.maxOf { it.y }

val Collection<Coordinate3D>.maxZ: Long
    get() = this.maxOf { it.z }

val Collection<Coordinate3D>.sizeX: Long
    get() = maxX + 1

val Collection<Coordinate3D>.sizeY: Long
    get() = maxY + 1

val Collection<Coordinate3D>.sizeZ: Long
    get() = maxZ + 1

val Collection<Coordinate3D>.minCoordinate: Coordinate3D
    get() = Coordinate3D(minX, minY, minZ)

val Collection<Coordinate3D>.maxCoordinate: Coordinate3D
    get() = Coordinate3D(maxX, maxY, maxZ)

fun Collection<Coordinate3D>.minMaxCoordinate(): Pair<Coordinate3D, Coordinate3D> {
    return Pair(minCoordinate, maxCoordinate)
}

fun Collection<Coordinate3D>.range(): CoordinateRange3D = CoordinateRange3D(this)

/**
 * Range to enumerate coordinates between the (minx, miny) and (maxx, maxy) found in a list of coordinates.
 */
class CoordinateRange3D(private val minMaxCoordinate: Pair<Coordinate3D, Coordinate3D>) : Iterable<Coordinate3D>,
    ClosedRange<Coordinate3D> {

    constructor(minCoordinate: Coordinate3D, xSize: Long, ySize: Long, zSize: Long) : this(
        Pair(
            minCoordinate,
            minCoordinate + Coordinate3D(xSize - 1, ySize - 1, zSize - 1)
        )
    )

    constructor(minCoordinate: Coordinate3D, maxCoordinate: Coordinate3D) : this(Pair(minCoordinate, maxCoordinate))
    constructor(collection: Collection<Coordinate3D>) : this(collection.minMaxCoordinate())

    companion object {
        private fun Collection<Coordinate3D>.minMaxCoordinate(): Pair<Coordinate3D, Coordinate3D> {
            var minX = Long.MAX_VALUE
            var minY = Long.MAX_VALUE
            var minZ = Long.MAX_VALUE
            var maxX = Long.MIN_VALUE
            var maxY = Long.MIN_VALUE
            var maxZ = Long.MIN_VALUE
            for (coordinate in this) {
                minX = kotlin.math.min(minX, coordinate.x)
                minY = kotlin.math.min(minY, coordinate.y)
                minZ = kotlin.math.min(minZ, coordinate.z)

                maxX = kotlin.math.max(maxX, coordinate.x)
                maxY = kotlin.math.max(maxY, coordinate.y)
                maxZ = kotlin.math.max(maxZ, coordinate.z)
            }
            return Pair(Coordinate3D(minX, minY, minZ), Coordinate3D(maxX, maxY, maxZ))
        }
    }

    private class CoordinateIterator(val minCoordinate: Coordinate3D, val maxCoordinate: Coordinate3D) :
        Iterator<Coordinate3D> {
        private var nextCoordinate: Coordinate3D? = if (minCoordinate <= maxCoordinate) minCoordinate else null

        override fun hasNext(): Boolean {
            return nextCoordinate != null
        }

        override fun next(): Coordinate3D {
            val next = nextCoordinate
                ?: throw IllegalStateException("Next called on iterator while there are no more elements to iterate over")
            nextCoordinate = when {
                next.x < maxCoordinate.x -> Coordinate3D(next.x + 1, next.y, next.z)
                next.y < maxCoordinate.y -> Coordinate3D(minCoordinate.x, next.y + 1, next.z)
                next.z < maxCoordinate.z -> Coordinate3D(minCoordinate.x, minCoordinate.y, next.z + 1)
                else -> null
            }
            return next
        }
    }

    override fun iterator(): Iterator<Coordinate3D> = CoordinateIterator(minMaxCoordinate.first, minMaxCoordinate.second)

    override fun toString(): String {
        return "$start..$endInclusive"
    }

    override val endInclusive: Coordinate3D
        get() = minMaxCoordinate.second
    override val start: Coordinate3D
        get() = minMaxCoordinate.first

}