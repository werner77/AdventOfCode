package com.behindmedia.adventofcode.year2021.day23

import com.behindmedia.adventofcode.common.*

private fun Coordinate.room(roomLevelCount: Int): Pair<Int, Int>? {
    val roomLevel = y - 2
    val roomIndex = if (x % 2 == 1) (x - 3) / 2 else -1
    return if (roomLevel in 0 until roomLevelCount && roomIndex in 0 until 4) {
        Pair(roomIndex, roomLevel)
    } else null
}

private val invalidOutsideXCoordinates = setOf(3, 5, 7, 9)

private fun Coordinate.isValidOutside(): Boolean {
    return y == 1 && x !in invalidOutsideXCoordinates
}

private data class Amphipod(val value: Char, val hasMoved: Boolean = false) {
    fun isValidDestination(coordinate: Coordinate, roomLevelCount: Int, state: State): Boolean {
        if (!hasMoved && coordinate.isValidOutside()) {
            return true
        }
        val (roomIndex, roomLevel) = coordinate.room(roomLevelCount) ?: return false
        val expectedValue = 'A' + roomIndex
        return expectedValue == value && (roomLevel == roomLevelCount - 1 || state[coordinate + Coordinate.down]?.value == expectedValue)
    }

    fun weight(): Int {
        var value = 1
        for (i in 0 until this.value - 'A') {
            value *= 10
        }
        return value
    }
}

private inline fun CharGrid.forEachValidDestination(
    from: Map.Entry<Coordinate, Amphipod>,
    state: State,
    roomLevelCount: Int,
    perform: (Path<Coordinate>) -> Unit
) {
    shortestPath(from = from.key,
        neighbours = { path ->
            path.destination.directNeighbours.filter { this[it] != '#' && state[it] == null }
        }, process = {
            if (from.value.isValidDestination(it.destination, roomLevelCount, state)) {
                perform(it)
            }
            null
        })
}

private typealias State = HashMap<Coordinate, Amphipod>

private fun State.isComplete(roomLevelCount: Int): Boolean = entries.all { (c, a) ->
    c.room(roomLevelCount)?.let { it.first == (a.value - 'A') } ?: false
}

private fun State.moving(from: Coordinate, to: Coordinate): State = State(this).also {
    val current = it.remove(from) ?: error("Expected from coordinate to exist")
    it[to] = if (current.hasMoved) current else current.copy(hasMoved = true)
}

fun solve(map: CharGrid, roomLevelCount: Int): Int {
    val initialState = State(map.toMap().filter { it.value - 'A' in 0 until 4 }.mapValues { Amphipod(it.value) })
    return shortestWeightedPath(
        from = initialState,
        neighbours = { state ->
            list {
                for (entry in state) {
                    map.forEachValidDestination(entry, state, roomLevelCount) {
                        val newState = state.moving(entry.key, it.destination)
                        add(Pair(newState, it.length * entry.value.weight()))
                    }
                }
            }
        },
        process = {
            if (it.destination.isComplete(roomLevelCount)) it.length else null
        }
    ) ?: error("No path found")
}

private fun part1() {
    val map: CharGrid = CharGrid(read("/2021/day23-1.txt"))
    timing {
        // Part 1
        println(solve(map, 2))
    }
}

private fun part2() {
    val map: CharGrid = CharGrid(read("/2021/day23-2.txt"))
    timing {
        // Part 2
        println(solve(map, 4))
    }
}

fun main() {
    part1()
    part2()
}