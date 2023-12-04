package com.behindmedia.adventofcode.year2023.day3

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.coordinateRange
import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.maxX
import com.behindmedia.adventofcode.common.parseMap
import com.behindmedia.adventofcode.common.plusAssign
import com.behindmedia.adventofcode.common.product

fun main() {

    val data = parseMap("/2023/day3.txt") { it }

    // Part 1
    println(processMap(data) { it.isSymbol() }.values.sumOf { it.sum() })

    // Part 2
    println(processMap(data) { it.isGear() }.values.filter { it.size == 2 }.sumOf { it.product() })
}

/**
 * Processes the map and uses the supplied closure to determine whether a character is a valid symbol or not.
 * If valid the neighbouring numbers of that coordinate will be put in the returned map.
 */
private fun processMap(
    data: Map<Coordinate, Char>,
    isValidSymbol: (Char) -> Boolean
): Map<Coordinate, List<Int>> {
    val maxX = data.maxX
    val iterator = data.coordinateRange.iterator()
    val symbolMap = defaultMutableMapOf<Coordinate, MutableList<Int>>(true) { mutableListOf() }
    val buffer = StringBuilder()
    while (iterator.hasNext()) {
        buffer.clear()
        val symbolCoordinates = mutableSetOf<Coordinate>()
        while (iterator.hasNext()) {
            val c = iterator.next()
            val v = data[c]?.takeIf { it.isDigit() } ?: break
            symbolCoordinates += c.allNeighbours.filter {
                data[it]?.let { value -> isValidSymbol(value) } == true
            }
            buffer += v
            if (c.x == maxX) break
        }
        if (buffer.isNotEmpty()) {
            symbolCoordinates.forEach { symbolCoordinate ->
                symbolMap[symbolCoordinate] += buffer.toString().toInt()
            }
        }
    }
    return symbolMap
}

private fun Char.isGear(): Boolean {
    return this == '*'
}

private fun Char.isSymbol(): Boolean {
    return this != '.' && !this.isDigit()
}