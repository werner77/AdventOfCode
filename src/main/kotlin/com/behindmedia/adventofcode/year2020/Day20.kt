package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.*
import kotlin.math.round
import kotlin.math.sqrt

class Day20 {

    enum class TileSide(val index: Int) {
        Top(0),
        Right(1),
        Bottom(2),
        Left(3)
    }

    // Flip will always happen first, then rotation
    data class TileState(val tile: Tile, val flipped: Boolean, val rotation: Int) {
        fun effectiveSideValue(side: TileSide, mirrored: Boolean): Int {
            return tile.sideValue(side, flipped, rotation, mirrored)
        }

        private fun effectiveCoordinate(coordinate: Coordinate): Coordinate {
            return coordinate.transformed(flipped, rotation, tile.size)
        }

        fun removingBorders() : TileState {
            return TileState(tile.removingBorders(), flipped, rotation)
        }

        val effectiveMap: Map<Coordinate, Char>
            get() {
                return tile.map.mapKeys { entry -> effectiveCoordinate(entry.key) }
            }
    }

    class Tile(val id: Long, val map: Map<Coordinate, Char>) {
        val size = map.maxOf { it.key.x } + 1
        val sides: List<Int>
        val flipSides: List<Int>
        val allSides: List<Int>
            get() = sides + flipSides

        init {
            sides = mutableListOf<Int>().also {
                var start = calculateSide(Coordinate.origin, it) { Coordinate.right } // Top
                start = calculateSide(start, it) { Coordinate.down } // Right
                start = calculateSide(start, it) { Coordinate.left } // Bottom
                calculateSide(start, it) { Coordinate.up } // Left
            }
            flipSides = mutableListOf<Int>().apply {
                add(invert(sides[TileSide.Top.index])) // Top
                add(invert(sides[TileSide.Left.index])) // Left becomes right
                add(invert(sides[TileSide.Bottom.index])) // Bottom
                add(invert(sides[TileSide.Right.index])) // Right becomes left
            }
        }

        fun removingBorders() : Tile {
            val newMap = map.filter { it.key.x != 0 && it.key.x != size - 1 && it.key.y != 0 && it.key.y != size - 1 }.mapKeys {
                it.key.offset(-1, -1)
            }
            val t = Tile(id, newMap)
            return t
        }

        fun findFirstState(predicate: (TileState) -> Boolean): TileState? {
            for (flipped in listOf(false, true)) {
                for (rotation in 0 until 4) {
                    val state = TileState(this, flipped, rotation)
                    if (predicate(state)) {
                        return state
                    }
                }
            }
            return null
        }

        fun sideValue(side: TileSide, flipped: Boolean, rotation: Int, mirrored: Boolean): Int {
            val sides = if (flipped) {
                flipSides
            } else {
                sides
            }
            var s = sides[(rotation + side.index) % 4]
            if (mirrored) {
                s = invert(s)
            }
            return s
        }

        private fun invert(side: Int): Int {
            var s = 0
            for (i in 0 until size) {
                val mask = 1 shl i
                val invertedMask = 1 shl (size - 1 - i)
                if ((side and mask) == mask) {
                    s = s or invertedMask
                }
            }
            return s
        }

        private fun calculateSide(start: Coordinate, sides: MutableList<Int>, increment: () -> Coordinate): Coordinate {
            var n = 0
            var current = start
            for (i in 0 until size) {
                if (i != 0) current += increment()
                if (map[current] == '#') {
                    n = n or (1 shl i)
                }
            }
            sides.add(n)
            return current
        }

        override fun equals(other: Any?): Boolean {
            return if (other is Tile) id == other.id else false
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }

        override fun toString(): String {
            return "Tile(id=$id)"
        }
    }

    fun part1(input: String): Long {
        val tileMap = parseTileMap(input)
        val puzzleSize = round(sqrt(tileMap.size.toDouble())).toInt()
        val positionedTiles = positionTiles(puzzleSize, tileMap)

        // Check the four corners
        return positionedTiles.entries.filter {
            (it.key.x == 0 || it.key.x == puzzleSize - 1) && (it.key.y == 0 || it.key.y == puzzleSize - 1)
        }.map { it.value.tile.id }.reduce { acc, l -> acc * l }
    }

    fun part2(input: String, seaMonster: String): Long {
        val tileMap = parseTileMap(input)
        val seaMonsterMap = parseMap(seaMonster) { if (it == '#') it else null }

        seaMonsterMap.printMap(" ")

        val seaMonsterWidth = seaMonsterMap.maxOf { it.key.x } + 1
        val seaMonsterHeight = seaMonsterMap.maxOf { it.key.y } + 1

        val tileSize = tileMap.values.first().size
        val puzzleSize = round(sqrt(tileMap.size.toDouble())).toInt()
        val map = positionTiles(puzzleSize, tileMap)

        // For each tile remove the border
        val mapWithoutBorders = map.mapValues { entry ->
            entry.value.removingBorders()
        }.merge()

        mapWithoutBorders.printMap(".")

        val totalMapSize = puzzleSize * tileSize

        val totalCount = mapWithoutBorders.count { it.value == '#' }

        for (flipped in listOf(false, true)) {
            for (rotation in 0 until 4) {
                val flippedRotatedMap = mapWithoutBorders.transformed(flipped, rotation)
                var matchCount = 0L
                for (x in 0 until totalMapSize - seaMonsterWidth + 1) {
                    for (y in 0 until totalMapSize - seaMonsterHeight + 1) {
                        var found = true
                        for (seaMonsterCoordinate in seaMonsterMap.keys) {
                            val c = seaMonsterCoordinate.offset(x, y)
                            if (flippedRotatedMap[c] != '#') {
                                found = false
                                break
                            }
                        }
                        if (found) {
                            matchCount++
                        }
                    }
                }
                if (matchCount > 0) return totalCount - matchCount * seaMonsterMap.size
            }
        }
        error("No matches found")
    }

    private fun parseTileMap(input: String): Map<Long, Tile> {
        var y = 0
        var tile: MutableMap<Coordinate, Char>? = null
        val tileMap = mutableMapOf<Long, Tile>()
        var tileId: Long? = null
        parseLines(input) {
            if (it.isBlank()) {
                tileMap[tileId!!] = Tile(tileId!!, tile!!)
                tileId = null
                tile = null
            } else if (it.startsWith("Tile")) {
                // Tile identifier
                tileId = it.split(" ")[1].trim(':', ' ').toLong()
                tile = mutableMapOf()
                y = -1
            } else {
                var x = 0
                y++
                for (c in it) {
                    tile!![Coordinate(x, y)] = c
                    x++
                }
            }
        }
        tileMap[tileId!!] = Tile(tileId!!, tile!!)
        return tileMap
    }

    // Output should be which tile is at each position in the grid and its state
    private fun positionTiles(puzzleSize: Int, tileMap: Map<Long, Tile>): Map<Coordinate, TileState> {

        // A map where the key is the side and the value is a set of tiles containing that side
        val tileLookupMap = tileMap.values.fold(mutableMapOf<Int, MutableSet<Tile>>()) { map, tile ->
            map.apply {
                tile.allSides.forEach { side ->
                    getOrPut(side) { mutableSetOf() }.add(tile)
                }
            }
        }

        // Find the top left corner tile
        val cornerTile = tileMap.values.mapNotNull { tile ->
            tile.findFirstState { state ->
                tileLookupMap[state.effectiveSideValue(TileSide.Top, true)]?.size == 1 &&
                        tileLookupMap[state.effectiveSideValue(TileSide.Left, true)]?.size == 1
            }
        }.firstOrNull() ?: error("Could not find top left corner tile")

        val positionedTiles = mutableMapOf<Coordinate, TileState>()
        val unpositionedTiles = tileMap.values.toMutableSet()

        // The rotation should be such that sides 0 and 3 are the ones having no neighbours
        positionedTiles[Coordinate.origin] = cornerTile
        unpositionedTiles.remove(cornerTile.tile)

        for (coordinate in CoordinateRange(Coordinate.origin, puzzleSize, puzzleSize)) {
            // Try to match every subsequent tile
            if (coordinate != Coordinate.origin) {
                // Should match the tile to the left and above
                val leftSideTile = positionedTiles[coordinate + Coordinate.left]
                val topSideTile = positionedTiles[coordinate + Coordinate.up]
                val candidates = unpositionedTiles.mapNotNull { tile ->
                    tile.findFirstState { state ->
                        val topSide = state.effectiveSideValue(TileSide.Top, true)
                        val leftSide = state.effectiveSideValue(TileSide.Left, true)

                        val topMatches =
                            (topSideTile == null && tileLookupMap[topSide]?.size == 1) ||
                                    (topSideTile != null && topSideTile.effectiveSideValue(TileSide.Bottom, false) == topSide)

                        val leftMatches =
                            (leftSideTile == null && tileLookupMap[leftSide]?.size == 1) ||
                                    (leftSideTile != null && leftSideTile.effectiveSideValue(TileSide.Right, false) == leftSide)

                        topMatches && leftMatches
                    }
                }
                if (candidates.size != 1) error("Found not exactly one candidate")
                val state = candidates[0]
                positionedTiles[coordinate] = state
                unpositionedTiles.remove(state.tile)
            }
        }
        return positionedTiles
    }

    private fun Map<Coordinate, TileState>.merge() : Map<Coordinate, Char> {
        val result = mutableMapOf<Coordinate, Char>()
        val tileSize = this.values.first().tile.size
        for (coordinate in CoordinateRange(this.keys)) {
            val tileMap = this[coordinate]!!.effectiveMap
            val origin = Coordinate(coordinate.x * tileSize, coordinate.y * tileSize)
            for (c in CoordinateRange(tileMap.keys)) {
                val c1 = origin + c
                result[c1] = tileMap[c]!!
            }
        }
        return result
    }

    private fun Map<Coordinate, Char>.transformed(flipped: Boolean, rotation: Int): Map<Coordinate, Char> {
        val mapSize = this.maxOf { it.key.x } + 1
        return mapKeys { it.key.transformed(flipped, rotation, mapSize) }
    }
}

private fun Coordinate.transformed(flipped: Boolean, rotation: Int, squareSize: Int): Coordinate {
    val max = squareSize - 1
    var x = this.x
    var y = this.y
    if (flipped) {
        x = max - x
    }
    repeat(rotation % 4) {
        val a = x
        x = y
        y = max - a
    }
    return Coordinate(x, y)
}