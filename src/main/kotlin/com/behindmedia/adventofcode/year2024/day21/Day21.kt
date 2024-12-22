package com.behindmedia.adventofcode.year2024.day21

import com.behindmedia.adventofcode.common.*
import java.util.*

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

private val directions = mapOf(
    '^' to Coordinate.up,
    'v' to Coordinate.down,
    '<' to Coordinate.left,
    '>' to Coordinate.right,
)

// 12 positions -> 4 bit
// 26 * 4 = 104 bit, we need two longs to store the positions

private val directionalGrid = Grid(directionalKeyboard)
private val numericGrid = Grid(numericKeyboard)

fun main() = timing {
    val codes = parseLines("/2024/day21.txt") { line ->
        line
    }
    println(codes.sumOf { code -> solve(code, 3).let { it.first * it.second } })
}

private fun solve(code: String, levelCount: Int): Pair<Int, Int> {
    val lastIndex = code.lastIndexOf('A')
    val numericPart = if (lastIndex == -1) {
        code.toInt()
    } else {
        code.substring(0, lastIndex).toInt()
    }
    val length = findLength(code, levelCount)
    return length to numericPart
}

private fun findLength(code: String, levelCount: Int): Int {
    return compute(code, levelCount) ?: error("No path found")
}

private typealias Grid = CharGrid
private typealias Point = Byte

private data class State(val index: Int, val positions: List<Point>)

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
    val direction = directions[command]
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

private fun compute(code: String, levelCount: Int): Int? {
    val pending = PriorityQueue<Path<State>>()
    val directionalCommands = listOf('A', '<', '^', '>', 'v')
    val startPositions = (0 until levelCount).map {
        level -> gridForLevel(level).single { it.value == 'A' }.key.point
    }
    pending.add(Path(State(index = 0, positions = startPositions), 0, null))
    val seen = mutableMapOf<State, Int>()
    while (!pending.isEmpty()) {
        val current = pending.poll() ?: error("No state found")
        val state = current.destination
        val length = current.length
        if (state.index == code.length) {
            // Found result
            return length
        }
        for (command in directionalCommands) {
            val nextCommand = executeCommand(state, command, levelCount, code) ?: continue
            val currentWeight = seen[nextCommand] ?: Int.MAX_VALUE
            val weight = length + 1
            if (weight < currentWeight) {
                seen[nextCommand] = weight
                pending.add(Path(nextCommand, weight, null))
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
