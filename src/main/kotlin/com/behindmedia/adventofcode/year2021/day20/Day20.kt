package com.behindmedia.adventofcode.year2021.day20

import com.behindmedia.adventofcode.common.*

private fun enhance(image: Map<Coordinate, Char>, data: String, default: Char): Map<Coordinate, Char> {
    val output = mutableMapOf<Coordinate, Char>()
    val minCoordinate = image.minCoordinate
    val maxCoordinate = image.maxCoordinate
    val extra = 2
    for (y in minCoordinate.y - extra..maxCoordinate.y + extra) {
        for (x in minCoordinate.x - extra..maxCoordinate.x + extra) {
            var index = 0
            var bit = 8
            for (j in -1..1) {
                for (i in -1..1) {
                    val current = image[Coordinate(x + i, y + j)] ?: default
                    if (current == '#') {
                        index = index or (1 shl bit)
                    }
                    bit--
                }
            }
            output[Coordinate(x, y)] = if (data[index] == '#') '#' else '.'
        }
    }
    return output
}

private fun enhance(image: Map<Coordinate, Char>, data: String, count: Int): Map<Coordinate, Char> {
    var map: Map<Coordinate, Char> = image
    for (i in 0 until count) {
        val default = if (i % 2 == 1) data[0] else '.'
        map = enhance(map, data, default)
    }
    return map
}

private val Map<Coordinate, Char>.litPixelCount: Int
    get() = values.count { it == '#' }

fun main() {
    val (data, image) = parse("/2021/day20.txt") { text ->
        val (enhancement, image) = text.split("\n\n")
        val imageMap = mutableMapOf<Coordinate, Char>()
        image.split("\n").mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                imageMap[Coordinate(x, y)] = c
            }
        }
        Pair(enhancement, imageMap)
    }

    // Part 1
    println(enhance(image, data, 2).litPixelCount)

    // Part 2
    println(enhance(image, data, 50).litPixelCount)
}