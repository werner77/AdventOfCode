package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.Coordinate

class Day20 {

    // ^ENWWW(NEEE|SSE(EE|N))$

    fun MutableMap<Coordinate, Char>.markRoom(coordinate: Coordinate, marker: Char = '.') {
        this[coordinate] = marker
        for (direction in coordinate.crossNeighbours) {
            this[coordinate - direction] = '#'
        }
    }

    fun flattenOptions(chars: List<Char>, startIndex: Int): List<List<Char>> {
        var level = 1
        val options = mutableListOf<MutableList<Char>>()
        var currentOption = mutableListOf<Char>()
        for (i in startIndex until chars.size) {
            val c = chars[i]
            when {
                c == '|' && level == 1 -> {
                    options.add(currentOption)
                    currentOption = mutableListOf()
                }
                c == '(' -> level++
                c == ')' -> level--
            }
            if (level == 0) {
                options.add(currentOption)
                for (option in options) {
                    option.addAll(chars.subList(i + 1, chars.size))
                }
                break
            }
            currentOption.add(c)
        }
        return options
    }

    fun traverseMap(map: MutableMap<Coordinate, Char>, position: Coordinate, string: List<Char>) {
        var currentPosition = position
        loop@ for ((i, c) in string.withIndex()) {
            when (c) {
                '^' -> map.markRoom(currentPosition, 'X')
                '$' -> break@loop
                '(' -> {
                    flattenOptions(string, i + 1).forEach {
                        traverseMap(map, currentPosition, it)
                    }
                    break@loop
                }

                else -> throw IllegalStateException("Invalid character encountered: $c")
            }
        }
    }

    fun parse(input: String): Map<Coordinate, Char> {

        val directions = mapOf<Char, Coordinate>(
            Pair('N', Coordinate.up),
            Pair('S', Coordinate.down),
            Pair('E', Coordinate.right),
            Pair('W', Coordinate.left)
        )

        for ((i, c) in input.withIndex()) {

            if (c in "NSEW") {

            }

            val direction = directions[c]
            if (direction != null) {

            } else {

            }
        }


        return mutableMapOf()
    }

}