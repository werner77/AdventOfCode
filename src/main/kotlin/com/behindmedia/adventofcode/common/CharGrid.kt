package com.behindmedia.adventofcode.common

open class CharGrid(final override val sizeX: Int, final override val sizeY: Int, default: (Int, Int) -> Char = { _, _ -> defaultChar }) : ValueGrid<Char> {
    constructor(squareSize: Int, default: (Int, Int) -> Char = { _, _ -> defaultChar }) : this(
        squareSize,
        squareSize,
        default
    )

    companion object {
        const val defaultChar = ' '

        operator fun invoke(string: String, emptyChar: Char = defaultChar): CharGrid {
            val lines = string.trim('\n').split("\n")
            val sizeY = lines.size
            val sizeX = lines.getOrNull(0)?.length ?: 0
            return CharGrid(sizeX, sizeY) { x, y ->
                lines[y].getOrNull(x) ?: emptyChar
            }
        }
    }

    protected val data = Array(sizeY) { y ->
        Array<Char>(sizeX) { x ->
            default.invoke(x, y)
        }
    }

    override operator fun get(coordinate: Coordinate): Char {
        return data[coordinate.y][coordinate.x]
    }

    override fun getOrNull(coordinate: Coordinate): Char? {
        return data.getOrNull(coordinate.y)?.getOrNull(coordinate.x)
    }

    override fun copy(): CharGrid {
        return CharGrid(this.sizeX, this.sizeY) { x, y ->
            this.data[y][x]
        }
    }

    override fun mutableCopy(): MutableCharGrid {
        return MutableCharGrid(this.sizeX, this.sizeY) { x, y ->
            this.data[y][x]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherMap = other as? CharGrid ?: return false
        return data.contentDeepEquals(otherMap.data)
    }

    override fun hashCode(): Int {
        return data.contentDeepHashCode()
    }

    override fun toString(): String = ValueGrid.toString(this)
}

class MutableCharGrid(sizeX: Int, sizeY: Int, default: (Int, Int) -> Char = { _, _ -> defaultChar }): CharGrid(sizeX, sizeY, default), MutableValueGrid<Char> {

    constructor(squareSize: Int, default: (Int, Int) -> Char = { _, _ -> defaultChar }) : this(
        squareSize,
        squareSize,
        default
    )

    override operator fun set(coordinate: Coordinate, value: Char) {
        data[coordinate.y][coordinate.x] = value
    }
}