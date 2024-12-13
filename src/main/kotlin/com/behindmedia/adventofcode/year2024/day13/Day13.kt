package com.behindmedia.adventofcode.year2024.day13

import com.behindmedia.adventofcode.common.*

private val pattern = """Button A: X\+(\d+), Y\+(\d+)\nButton B: X\+(\d+), Y\+(\d+)\nPrize: X=(\d+), Y=(\d+)""".toRegex()

private typealias Point = LongCoordinate

private data class Game(val buttonDirections: Pair<Point, Point>, val prizeLocation: Point) {
    companion object {
        operator fun invoke(string: String): Game {
            val match = pattern.matchEntire(string.trim()) ?: error("Invalid input: $string")
            val (x1, y1, x2, y2, x3, y3) = match.destructured
            return Game(
                Point(x1.toLong(), y1.toLong()) to Point(x2.toLong(), y2.toLong()),
                Point(x3.toLong(), y3.toLong())
            )
        }
    }

    init {
        val (button1, button2) = buttonDirections
        require(button1.x > 0 && button1.y > 0)
        require(button2.x > 0 && button2.y > 0)
        require(prizeLocation.x > 0 && prizeLocation.y > 0)
    }
}

fun main() = timing {
    val content = read("/2024/day13.txt")
    val games  = content.splitNonEmpty("\n\n").map {
        Game(it)
    }

    // Part 1
    println(games.mapNotNull { solve(it) }.sum())

    val offset = Point(10000000000000L, 10000000000000L)

    // Part 2
    println(games.map { it.copy(prizeLocation = it.prizeLocation + offset) }.mapNotNull { solve(it) }.sum())
}

private fun solve(game: Game): Long? {

    // a * x1 + b * x2 = x3
    // a * y1 + b * y2 = y3

    // a * x1 * y2 + b * x2 * y2 = x3 * y2
    // a * x2 * y1 + b * x2 * y2 = x2 * y3

    // a * x1 * y2 - a * x2 * y1 = x3 * y2 - x2 * y3

    // a = (x3 * y2 - x2 * y3) / (x1 * y2 - x2 * y1)
    // b = (x3 - a * x1) / x2

    val (button1, button2) = game.buttonDirections

    val x1 = button1.x
    val x2 = button2.x
    val x3 = game.prizeLocation.x

    val y1 = button1.y
    val y2 = button2.y
    val y3 = game.prizeLocation.y

    val remainderA = (x3 * y2 - x2 * y3) % (x1 * y2 - x2 * y1)

    if (remainderA != 0L) {
        return null
    }

    val a = (x3 * y2 - x2 * y3) / (x1 * y2 - x2 * y1)
    val remainderB = (x3 - a * x1) % x2

    if (remainderB != 0L) {
        return null
    }

    val b = (x3 - a * x1) / x2
    return a * 3L + b * 1L
}