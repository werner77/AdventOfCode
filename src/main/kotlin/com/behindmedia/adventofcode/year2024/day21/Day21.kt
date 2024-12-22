package com.behindmedia.adventofcode.year2024.day21

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.common.Coordinate.Companion.down
import com.behindmedia.adventofcode.common.Coordinate.Companion.left
import com.behindmedia.adventofcode.common.Coordinate.Companion.right
import com.behindmedia.adventofcode.common.Coordinate.Companion.up
import java.util.*
import kotlin.collections.ArrayDeque
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

private val directionalGrid = Grid(directionalKeyboard)
private val numericGrid = Grid(numericKeyboard)

private val numericLookupTable = numericGrid.associate { it.value to it.key }
private val directionalLookupTable = directionalGrid.associate { it.value to it.key }

private val translations: MutableMap<String, List<List<String>>> = getTranslations().toMutableMap()

fun main() = timing {
    val codes = parseLines("/2024/day21.txt") { line ->
        line
    }

    //println(solve("980A", 3))

    println(codes.sumOf { code -> solve(code, 26).let { it.first * it.second } })

    // Links
    //println(solve("0", 26).first)


    //printTranslations()

    //optimalTranslations()
//    for (level in 1 until 8) {
//        println("Level: $level")
//
//        println(findLength(listOf(up), level - 1))
//        println(solve("3", level).first)
//    }


}

private fun dfs(left: Int, pattern: String, cache: MutableMap<Pair<Int, String>, Long>): Long {
    if (left == 0) {
        return pattern.length.toLong()
    }
    return cache.getOrPut(left to pattern) {
        val options = translations.getOrPut(pattern) {
            findPatternTranslations(pattern)
        }
//
//        val options = translations[pattern] ?: error("Pattern not found: $pattern")
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

private fun findLength(directions: List<Coordinate>, maxLevel: Int): Long {
    val startPattern = (directions.map { it.character!! } + 'A').joinToString("")
    val map = hashMapOf<Pair<Int, String>, Long>()
    return dfs(maxLevel, startPattern, map)
}

private fun findPatternTranslations(pattern: String): List<List<String>> {
    // Find a brute force translation for this pattern
    val grid = directionalGrid
    val origin = directionalLookupTable['A']!!
    var start = origin
    var result: List<List<String>>? = null
    for (end in pattern) {
        val target = directionalLookupTable[end]!!
        val pending = PriorityQueue<Path<Coordinate>>()
        pending += Path(start, 0, null)
        val seen = mutableMapOf<Coordinate, Int>()
        var minLength: Int? = null
        val options = mutableListOf<String>()
        while (pending.isNotEmpty()) {
            val current = pending.poll()
            val pos = current.destination
            if (minLength != null && current.length > minLength) {
                break
            }
            if (pos == target) {
                // Found target, add to result
                options += current.completeDirections.map { it.character }.joinToString("") + "A"
                if (minLength == null) {
                    minLength = current.length
                }
            }
            for (direction in arrayOf(right, up, down, left)) {
                val next = pos + direction
                val value = grid.getOrNull(next) ?: continue
                if (value != '#') {
                    val heuristic = 0
                    val weight = current.length + 1
                    val currentWeight = seen[next] ?: Int.MAX_VALUE
                    if (weight <= currentWeight) {
                        seen[next] = weight
                        pending += Path(next, current.length + 1, current, heuristic)
                    }
                }
            }
        }

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
        start = target
    }
    return result ?: error("No result")
}

private fun translateToNextLevel(map: MutableMap<String, List<List<String>>>, pattern: String): String {
    val buffer = StringBuilder()
    require(pattern.last() == 'A')
    val current = mutableListOf<Char>()
    for (c in pattern) {
        current += c
        if (c == 'A') {
            val key = current.joinToString("")
            val existing = map[key] ?: run {
                val computed = findPatternTranslations(key)
                map[key] = computed
                computed
            }
            for (option in existing) {
                for (s in option) {
                    buffer += s
                }
            }
            current.clear()
        }
    }
    return buffer.toString()
}

private fun solve(code: String, levelCount: Int): Pair<Long, Long> {
    val lastIndex = code.lastIndexOf('A')
    val numericPart = if (lastIndex == -1) {
        code.toLong()
    } else {
        code.substring(0, lastIndex).toLong()
    }
    val length = findLengthFast(code, levelCount)
    return length to numericPart
}

private fun findLengthFast(code: String, levelCount: Int): Long {
    val minValues = MutableList(code.length) {
        Long.MAX_VALUE
    }

    findPath(code) { index, directions ->
        val length = findLength(directions, levelCount - 1)
        minValues[index] = min(minValues[index], length)
    }

    return minValues.sum()
}

private fun getTranslations(): Map<String, List<List<String>>> {
    val mappings = mutableMapOf<String, List<List<String>>>()
    for (direction in Coordinate.directNeighbourDirections) {
        val startPattern = when (direction) {
            Coordinate.up -> "^A"
            Coordinate.down -> "vA"
            Coordinate.left -> "<A"
            Coordinate.right -> ">A"
            else -> error("Invalid direction $direction")
        }
        var current = startPattern
        for (level in 0 until 4) {
            current = translateToNextLevel(mappings, current)
        }
    }
    return mappings
}

private fun findPath(code: String, option: (Int, List<Coordinate>) -> Unit) {
    val grid = numericGrid
    var start = numericLookupTable['A']!!
    for ((i, c) in code.withIndex()) {
        val end = numericLookupTable[c]!!
        val seen = mutableMapOf<Coordinate, Int>()
        val pending = ArrayDeque<Path<Coordinate>>()
        pending += Path(start, 0, null)
        var minLength: Int? = null
        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            val pos = current.destination

            if (minLength != null && current.length > minLength) {
                break
            }

            if (pos == end) {
                // yield an option
                val commands = current.completeDirections
                option.invoke(i, commands)
                minLength = current.length
            }

            for (next in pos.directNeighbours) {
                val value = grid.getOrNull(next) ?: continue
                if (value != '#') {
                    val existingWeight = seen[next] ?: Int.MAX_VALUE
                    val weight = current.length + 1
                    if (weight <= existingWeight) {
                        seen[next] = weight
                        pending += Path(next, weight, current)
                    }
                }
            }
        }
        start = end
    }
}

private typealias Grid = CharGrid
private typealias Point = Byte

private data class State(val index: Int, val positions: List<Point>) {
    fun heuristic(code: String): Int {
        return code.length - index
    }
}

val Coordinate.point: Point
    get() {
        return ((this.x shl 4) or this.y).toByte()
    }

val Point.coordinate: Coordinate
    get() {
        val i = this.toInt()
        val y = i and 15
        val x = i shr 4
        return Coordinate(x, y)
    }

private fun gridForLevel(level: Int): Grid {
    return if (level == 0) {
        numericGrid
    } else {
        directionalGrid
    }
}

private fun executeCommand(state: State, command: Char, level: Int, code: String): State? {
    val direction = command.direction
    if (direction != null) {
        require(level > 0)
        val nextLevel = level - 1
        // Send directional command to next level -> change the position
        val position = state.positions[nextLevel]
        // Change position
        val next = position.coordinate + direction
        val grid = gridForLevel(nextLevel)
        val value = grid.getOrNull(next)
        return if (value != null && value != '#') {
            State(index = state.index, positions = state.positions.copy(index = nextLevel, position = next.point))
        } else {
            // Invalid command
            null
        }
    } else {
        return if (level == 0) {
            return if (command == code[state.index]) {
                State(index = state.index + 1, positions = state.positions)
            } else {
                null
            }
        } else {
            require(command == 'A')
            // Commit command on next level
            val nextLevel = level - 1
            val grid = gridForLevel(nextLevel)
            val position = state.positions[nextLevel]
            val value = grid[position.coordinate]
            executeCommand(state, value, level - 1, code)
        }
    }
}

private fun compute(code: String, levelCount: Int): Pair<List<Char>, Int>? {
    val pending = PriorityQueue<Path<Pair<State, Char?>>>()
    val directionalCommands = listOf('A', '<', '^', '>', 'v')
    val startPositions = (0 until levelCount).map { level ->
        gridForLevel(level).single { it.value == 'A' }.key.point
    }
    pending.add(Path(State(index = 0, positions = startPositions) to null, 0, null))
    val seen = mutableMapOf<State, Int>()
    while (!pending.isEmpty()) {
        val current = pending.poll() ?: error("No state found")
        val state = current.destination.first
        val length = current.length
        if (state.index == code.length) {
            // Found result
            val commands = current.nodes { it.second != null }.map { it.second!! }
            return commands to length
        }
        for (command in directionalCommands) {
            val nextCommand = executeCommand(state, command, levelCount, code) ?: continue
            val currentWeight = seen[nextCommand] ?: Int.MAX_VALUE
            val heuristic = nextCommand.heuristic(code)
            val weight = length + 1 + heuristic
            if (weight < currentWeight) {
                seen[nextCommand] = weight
                pending.add(Path(nextCommand to command, length + 1, current, heuristic))
            }
        }
    }
    return null
}

private fun List<Point>.copy(index: Int, position: Point): List<Point> {
    require(index in indices)
    val current = this
    return List(size) { i ->
        if (index == i) {
            position
        } else {
            current[i]
        }
    }
}
