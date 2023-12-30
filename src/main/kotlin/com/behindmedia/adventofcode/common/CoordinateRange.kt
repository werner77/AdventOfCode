package com.behindmedia.adventofcode.common

/**
 * Range to enumerate coordinates between the (minx, miny) and (maxx, maxy) found in a list of coordinates.
 */
class CoordinateRange(private val minMaxCoordinate: Pair<Coordinate, Coordinate>) : Iterable<Coordinate>,
    ClosedRange<Coordinate> {

    constructor(minCoordinate: Coordinate, width: Int, height: Int) : this(
        Pair(
            minCoordinate,
            minCoordinate + Coordinate(width - 1, height - 1)
        )
    )

    constructor(minCoordinate: Coordinate, maxCoordinate: Coordinate) : this(Pair(minCoordinate, maxCoordinate))
    constructor(collection: Collection<Coordinate>) : this(collection.minMaxCoordinate())

    companion object {
        private fun Collection<Coordinate>.minMaxCoordinate(): Pair<Coordinate, Coordinate> {
            var minX = Integer.MAX_VALUE
            var minY = Integer.MAX_VALUE
            var maxX = Integer.MIN_VALUE
            var maxY = Integer.MIN_VALUE
            for (coordinate in this) {
                minX = kotlin.math.min(minX, coordinate.x)
                minY = kotlin.math.min(minY, coordinate.y)
                maxX = kotlin.math.max(maxX, coordinate.x)
                maxY = kotlin.math.max(maxY, coordinate.y)
            }
            return Pair(Coordinate(minX, minY), Coordinate(maxX, maxY))
        }
    }

    private class CoordinateIterator(
        val minCoordinate: Coordinate,
        val maxCoordinate: Coordinate,
        val reversed: Boolean
    ) :
        Iterator<Coordinate> {
        private var nextCoordinate: Coordinate? =
            if (reversed) {
                if (minCoordinate <= maxCoordinate) maxCoordinate else null
            } else {
                if (minCoordinate <= maxCoordinate) minCoordinate else null
            }

        override fun hasNext(): Boolean {
            return nextCoordinate != null
        }

        override fun next(): Coordinate {
            val next = nextCoordinate
                ?: throw IllegalStateException("Next called on iterator while there are no more elements to iterate over")
            nextCoordinate = if (reversed) {
                when {
                    next.x > minCoordinate.x -> next.offset(-1, 0)
                    next.y > minCoordinate.y -> Coordinate(maxCoordinate.x, next.y - 1)
                    else -> null
                }
            } else {
                when {
                    next.x < maxCoordinate.x -> next.offset(1, 0)
                    next.y < maxCoordinate.y -> Coordinate(minCoordinate.x, next.y + 1)
                    else -> null
                }
            }
            return next
        }
    }

    override fun iterator(): Iterator<Coordinate> = CoordinateIterator(minMaxCoordinate.first, minMaxCoordinate.second, false)

    fun reverseIterator(): Iterator<Coordinate> = CoordinateIterator(minMaxCoordinate.first, minMaxCoordinate.second, true)

    override fun toString(): String {
        return "$start..$endInclusive"
    }

    override val endInclusive: Coordinate
        get() = minMaxCoordinate.second
    override val start: Coordinate
        get() = minMaxCoordinate.first


    val sizeX: Int
        get() = endInclusive.x - start.x + 1

    val sizeY: Int
        get() = endInclusive.y - start.y + 1

    val size: Int
        get() = sizeX * sizeY

    override fun contains(value: Coordinate): Boolean {
        return value.x in start.x..endInclusive.x && value.y in start.y..endInclusive.y
    }

    fun inset(insets: Insets): CoordinateRange {
        return CoordinateRange(
            start + Coordinate(insets.left, insets.top),
            endInclusive - Coordinate(insets.right, insets.bottom)
        )
    }
}