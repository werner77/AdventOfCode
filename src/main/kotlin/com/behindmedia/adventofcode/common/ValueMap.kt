package com.behindmedia.adventofcode.common

import java.util.NoSuchElementException

class ValueMap<T>(val sizeX: Int, val sizeY: Int, default: (Int, Int) -> T) : Iterable<Map.Entry<Coordinate, T>> {

    constructor(squareSize: Int, default: (Int, Int) -> T) : this(
        squareSize,
        squareSize,
        default
    )

    val size: Int
        get() = sizeX * sizeY

    private val data: List<MutableList<T>> = List(sizeY) { y ->
        MutableList(sizeX) { x ->
            default(x, y)
        }
    }

    companion object {
        operator fun <T> invoke(string: String, converter: (Char) -> T): ValueMap<T> {
            val lines = string.split("\n")
            val sizeY = lines.size
            val sizeX = lines.getOrNull(0)?.length ?: 0
            return ValueMap(sizeX, sizeY) { x, y ->
                converter.invoke(lines[y].getOrNull(x) ?: error("Invalid coordinate: $x, $y"))
            }
        }
    }

    override fun iterator(): Iterator<Map.Entry<Coordinate, T>> {
        return object : Iterator<Map.Entry<Coordinate, T>> {
            private var x = 0
            private var y = 0

            override fun hasNext(): Boolean {
                return x < sizeX && y < sizeY
            }

            override fun next(): Map.Entry<Coordinate, T> {
                if (!hasNext()) throw NoSuchElementException()
                return Entry(Coordinate(x, y), data[y][x]).also {
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

    operator fun get(coordinate: Coordinate): T {
        return data[coordinate.y][coordinate.x]
    }

    operator fun set(coordinate: Coordinate, value: T) {
        data[coordinate.y][coordinate.x] = value
    }

    fun getOrNull(coordinate: Coordinate): T? {
        return data.getOrNull(coordinate.y)?.getOrNull(coordinate.x)
    }

    fun toMap(): Map<Coordinate, T> {
        val map = HashMap<Coordinate, T>(size, 1.0f)
        for (x in 0 until sizeX) {
            for (y in 0 until sizeY) {
                map[Coordinate(x, y)] = data[y][x]
            }
        }
        return map
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherMap = other as? ValueMap<*> ?: return false
        return data == otherMap.data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (line in data) {
            for (c in line) {
                builder.append(c)
            }
            builder.append("\n")
        }
        return builder.toString()
    }
}

