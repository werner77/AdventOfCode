package com.behindmedia.adventofcode.year2020.day20

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.productOf
import com.behindmedia.adventofcode.common.read
import kotlin.math.roundToInt
import kotlin.math.sqrt

private val seaMonster = """
                  # 
#    ##    ##    ###
 #  #  #  #  #  #   
""".trimIndent()

private val orientations = 0 until 8

private data class Tile(val id: Int, private val grid: CharGrid) {
    val size: Int
        get() = grid.sizeX

    companion object {
        private val regex = """Tile (\d+):""".toRegex()
        operator fun invoke(string: String): Tile {
            val lines = string.split("\n")
            val header = lines[0]
            val (id) = regex.matchEntire(header)?.destructured ?: error("Could not match header: $header")
            val rest = lines.subList(1, lines.size)
            return Tile(id.toInt(), CharGrid(rest.joinToString("\n")))
        }
    }

    operator fun get(coordinate: Coordinate, orientation: Int): Char {
        return grid[coordinate, orientation]
    }
}

private operator fun CharGrid.get(coordinate: Coordinate, orientation: Int): Char {
    return this[coordinate.mapped(this.sizeX, orientation)]
}

private fun Coordinate.mapped(size: Int, orientation: Int): Coordinate {
    val max = size - 1
    val flipped = orientation.mod(8) % 2 == 1
    return when (orientation.mod(8) / 2) {
        0 -> if (flipped) Coordinate(max - this.x, this.y) else this
        1 -> if (flipped) Coordinate(max - this.y, max - this.x) else Coordinate(
            this.y,
            max - this.x
        )

        2 -> if (flipped) Coordinate(this.x, max - this.y) else Coordinate(
            max - this.x,
            max - this.y
        )

        3 -> if (flipped) Coordinate(this.y, this.x) else Coordinate(max - this.y, this.x)
        else -> error("Invalid rotation: ${orientation.mod(8) / 2}")
    }
}

fun main() {
    val sections = read("/2020/day20.txt").split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
    val tiles = sections.map { Tile(it) }

    val result = tiles.solve() ?: error("Could not find result")
    println(cornerCoordinates(tiles.squareSize).productOf { result[it]!!.first.id.toLong() })

    // Merge grid
    val mergedGrid = result.merge(borderSize = 1)
    println(orientations.minOf { roughness(mergedGrid, it) })
}

private fun roughness(grid: CharGrid, orientation: Int): Int {
    val allCoordinates = mutableSetOf<Coordinate>()
    val seaMonsterGrid = CharGrid(seaMonster)
    val size = grid.sizeX
    require(grid.sizeY == size)
    for (y in 0 until size - seaMonsterGrid.sizeY) {
        for (x in 0 until size - seaMonsterGrid.sizeX) {
            val c = Coordinate(x, y)
            var match = true
            val seaMonsterCoordinates = mutableListOf<Coordinate>()
            for ((dc, v) in seaMonsterGrid) {
                if (v != '#') continue
                if (grid[(c + dc).mapped(size, orientation)] != '#') {
                    match = false
                    break
                }
                seaMonsterCoordinates += c + dc
            }
            if (match) {
                allCoordinates += seaMonsterCoordinates
            }
        }
    }
    return grid.count { it.value == '#' } - allCoordinates.size
}

private fun Int.sqrt(): Int {
    return sqrt(this.toDouble()).roundToInt().also { require(it * it == this) }
}

private val Collection<Tile>.squareSize: Int
    get() = size.sqrt()

private fun cornerCoordinates(size: Int): List<Coordinate> {
    val result = mutableListOf<Coordinate>()
    for (x in listOf(0, size - 1)) {
        for (y in listOf(0, size - 1)) {
            result += Coordinate(x, y)
        }
    }
    return result
}

private fun Pair<Tile, Int>.matches(other: Pair<Tile, Int>, offset: Coordinate): Boolean {
    val size = this.first.size
    val direction = if (offset.isHorizontal) Coordinate.down else Coordinate.right
    val (first, second) = when (offset) {
        Coordinate.up -> Coordinate(0, 0) to Coordinate(0, size - 1)
        Coordinate.down -> Coordinate(0, size - 1) to Coordinate(0, 0)
        Coordinate.left -> Coordinate(0, 0) to Coordinate(size - 1, 0)
        Coordinate.right -> Coordinate(size - 1, 0) to Coordinate(0, 0)
        else -> error("Invalid offset: $offset")
    }
    val (tile1, orientation1) = this
    val (tile2, orientation2) = other
    return (0 until size).all {
        tile1[first + direction * it, orientation1] == tile2[second + direction * it, orientation2]
    }
}

private fun Map<Coordinate, Pair<Tile, Int>>.fits(tile: Tile, position: Coordinate, orientation: Int): Boolean {
    return Coordinate.directNeighbourDirections.all { direction ->
        val other = this[position + direction]
        other == null || (tile to orientation).matches(other, direction)
    }
}

private fun Map<Coordinate, Pair<Tile, Int>>.merge(borderSize: Int): CharGrid {
    val size = size.sqrt()
    val tileSize = this.values.first().first.size
    val tileSizeWithoutBorders = tileSize - 2 * borderSize
    return CharGrid(sizeX = size * tileSizeWithoutBorders, sizeY = size * tileSizeWithoutBorders) { x, y ->
        val tileCoordinate = Coordinate(x / tileSizeWithoutBorders, y / tileSizeWithoutBorders)
        val (tile, orientation) = this[tileCoordinate] ?: error("No coordinate found at: $tileCoordinate")
        val localCoordinate = Coordinate(borderSize + x - tileCoordinate.x * tileSizeWithoutBorders, borderSize + y - tileCoordinate.y * tileSizeWithoutBorders)
        tile[localCoordinate, orientation]
    }
}

private fun List<Tile>.solve(): Map<Coordinate, Pair<Tile, Int>>? {
    return solve(squareSize, Coordinate.origin, ArrayDeque(this), hashMapOf())
}

private fun Coordinate.next(size: Int): Coordinate {
    return if (x < size - 1) Coordinate(x = x + 1, y) else Coordinate(x = 0, y = y + 1)
}

private fun solve(
    size: Int,
    position: Coordinate,
    remaining: ArrayDeque<Tile>,
    settled: MutableMap<Coordinate, Pair<Tile, Int>>
): Map<Coordinate, Pair<Tile, Int>>? {
    if (remaining.isEmpty()) {
        require(position.y == size)
        return settled
    }
    val nextPosition = position.next(size)
    for (i in 0 until remaining.size) {
        val tile = remaining.removeFirst()
        for (orientation in orientations) {
            if (!settled.fits(tile, position, orientation)) continue
            settled[position] = tile to orientation
            val result = solve(size, nextPosition, remaining, settled)
            if (result != null) {
                return result
            }
            settled.remove(position)
        }
        remaining.addLast(tile)
    }
    return null
}