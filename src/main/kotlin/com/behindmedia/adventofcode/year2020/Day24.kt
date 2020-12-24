package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines

class Day24 {
    enum class Direction {
        E, SE, SW, W, NW, NE
    }

    fun part1(input: String) : Long {
        val lines = parseLines(input) {
            parseDirections(it)
        }
        println(lines)
        return 0
    }

    fun part2(input: String) : Long {
        return 0
    }

    private fun parseDirections(string: String) : List<Direction> {
        val result = mutableListOf<Direction>()
        val buffer = StringBuilder()
        for (c in string) {
            buffer.append(c)

            try {
                val direction = Direction.valueOf(buffer.toString().toUpperCase())
                result.add(direction)
                buffer.clear()
            } catch (e: IllegalArgumentException) {
                //ignore
            }
        }
        return result
    }
}