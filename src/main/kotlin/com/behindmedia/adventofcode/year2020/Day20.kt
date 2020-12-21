package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.*
import kotlin.math.round
import kotlin.math.sqrt

class Day20 {

    /**
     * The 4 sides of a tile
     */
    enum class TileSide(val index: Int) {
        Top(0),
        Right(1),
        Bottom(2),
        Left(3)
    }

    /**
     * State of a tile containing the tile itself, its rotation (0-3) and whether its flipped or not.
     *
     * Definitions:
     *
     * - Flip happens over the vertical axis.
     * - Rotation is counter clockwise.
     * - Flip happens first, then rotation.
     *
     * This yields 8 states per tile.
     */
    data class TileState(val tile: Tile, val flipped: Boolean, val rotation: Int) {
        fun effectiveSideValue(side: TileSide, mirrored: Boolean = false): Int {
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

    /**
     * Tile containing its ID and the map of characters it represents.
     *
     * The sides of the tile are encoded as Ints where a bit of 1 designates '#' and a bit of 0 designates '.'
     */
    class Tile(val id: Long, val map: Map<Coordinate, Char>) {
        val max = map.maxOf { it.key.x }
        val size
            get() = max + 1
        private val sides: IntArray
        private val flipSides: IntArray

        /**
         * All the possible side values (for both flipped and normal orientation)
         */
        val sideValues: List<Int>
            get() = sides.toList() + flipSides.toList()

        companion object {
            /**
             * Translation between the side indexes of the normal side vs the flip side.
             *
             * Flip happens along the vertical axis so top remains top and bottom remains bottom.
             * Left and right are interchanged
             */
            val flipSideIndexes = IntArray(4) {
                when (it) {
                    TileSide.Top.index -> TileSide.Top.index
                    TileSide.Right.index -> TileSide.Left.index
                    TileSide.Bottom.index -> TileSide.Bottom.index
                    TileSide.Left.index -> TileSide.Right.index
                    else -> error("Unexpected index")
                }
            }
        }

        init {
            // Calculate the sides in clockwise direction
            sides = IntArray(4) {
                when (it) {
                    TileSide.Top.index -> calculateSide(Coordinate.origin) { Coordinate.right }
                    TileSide.Right.index -> calculateSide(Coordinate.right * max) { Coordinate.down }
                    TileSide.Bottom.index -> calculateSide((Coordinate.right + Coordinate.down) * max) { Coordinate.left }
                    TileSide.Left.index -> calculateSide(Coordinate.down * max) { Coordinate.up }
                    else -> error("Unexpected index: $it")
                }
            }
            // Flip sides are the mirror of the forward sides
            flipSides = IntArray(4) {
                mirror(sides[flipSideIndexes[it]])
            }
        }

        /**
         * Returns a new tile by removing the outermost border of 1
         */
        fun removingBorders() : Tile {
            val newMap = map.filter { it.key.x != 0 && it.key.x != max && it.key.y != 0 && it.key.y != max }
                .mapKeys { it.key.offset(-1, -1) }
            return Tile(id, newMap)
        }

        /**
         * Returns the first state for which the specified predicate returns true
         */
        fun firstState(predicate: (TileState) -> Boolean): TileState? {
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

        /**
         * The side value corresponding with the specified state.
         *
         * Mirrored is used to match a tile next to it (its side value needs to be the mirror of this tile's value)
         */
        fun sideValue(side: TileSide, flipped: Boolean, rotation: Int, mirrored: Boolean): Int {
            val sides = if (flipped xor mirrored) flipSides else sides
            val index = (rotation + side.index) % 4
            return if (mirrored) sides[flipSideIndexes[index]] else sides[index]
        }

        /**
         * Mirrors a side value (bits of Integer are in the reverse order)
         */
        private fun mirror(side: Int): Int {
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

        /**
         * Calculate the integer describing this side where each '#' is a binary 1 and each '.' is a binary 0
         */
        private fun calculateSide(start: Coordinate, increment: () -> Coordinate): Int {
            var n = 0
            var current = start
            for (i in 0 until size) {
                if (i != 0) current += increment()
                if (map[current] == '#') {
                    n = n or (1 shl i)
                }
            }
            return n
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

        // The width/height of the entire puzzle width==height since it is a square
        val puzzleSize = round(sqrt(tileMap.size.toDouble())).toInt()

        assert(puzzleSize * puzzleSize == tileMap.size)

        // Position all the tiles (solve the puzzle)
        // This returns a map where the key is the coordinate of the tile within the puzzle and the value is the TileState
        val positionedTiles = positionTiles(puzzleSize, tileMap)

        // Check the four corners of the puzzle
        return positionedTiles.entries.filter {
            (it.key.x == 0 || it.key.x == puzzleSize - 1) && (it.key.y == 0 || it.key.y == puzzleSize - 1)
        }.map { it.value.tile.id }.reduce { acc, l -> acc * l }
    }

    fun part2(input: String, seaMonster: String): Long {
        val tileMap = parseTileMap(input)
        val seaMonsterMap = parseMap(seaMonster) { if (it == '#') it else null }

        val seaMonsterWidth = seaMonsterMap.maxOf { it.key.x } + 1
        val seaMonsterHeight = seaMonsterMap.maxOf { it.key.y } + 1

        val tileSize = tileMap.values.first().size
        val puzzleSize = round(sqrt(tileMap.size.toDouble())).toInt()
        val map = positionTiles(puzzleSize, tileMap)

        // For each tile remove the border
        val mapWithoutBorders = map.mapValues { entry ->
            entry.value.removingBorders()
        }.merge()

        val totalMapSize = puzzleSize * tileSize
        val totalCount = mapWithoutBorders.count { it.value == '#' }

        for (flipped in listOf(false, true)) {
            for (rotation in 0 until 4) {
                val flippedRotatedMap = mapWithoutBorders.transformed(flipped, rotation)
                var matchCount = 0L
                for (x in 0 until totalMapSize - seaMonsterWidth + 1) {
                    for (y in 0 until totalMapSize - seaMonsterHeight + 1) {
                        val seaMonsterFound = !seaMonsterMap.keys.any {
                            flippedRotatedMap[it.offset(x, y)] != '#'
                        }
                        if (seaMonsterFound) {
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

        // A map where the key is the side (Integer) and the value is a set of tiles containing that side
        val tileLookupMap = tileMap.values.fold(mutableMapOf<Int, MutableSet<Tile>>()) { map, tile ->
            map.apply {
                tile.sideValues.forEach { side ->
                    getOrPut(side) { mutableSetOf() }.add(tile)
                }
            }
        }

        // Find the top left corner tile
        val topLeftCornerTile = tileMap.values.mapNotNull { tile ->
            tile.firstState { state ->
                tileLookupMap[state.effectiveSideValue(TileSide.Top, true)]?.size == 1 &&
                        tileLookupMap[state.effectiveSideValue(TileSide.Left, true)]?.size == 1
            }
        }.firstOrNull() ?: error("Could not find a top left corner tile")

        val positionedTiles = mutableMapOf<Coordinate, TileState>()
        val unpositionedTiles = tileMap.values.toMutableSet()

        for (coordinate in CoordinateRange(Coordinate.origin, puzzleSize, puzzleSize)) {
            // Try to match every subsequent tile, skip the first
            val positionedTile = if (positionedTiles.isEmpty()) {
                topLeftCornerTile
            } else {
                // Should match the tile to the left and above
                val leftSideTileValue = positionedTiles[coordinate + Coordinate.left]?.effectiveSideValue(TileSide.Right, true)
                val topSideTileValue = positionedTiles[coordinate + Coordinate.up]?.effectiveSideValue(TileSide.Bottom, true)
                val candidateLeftTiles = leftSideTileValue?.let { tileLookupMap[it] } ?: emptySet()
                val candidateTopTiles = topSideTileValue?.let { tileLookupMap[it] } ?: emptySet()

                // Only take the tiles into account which have a matching side value
                val candidates = (candidateLeftTiles + candidateTopTiles)

                candidates.mapNotNull { tile ->
                    // Only consider unpositioned tiles
                    if (unpositionedTiles.contains(tile)) {
                        tile.firstState { state ->
                            val topSide = state.effectiveSideValue(TileSide.Top)
                            val leftSide = state.effectiveSideValue(TileSide.Left)

                            val topMatches = (topSideTileValue == null && tileLookupMap[topSide]?.size == 1) || topSide == topSideTileValue
                            val leftMatches = (leftSideTileValue == null && tileLookupMap[leftSide]?.size == 1) || leftSide == leftSideTileValue

                            topMatches && leftMatches
                        }
                    } else {
                        null
                    }
                }.only() // There is only one match for every tile as deducted from the counts which makes the solution easier.
            }
            positionedTiles[coordinate] = positionedTile
            unpositionedTiles.remove(positionedTile.tile)
        }
        return positionedTiles
    }

    /**
     * Merges a map of puzzle pieces into an entire puzzle
     */
    private fun Map<Coordinate, TileState>.merge() : Map<Coordinate, Char> {
        val result = mutableMapOf<Coordinate, Char>()
        val tileSize = this.values.first().tile.size // All sizes should be equal
        for (coordinate in CoordinateRange(this.keys)) {
            val tileMap = this[coordinate]?.effectiveMap ?: error("Expected coordinate to exist in map")
            val origin = Coordinate(coordinate.x * tileSize, coordinate.y * tileSize)
            for (c in CoordinateRange(tileMap.keys)) {
                val offsetCoordinate = origin + c
                result[offsetCoordinate] = tileMap[c] ?: error("Expected coordinate to be present in tileMap")
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

    // Flip along the vertical axis
    if (flipped) {
        x = max - x
    }

    // Rotate counter clockwise
    repeat(rotation % 4) {
        val a = x
        x = y
        y = max - a
    }
    return Coordinate(x, y)
}