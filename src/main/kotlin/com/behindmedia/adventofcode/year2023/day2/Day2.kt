package com.behindmedia.adventofcode.year2023.day2

import com.behindmedia.adventofcode.common.DefaultMap
import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.parseLines
import kotlin.math.max

data class Game(val id: Int, val draw: List<DefaultMap<String, Int>>) {
    companion object {
        operator fun invoke(string: String): Game {
            val (game, contents) = string.split(":")
            val gameId = game.split(" ")[1].toInt()
            val itemStrings = contents.split(";")
            val itemList = itemStrings.map {
                parseItems(it)
            }
            return Game(gameId, itemList)
        }

        private fun parseItems(itemString: String): DefaultMap<String, Int> {
            return defaultMutableMapOf<String, Int> {
                0
            }.apply {
                val items = itemString.split(',', ' ').filter { it.isNotEmpty() }
                for ((count, name) in items.chunked(2)) {
                    put(name, count.toInt())
                }
            }
        }
    }

    val minimalDraw: DefaultMap<String, Int> by lazy {
        defaultMutableMapOf<String, Int> { 0 }.apply {
            for (items in draw) {
                for ((name, count) in items) {
                    this[name] = max(count, this[name])
                }
            }
        }
    }
}

fun main() {
    val data = parseLines("/2023/day2.txt") { line ->
        Game(line)
    }

    // Part 1
    println(data.sumOf { game ->
        if (game.draw.all { it.total <= (12 + 13 + 14) && it["red"] <= 12 && it["green"] <= 13 && it["blue"] <= 14 }) {
            game.id
        } else {
            0
        }
    })

    // Part 2
    println(data.sumOf {
        it.minimalDraw.power
    })
}

private val DefaultMap<String, Int>.total: Int
    get() = values.sum()

private val DefaultMap<String, Int>.power: Int
    get() = this["red"] * this["green"] * this["blue"]
