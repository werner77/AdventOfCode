package com.behindmedia.adventofcode.year2016

import com.behindmedia.adventofcode.common.*

private val Map<Coordinate, Char>.height
    get() = this.maxOf { it.key.y } + 1

private val Map<Coordinate, Char>.width
    get() = this.maxOf { it.key.x } + 1

private sealed class Instruction {

    /*
    rect 2x1
    rotate row y=0 by 5
    rotate column x=0 by 1
     */

    abstract fun apply(map: MutableMap<Coordinate, Char>)

    companion object {
        operator fun invoke(input: String): Instruction {
            return if (input.startsWith("rect")) {
                val components = input.splitSequence(" x").toList()
                PlaceRect(components[1].toInt(), components[2].toInt())
            } else if (input.startsWith("rotate row")) {
                val components = input.splitSequence(" =").toList()
                RotateRow(components[3].toInt(), components[5].toInt())
            } else if (input.startsWith("rotate column")) {
                val components = input.splitSequence(" =").toList()
                RotateColumn(components[3].toInt(), components[5].toInt())
            } else {
                error("Invalid input: $input")
            }
        }
    }

    data class RotateColumn(val x: Int, val amount: Int) : Instruction() {
        override fun apply(map: MutableMap<Coordinate, Char>) {
            val height = map.height
            val newValues = mutableSetOf<Coordinate>()
            for (y in 0 until height) {
                // Shift down amount
                val coordinate = Coordinate(x, y)
                val newCoordinate = Coordinate(x, (y + amount) % height)
                if (map[coordinate] == '#') {
                    newValues.add(newCoordinate)
                }
                map[coordinate] = ' '
            }
            for (c in newValues) {
                map[c] = '#'
            }
        }
    }

    data class RotateRow(val y: Int, val amount: Int) : Instruction() {
        override fun apply(map: MutableMap<Coordinate, Char>) {
            val width = map.width
            val newCoordinates = mutableSetOf<Coordinate>()
            for (x in 0 until width) {
                // Shift down amount
                val coordinate = Coordinate(x, y)
                if (map[coordinate] == '#') {
                    val newCoordinate = Coordinate((x + amount) % width, y)
                    newCoordinates.add(newCoordinate)
                }
                map[coordinate] = ' '
            }
            for (c in newCoordinates) {
                map[c] = '#'
            }
        }
    }

    data class PlaceRect(val width: Int, val height: Int) : Instruction() {
        override fun apply(map: MutableMap<Coordinate, Char>) {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    map[Coordinate(x, y)] = '#'
                }
            }
        }
    }
}

private fun <T> parseLines(lineParser: (String) -> T): List<T> {
    return parseLines("/2016/day8.txt") {
        lineParser.invoke(it)
    }
}

private fun part1And2() {
    val map = mutableMapOf<Coordinate, Char>()
    for (x in 0 until 50) {
        for (y in 0 until 6) {
            map[Coordinate(x, y)] = ' '
        }
    }
    val lines = parseLines { Instruction(it) }
    for (instr in lines) {
        instr.apply(map)
    }
    map.printMap(' ')
    println(map.entries.count { it.value == '#' })
}

fun main() {
    part1And2()
}