package com.behindmedia.adventofcode.year2025.day12

import com.behindmedia.adventofcode.common.*

private data class Piece(val index: Int, val grid: CharGrid) {
    companion object {
        operator fun invoke(string: String): Piece {
            val firstLine = string.lines()[0]
            val index = firstLine.substringBefore(":").toInt()
            val rest = string.substringAfter("\n")
            val grid = CharGrid(rest)
            return Piece(index, grid)
        }
    }

    val filledSize: Int by lazy {
        grid.count { (c, v) -> v == '#' }
    }
}

private data class Area(val width: Int, val height: Int, val counts: Map<Int, Int>) {
    companion object {
        operator fun invoke(string: String): Area {
            val components = string.split(":").map { it.trim() }
            require(components.size == 2)
            val (width, height) = components[0].split("x").map { it.toInt() }
            val counts = components[1].split(" ")
                .map { it.toInt() }
                .withIndex()
                .filter { it.value != 0 }
                .associateBy { it.index }
                .mapValues { it.value.value }
            return Area(width, height, counts)
        }
    }

    fun fits(pieces: List<Piece>): Boolean {
        var sumAreaPieces = 0
        for ((pieceIndex, count) in counts) {
            sumAreaPieces += pieces[pieceIndex].filledSize * count
        }
        return sumAreaPieces <= width * height
    }
}

private fun solve(fileName: String): Int {
    val data = read("/2025/$fileName")
    val parts = data.split("\n\n")
    val pieces = (0 until parts.size - 1).map { Piece(parts[it].trim()) }.toList()
    val areas = parts.last().trim().split("\n").map { Area(it) }
    return areas.count {
        it.fits(pieces)
    }
}

fun main() {
    println(solve("day12.txt"))
}