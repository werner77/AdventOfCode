package com.behindmedia.adventofcode.common

class CharMap(override val sizeX: Int, override val sizeY: Int, default: (Int, Int) -> Char = { _, _ -> defaultChar }) : MutableValueMap<Char> {
    constructor(squareSize: Int, default: (Int, Int) -> Char = { _, _ -> defaultChar }) : this(
        squareSize,
        squareSize,
        default
    )

    companion object {
        private const val defaultChar = ' '

        operator fun invoke(string: String, emptyChar: Char = defaultChar): CharMap {
            val lines = string.trim().split("\n")
            val sizeY = lines.size
            val sizeX = lines.getOrNull(0)?.length ?: 0
            return CharMap(sizeX, sizeY) { x, y ->
                lines[y].getOrNull(x) ?: emptyChar
            }
        }
    }

    private val data = Array(sizeY) { y ->
        CharArray(sizeX) { x ->
            default.invoke(x, y)
        }
    }

    override operator fun get(coordinate: Coordinate): Char {
        return data[coordinate.y][coordinate.x]
    }

    override operator fun set(coordinate: Coordinate, value: Char) {
        data[coordinate.y][coordinate.x] = value
    }

    override fun getOrNull(coordinate: Coordinate): Char? {
        return data.getOrNull(coordinate.y)?.getOrNull(coordinate.x)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherMap = other as? CharMap ?: return false
        return data.contentDeepEquals(otherMap.data)
    }

    override fun hashCode(): Int {
        return data.contentDeepHashCode()
    }

    override fun toString(): String = ValueMap.toString(this)
}