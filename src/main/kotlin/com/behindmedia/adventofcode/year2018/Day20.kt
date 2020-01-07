package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.CoordinatePath
import com.behindmedia.adventofcode.common.printMap

class Day20 {

    private val directions = mapOf(
        Pair('N', Coordinate.up),
        Pair('S', Coordinate.down),
        Pair('E', Coordinate.right),
        Pair('W', Coordinate.left)
    )

    private fun MutableMap<Coordinate, Char>.markRoom(coordinate: Coordinate, marker: Char = '.') {
        this[coordinate] = marker
        for (neighbour in coordinate.indirectNeighbours) {
            this[neighbour] = '#'
        }
    }

    private fun flattenOptions(encoding: List<Char>, startIndex: Int): List<List<Char>> {
        var level = 1
        val options = mutableListOf<MutableList<Char>>()
        var currentOption = mutableListOf<Char>()
        loop@ for (i in startIndex until encoding.size) {
            val c = encoding[i]
            when {
                c == '|' && level == 1 -> {
                    options.add(currentOption)
                    currentOption = mutableListOf()
                    continue@loop
                }
                c == '(' -> level++
                c == ')' -> level--
            }
            if (level == 0) {
                options.add(currentOption)
                for (option in options) {
                    option.addAll(encoding.subList(i + 1, encoding.size))
                }
                break
            }
            currentOption.add(c)
        }
        return options
    }

    private fun traverseMap(map: MutableMap<Coordinate, Char>, position: Coordinate, encoding: List<Char>) {
        var currentPosition = position
        for ((i, c) in encoding.withIndex()) {
            directions[c]?.let {
                for (j in 0..1) {
                    currentPosition += it
                    if (currentPosition in map) return
                    if (j == 0) {
                        map[currentPosition] = if (it.x == 0) '-' else '|'
                    } else {
                        map.markRoom(currentPosition)
                    }
                }
            } ?: run {
                when (c) {
                    '^' -> map.markRoom(currentPosition, 'X')
                    '$' -> return
                    '(' -> {
                        flattenOptions(encoding, i + 1).forEach {
                            traverseMap(map, currentPosition, it)
                        }
                        return
                    }
                    else -> throw IllegalStateException("Invalid character encountered: $c")
                }
            }
        }
    }

    fun parse(input: String): Map<Coordinate, Char> {
        val map = mutableMapOf<Coordinate, Char>()
        traverseMap(map, Coordinate.origin, input.toList())
        return map
    }

    fun shortestPaths(start: Coordinate = Coordinate.origin, map: Map<Coordinate, Char>): List<CoordinatePath> {
        val allPaths = mutableListOf<CoordinatePath>()
        start.reachableCoordinates(reachable = {
            map.getOrDefault(it, '#') != '#'
        }, process = {
            if (map[it.coordinate] == '.') allPaths.add(it)
            null
        })
        return allPaths
    }
}