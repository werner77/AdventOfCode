package com.behindmedia.adventofcode.year2024.day21

import com.behindmedia.adventofcode.common.*
import kotlin.math.min

private val numericKeyboard = """
789
456
123
#0A
""".trimIndent()

private val directionalKeyboard = """
#^A
<v>
""".trimIndent()

private val Char.direction: Coordinate?
    get() = when (this) {
        '^' -> Coordinate.up
        'v' -> Coordinate.down
        '<' -> Coordinate.left
        '>' -> Coordinate.right
        else -> null
    }

private val Coordinate.character: Char?
    get() = when (this) {
        Coordinate.up -> '^'
        Coordinate.down -> 'v'
        Coordinate.left -> '<'
        Coordinate.right -> '>'
        else -> null
    }

private class Grid(private val impl: CharGrid): ValueGrid<Char> by impl {
    private val lookupTable = impl.associate { it.value to it.key }

    fun isReachable(coordinates: Coordinate): Boolean {
        val value = this.getOrNull(coordinates)
        return value != null && value != '#'
    }

    fun coordinate(char: Char): Coordinate {
        return lookupTable[char] ?: error("Invalid character: '$char'")
    }
}

private val directionalGrid = Grid(CharGrid(directionalKeyboard))
private val numericGrid = Grid(CharGrid(numericKeyboard))
private val translations: MutableMap<String, List<List<String>>> = mutableMapOf()

fun main() = timing {
    val codes = parseLines("/2024/day21.txt") { line ->
        line
    }

    // Part 1
    println(codes.sumOf { code -> solve(code, 3).let { it.first * it.second } })

    // Part 2
    println(codes.sumOf { code -> solve(code, 26).let { it.first * it.second } })
}

private fun solve(code: String, level: Int): Pair<Long, Long> {
    val lastIndex = code.lastIndexOf('A')
    val numericPart = if (lastIndex == -1) {
        code.toLong()
    } else {
        code.substring(0, lastIndex).toLong()
    }
    val length = findLevel(code, level)
    return length to numericPart
}

private fun findLevel(code: String, levelCount: Int): Long {
    val minValues = MutableList(code.length) {
        Long.MAX_VALUE
    }
    numericGrid.findPath(code) { index, directions ->
        val length = findLength(directions, levelCount - 1)
        minValues[index] = min(minValues[index], length)
    }
    return minValues.sum()
}

private fun findLength(directions: List<Coordinate>, level: Int): Long {
    val startPattern = (directions.map { it.character!! } + 'A').joinToString("")
    val map = hashMapOf<Pair<Int, String>, Long>()
    return dfs(level, startPattern, map)
}

private fun dfs(left: Int, pattern: String, cache: MutableMap<Pair<Int, String>, Long>): Long {
    if (left == 0) {
        return pattern.length.toLong()
    }
    return cache.getOrPut(left to pattern) {
        val options = translations.getOrPut(pattern) {
            directionalGrid.findPatternTranslations(pattern)
        }
        require(options.isNotEmpty())
        var minTotal = Long.MAX_VALUE
        for (option in options) {
            var total = 0L
            for (translation in option) {
                total += dfs(left - 1, translation, cache)
            }
            minTotal = min(minTotal, total)
        }
        minTotal
    }
}

private fun Grid.findPatternTranslations(pattern: String): List<List<String>> {
    // Find a brute force list of translations for this pattern using path finding
    val origin = this.coordinate('A')
    var start = origin
    var result: List<List<String>>? = null
    for (c in pattern) {
        val end = this.coordinate(c)
        val options = mutableListOf<String>()
        shortestWeightedPath(
            from = start,
            neighbours = { pos -> pos.directNeighbours.filter { isReachable(it) }.map { it to 1} },
            findAll = true,
            process = { path ->
                if (path.destination == end) {
                    options += path.completeDirections.map { it.character }.joinToString("") + 'A'
                } else {
                    null
                }
            }
        )
        if (result == null) {
            result = List(options.size) { listOf(options[it]) }
        } else {
            val newResult = mutableListOf<List<String>>()
            for (option in options) {
                for (current in result) {
                    newResult.add(current + option)
                }
            }
            result = newResult
        }
        start = end
    }
    return result ?: error("No result")
}

/**
 * Finds all shortest paths to type in the specified code on the keypad.
 *
 * For each character at index i the option lambda is invoked.
 */
private fun Grid.findPath(code: String, option: (Int, List<Coordinate>) -> Unit) {
    var start = this.coordinate('A')
    for ((i, c) in code.withIndex()) {
        val end = this.coordinate(c)
        shortestWeightedPath(
            from = start,
            neighbours = { pos -> pos.directNeighbours.filter { isReachable(it) }.map { it to 1 } },
            findAll = true,
            process = { path ->
                if (path.destination == end) {
                    option.invoke(i, path.completeDirections)
                } else {
                    null
                }
            }
        )
        start = end
    }
}
