package com.behindmedia.adventofcode.common

import java.util.NoSuchElementException

interface ValueGrid<T: Any>: Iterable<Map.Entry<Coordinate, T>> {
    companion object {
        fun <T: Any>toString(valueGrid: ValueGrid<T>): String {
            val builder = StringBuilder()
            for (y in 0 until valueGrid.sizeY) {
                for (x in 0 until valueGrid.sizeX) {
                    builder.append(valueGrid[Coordinate(x, y)])
                }
                builder.append("\n")
            }
            return builder.toString()
        }
    }

    val sizeX: Int
    val sizeY: Int

    val minX: Int
        get() = if (sizeX > 0) 0 else error("Size x is 0")
    val minY: Int
        get() = if (sizeY > 0) 0 else error("Size y is 0")
    val maxX: Int
        get() = if (sizeX > 0) sizeX - 1 else error("Size x is 0")
    val maxY: Int
        get() = if (sizeY > 0) sizeY - 1 else error("Size y is 0")

    val size: Int
        get() = sizeX * sizeY
    val coordinateRange: CoordinateRange
        get() = CoordinateRange(Coordinate.origin, sizeX, sizeY)
    val keys: List<Coordinate>
        get() = coordinateRange.toList()
    val values: List<T>
        get() = coordinateRange.map { get(it) }

    fun copy(): ValueGrid<T>

    fun mutableCopy(): MutableValueGrid<T>

    fun containsKey(coordinate: Coordinate): Boolean = getOrNull(coordinate) != null

    fun isExtreme(coordinate: Coordinate): Boolean {
        return when (coordinate) {
            Coordinate.origin, Coordinate(0, sizeY - 1), Coordinate(sizeX - 1, 0), Coordinate(sizeX - 1, sizeY - 1) -> true
            else -> false
        }
    }

    operator fun get(coordinate: Coordinate): T = getOrNull(coordinate) ?: throw NoSuchElementException("No value for coordinate: $coordinate")

    fun getOrNull(coordinate: Coordinate): T?

    override fun iterator(): Iterator<Map.Entry<Coordinate, T>> {
        return this.coordinateRange.iterator().mapped { coordinate ->
            Entry(coordinate, this[coordinate])
        }
    }

    private data class Entry<T>(override val key: Coordinate, override val value: T) : Map.Entry<Coordinate, T>

    fun toMap(): Map<Coordinate, T> {
        val map = HashMap<Coordinate, T>(size, 1.0f)
        for (x in 0 until sizeX) {
            for (y in 0 until sizeY) {
                val c = Coordinate(x, y)
                map[c] = get(c)
            }
        }
        return map
    }
}

interface MutableValueGrid<T: Any> : ValueGrid<T> {
    operator fun set(coordinate: Coordinate, value: T)
}

open class ObjectGrid<T: Any>(final override val sizeX: Int, final override val sizeY: Int, default: (Int, Int) -> T) : ValueGrid<T> {

    constructor(squareSize: Int, default: (Int, Int) -> T) : this(
        squareSize,
        squareSize,
        default
    )

    protected val data: List<MutableList<T>> = List(sizeY) { y ->
        MutableList(sizeX) { x ->
            default(x, y)
        }
    }

    companion object {
        operator fun <T: Any> invoke(string: String, converter: (Char) -> T): ObjectGrid<T> {
            val lines = string.trim().split("\n")
            val sizeY = lines.size
            val sizeX = lines.getOrNull(0)?.length ?: 0
            return ObjectGrid(sizeX, sizeY) { x, y ->
                converter.invoke(lines[y].getOrNull(x) ?: error("Invalid coordinate: $x, $y"))
            }
        }
    }

    override operator fun get(coordinate: Coordinate): T {
        return data[coordinate.y][coordinate.x]
    }

    override fun getOrNull(coordinate: Coordinate): T? {
        return data.getOrNull(coordinate.y)?.getOrNull(coordinate.x)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherMap = other as? ObjectGrid<*> ?: return false
        return data == otherMap.data
    }

    override fun copy(): ObjectGrid<T> {
        return ObjectGrid(this.sizeX, this.sizeY) { x, y ->
            this.data[y][x]
        }
    }

    override fun mutableCopy(): MutableObjectGrid<T> {
        return MutableObjectGrid(this.sizeX, this.sizeY) { x, y ->
            this.data[y][x]
        }
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String = ValueGrid.toString(this)
}

class MutableObjectGrid<T: Any>(sizeX: Int, sizeY: Int, default: (Int, Int) -> T): ObjectGrid<T>(sizeX, sizeY, default), MutableValueGrid<T> {
    constructor(squareSize: Int, default: (Int, Int) -> T) : this(
        squareSize,
        squareSize,
        default
    )
    override operator fun set(coordinate: Coordinate, value: T) {
        data[coordinate.y][coordinate.x] = value
    }
}

