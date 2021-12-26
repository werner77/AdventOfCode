package com.behindmedia.adventofcode.year2017.day22

import com.behindmedia.adventofcode.common.*

fun main() {
    val map = parseMap("/2017/day22.txt") {
        it
    }

    // Part 1
    println(part1(map))

    // Part 2
    println(part2(map))
}

private fun part1(
    startMap: Map<Coordinate, Char>
): Int {
    return simulate(startMap, 10000, false)
}

private fun part2(startMap: Map<Coordinate, Char>): Int {
    return simulate(startMap, 10000000, true)
}

private fun simulate(
    startMap: Map<Coordinate, Char>,
    count: Int,
    part2: Boolean
): Int {
    val map = startMap.toMutableMap()
    val maxCoordinate = map.maxCoordinate
    val start = Coordinate(maxCoordinate.x / 2, maxCoordinate.y / 2)
    var current = start
    var direction = Coordinate.up
    var infected = 0

    repeat(count) {
        when (val c = map[current] ?: '.') {
            '#' -> {
                direction = direction.rotate(RotationDirection.Right)
                map[current] = if (part2) 'F' else '.'
            }
            '.' -> {
                direction = direction.rotate(RotationDirection.Left)
                map[current] = if (part2) 'W' else {
                    infected++
                    '#'
                }
            }
            'W' -> {
                map[current] = '#'
                infected++
            }
            'F' -> {
                map[current] = '.'
                for (i in 0 until 2) {
                    direction = direction.rotate(RotationDirection.Left)
                }
            }
            else -> error("Invalid char: $c")
        }
        current += direction
    }
    return infected
}