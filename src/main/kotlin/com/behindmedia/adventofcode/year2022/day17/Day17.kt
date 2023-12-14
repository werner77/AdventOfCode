package com.behindmedia.adventofcode.year2022.day17

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.common.Coordinate.Companion
import kotlin.math.max
import kotlin.math.min

private val emptyChar = '.'

private val rocks = listOf(
    CharGrid("####", emptyChar).coordinates,
    CharGrid(".#.\n###\n.#.", emptyChar).coordinates,
    CharGrid("..#\n..#\n###", emptyChar).coordinates,
    CharGrid("#\n#\n#\n#", emptyChar).coordinates,
    CharGrid("##\n##", emptyChar).coordinates
).map { Rock(it, Companion.origin) }

private val minRepitition = rocks.sumOf { it.sizeY }

private val directionMap: Map<Char, Coordinate> = mapOf(
    '<' to Coordinate.left,
    '>' to Coordinate.right
)

private val CharGrid.coordinates: List<Coordinate>
    get() = this.filter { it.value != emptyChar }.map { it.key }

private val Char.coordinate: Coordinate
    get() = directionMap[this] ?: error("Direction not found for: $this")

private data class Rock(private val coordinates: List<Coordinate>, var offset: Coordinate) {
    val minX: Int
        get() = allCoordinates().minOf { it.x }

    val minY: Int
        get() = allCoordinates().minOf { it.y }

    val maxX: Int
        get() = allCoordinates().maxOf { it.x }

    val maxY: Int
        get() = allCoordinates().maxOf { it.y }

    val sizeY = coordinates.maxOf { it.y } + 1

    fun allCoordinates(): Sequence<Coordinate> {
        return coordinates.asSequence().map {
            it + offset
        }
    }

    fun hasCoordinate(coordinate: Coordinate): Boolean {
        return allCoordinates().any { it == coordinate }
    }
}

private data class GameMap(val items: MutableMap<Coordinate, Char>, val sizeX: Int) {

    var minY: Int = 0
        private set

    val height: Int
        get() = -minY

    init {
        for (i in 0 until sizeX) {
            items[Coordinate(i, 0)] = '_'
        }
    }

    fun canMove(rock: Rock, direction: Coordinate): Boolean {
        val occupied = rock.allCoordinates().any {
            // occupied
            val c = it + direction
            val existing = items[c] ?: emptyChar
            existing != emptyChar || c.x < 0 || c.x >= sizeX
        }
        return !occupied
    }

    fun settle(rock: Rock) {
        rock.allCoordinates().forEach {
            items[it] = '#'
            minY = min(minY, it.y)
        }
    }

    fun print(rock: Rock) {
        val minX = min(items.minX, rock.minX)
        val maxX = max(items.maxX, rock.maxX)
        val minY = min(items.minY, rock.minY)
        val maxY = max(items.maxY, rock.maxY)
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val coordinate = Coordinate(x, y)
                print(items[coordinate] ?: if (rock.hasCoordinate(coordinate)) '@' else '.')
            }
            println()
        }
        println()
    }

    fun isSameLine(y1: Int, y2: Int): Boolean {
        for (x in 0 until sizeX) {
            val v1 = items[Coordinate(x, y1)]
            val v2 = items[Coordinate(x, y2)]
            if (v1 != v2) return false
        }
        return true
    }

    fun findInterval(): Int? {
        // Find the same line as the top one
        val y1 = minY
        var j = minRepitition
        while (true) {
            val y2 = y1 + j
            if (y2 >= 0) return null
            var sameLineCount = 0
            while (isSameLine(y1 + sameLineCount, y2 + sameLineCount)) {
                sameLineCount++
            }
            require(y2 > y1)
            if (sameLineCount == y2 - y1 - 1) {
                return sameLineCount + 1
            }
            j++
        }
    }
}

fun main() {
    val directions = parseLines("/2022/day17.txt") { line ->
        line.map { it.coordinate }
    }.single()

    // Part 1
    timing {
        part1(directions)
    }
    timing {
        part2(directions)
    }
}

private fun part1(directions: List<Coordinate>) {
    val ans = simulate(directions) { gameMap, r ->
        if (r == 2022) gameMap.height else null
    }
    println(ans)
}

private fun <T>simulate(directions: List<Coordinate>, exitCondition: (GameMap, Int) -> T?): T {
    // Y coordinate starts at 0 (floor)
    val gameMap = GameMap(items = mutableMapOf(), 7)
    var rock: Rock? = null
    var i = 0
    var r = 0
    while (true) {
        // get next direction
        val direction = directions[(i++ % directions.size)]
        if (rock == null) {
            val result = exitCondition.invoke(gameMap, r)
            if (result != null) return result
            rock = rocks[(r++ % rocks.size)].apply {
                offset = Coordinate(2, gameMap.minY - sizeY - 3)
            }
        }
        // move left/right
        if (gameMap.canMove(rock, direction)) {
            rock.offset += direction
        }
        // Check whether we can move down
        if (gameMap.canMove(rock, Coordinate.down)) {
            rock.offset += Companion.down
        } else {
            // settle
            gameMap.settle(rock)
            rock = null
        }
    }
}

private fun part2(directions: List<Coordinate>) {

    // Map of key = height to value = number of rocks
    val heightToRockMap = mutableMapOf<Int, Int>()

    val (intervalRocks, intervalHeight) = simulate(directions) { gameMap, r ->
        val currentHeight = gameMap.height
        heightToRockMap[currentHeight] = r
        val intervalHeight = gameMap.findInterval()
        if (intervalHeight != null) {
            val previousRocks = heightToRockMap[currentHeight - intervalHeight] ?: error("Previous height not found in map")
            val intervalRocks = r - previousRocks
            Pair(intervalRocks.toLong(), intervalHeight.toLong())
        } else {
            null
        }
    }

    val target = 1000000000000L
    val numberOfIntervals = target / intervalRocks - 1L
    val totalIntervalHeight = numberOfIntervals * intervalHeight
    val remainderIterations = (target - numberOfIntervals * intervalRocks).toInt()

    // Now simulate the remainder and add it to the totalIntervalHeight
    val ans = simulate(directions) { gameMap, r ->
        if (r == remainderIterations) {
            totalIntervalHeight + gameMap.height.toLong()
        } else {
            null
        }
    }
    println(ans)
}