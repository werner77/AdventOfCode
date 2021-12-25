package com.behindmedia.adventofcode.year2017.day21

import com.behindmedia.adventofcode.common.*

private val startTileString =
    """
    .#.
    ..#
    ###
    """.trimIndent()

typealias Transform = (Int, Int, Int) -> Pair<Int, Int>

private val transforms = arrayOf<Transform>(
    { _, x, y -> Pair(x, y) },
    { size, x, y -> Pair(x, size - 1 - y) },
    { size, x, y -> Pair(size - 1 - x, y) },
    { size, x, y -> Pair(size - 1 - x, size - 1 - y) },
    { _, x, y -> Pair(y, x) },
    { size, x, y -> Pair(y, size - 1 - x) },
    { size, x, y -> Pair(size - 1 - y, x) },
    { size, x, y -> Pair(size - 1 - y, size - 1 - x) },
)

private class Tile(private val data: CharMap) {
    companion object {
        operator fun invoke(string: String): Tile {
            return Tile(CharMap(string.split("/").joinToString("\n")))
        }
    }

    val size: Int
        get() = data.sizeX

    operator fun get(coordinate: Coordinate): Char {
        return data[coordinate]
    }

    fun match(map: CharMap, startCoordinate: Coordinate, size: Int): Boolean {
        if (size != this.size) return false
        return transforms.any { match(map, startCoordinate, it) }
    }

    private fun match(
        map: CharMap,
        startCoordinate: Coordinate,
        transform: Transform
    ): Boolean {
        for (y in 0 until size) {
            for (x in 0 until size) {
                val (x1, y1) = transform(size, x, y)
                if (map[startCoordinate + Coordinate(x, y)] != data[Coordinate(x1, y1)]) {
                    return false
                }
            }
        }
        return true
    }

    override fun toString(): String {
        return data.toString()
    }
}

private data class Rule(val input: Tile, val output: Tile)

fun main() {
    val rules = parseLines("/2017/day21.txt") { line ->
        val components = line.splitNonEmptySequence(" ", "=", ">").toList()
        require(components.size == 2)
        Rule(Tile(components[0]), Tile(components[1]))
    }

    // Part 1
    val map1 = enhance(rules, 5)
    println(map1.count { it.value == '#' })

    // Part 2
    timing {
        val map2 = enhance(rules, 18)
        println(map2.count { it.value == '#' })
    }
}

private fun enhance(rules: List<Rule>, iterationCount: Int): CharMap {
    var map = CharMap(startTileString)
    var size = map.sizeX
    var tileSize = size
    repeat(iterationCount) {
        val nextSize = if (tileSize == 2) 3 * size / 2 else 4 * size / 3
        val nextMap = CharMap(nextSize)
        val tileCount = size / tileSize
        for (x in 0 until tileCount) {
            for (y in 0 until tileCount) {
                val c1 = Coordinate(x * tileSize, y * tileSize)
                val c2 = Coordinate(x * (tileSize + 1), y * (tileSize + 1))
                var foundMatch = false
                for (rule in rules) {
                    if (rule.input.match(map, c1, tileSize)) {
                        for (i in 0 until tileSize + 1) {
                            for (j in 0 until tileSize + 1) {
                                val c = Coordinate(i, j)
                                nextMap[c2 + c] = rule.output[c]
                            }
                        }
                        foundMatch = true
                        break
                    }
                }
                require(foundMatch)
            }
        }
        size = nextSize
        tileSize = if (size % 2 == 0) 2 else 3
        map = nextMap
        require(map.sizeX * map.sizeY == size * size)
        println(it + 1)
    }
    return map
}