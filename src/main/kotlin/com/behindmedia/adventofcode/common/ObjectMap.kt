package com.behindmedia.adventofcode.common

import java.util.NoSuchElementException

interface ValueMap<T: Any>: Iterable<Map.Entry<Coordinate, T>> {
    companion object {
        fun <T: Any>toString(valueMap: ValueMap<T>): String {
            val builder = StringBuilder()
            for (y in 0 until valueMap.sizeY) {
                for (x in 0 until valueMap.sizeX) {
                    builder.append(valueMap[Coordinate(x, y)])
                }
                builder.append("\n")
            }
            return builder.toString()
        }
    }

    val sizeX: Int
    val sizeY: Int

    val size: Int
        get() = sizeX * sizeY

    fun isExtreme(coordinate: Coordinate): Boolean {
        return when (coordinate) {
            Coordinate.origin, Coordinate(0, sizeY - 1), Coordinate(sizeX - 1, 0), Coordinate(sizeX - 1, sizeY - 1) -> true
            else -> false
        }
    }

    operator fun get(coordinate: Coordinate): T = getOrNull(coordinate) ?: throw NoSuchElementException("No value for coordinate: $coordinate")

    fun getOrNull(coordinate: Coordinate): T?

    override fun iterator(): Iterator<Map.Entry<Coordinate, T>> {
        return object : Iterator<Map.Entry<Coordinate, T>> {
            private var x = 0
            private var y = 0

            override fun hasNext(): Boolean {
                return x < sizeX && y < sizeY
            }

            override fun next(): Map.Entry<Coordinate, T> {
                if (!hasNext()) throw NoSuchElementException()
                val c = Coordinate(x, y)
                return Entry(Coordinate(x, y), get(c)).also {
                    if (x >= sizeX - 1) {
                        x = 0
                        y++
                    } else {
                        x++
                    }
                }
            }
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

interface MutableValueMap<T: Any> : ValueMap<T> {
    operator fun set(coordinate: Coordinate, value: T)
}

class ObjectMap<T: Any>(override val sizeX: Int, override val sizeY: Int, default: (Int, Int) -> T) : MutableValueMap<T> {

    constructor(squareSize: Int, default: (Int, Int) -> T) : this(
        squareSize,
        squareSize,
        default
    )

    private val data: List<MutableList<T>> = List(sizeY) { y ->
        MutableList(sizeX) { x ->
            default(x, y)
        }
    }

    companion object {
        operator fun <T: Any> invoke(string: String, converter: (Char) -> T): ObjectMap<T> {
            val lines = string.trim().split("\n")
            val sizeY = lines.size
            val sizeX = lines.getOrNull(0)?.length ?: 0
            return ObjectMap(sizeX, sizeY) { x, y ->
                converter.invoke(lines[y].getOrNull(x) ?: error("Invalid coordinate: $x, $y"))
            }
        }
    }

    override operator fun get(coordinate: Coordinate): T {
        return data[coordinate.y][coordinate.x]
    }

    override operator fun set(coordinate: Coordinate, value: T) {
        data[coordinate.y][coordinate.x] = value
    }

    override fun getOrNull(coordinate: Coordinate): T? {
        return data.getOrNull(coordinate.y)?.getOrNull(coordinate.x)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherMap = other as? ObjectMap<*> ?: return false
        return data == otherMap.data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String = ValueMap.toString(this)
}

