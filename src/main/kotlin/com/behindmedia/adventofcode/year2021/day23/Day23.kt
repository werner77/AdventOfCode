package com.behindmedia.adventofcode.year2021.day23

import com.behindmedia.adventofcode.common.*

data class Amphipod(val value: Char, val hasMoved: Boolean = false) {
    fun isValidDestination(coordinate: Coordinate, roomLevelCount: Int, state: Map<Coordinate, Amphipod>): Boolean {
        if (!hasMoved && coordinate.isValidOutside()) {
            return true
        }
        val (roomIndex, roomLevel) = coordinate.room(roomLevelCount) ?: return false
        val expectedValue = 'A' + roomIndex
        return expectedValue == value && (roomLevel == roomLevelCount - 1 || state[coordinate + Coordinate.down]?.value == expectedValue)
    }

    fun weight(): Long {
        var value = 1L
        for (i in 0 until this.value - 'A') {
            value *= 10L
        }
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return value == (other as? Amphipod)?.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

fun Coordinate.room(roomLevelCount: Int): Pair<Int, Int>? {
    val roomLevel = y - 2
    val roomIndex = if (x % 2 == 1) (x - 3) / 2 else -1
    return if (roomLevel in 0 until roomLevelCount && roomIndex in 0 until 4) {
        Pair(roomIndex, roomLevel)
    } else null
}

private val outsideXCoordinates = setOf(1, 2, 4, 6, 8, 10, 11)

fun Coordinate.isValidOutside(): Boolean {
    return y == 1 && x in outsideXCoordinates
}

fun Map<Coordinate, Char>.findValidDestinations(
    from: Map.Entry<Coordinate, Amphipod>,
    state: Map<Coordinate, Amphipod>,
    roomLevelCount: Int
): Collection<Path<Coordinate>> {
    val result = mutableListOf<Path<Coordinate>>()
    shortestPath(from = from.key,
        neighbours = {
            it.destination.directNeighbours
        }, reachable = {
            this[it] != '#' && state[it] == null
        }, process = {
            if (from.value.isValidDestination(it.destination, roomLevelCount, state)) {
                result += it
            }
            null
        })
    return result
}

fun Map<Coordinate, Amphipod>.isComplete(roomLevelCount: Int): Boolean {
    return entries.all { (c, a) ->
        c.room(roomLevelCount)?.let { it.first == (a.value - 'A') } ?: false
    }
}

fun solve(map: Map<Coordinate, Char>, roomLevelCount: Int): Long {
    val initialState = map.filter { it.value - 'A' in 0 until 4 }.mapValues { Amphipod(it.value) }
    return shortestWeightedPath(
        from = initialState,
        neighbours = { state ->
            val destinations = mutableListOf<Pair<Map<Coordinate, Amphipod>, Long>>()
            for (position in state) {
                map.findValidDestinations(position, state, roomLevelCount).forEach {
                    val newState = HashMap(state)
                    newState -= position.key
                    if (!position.value.hasMoved) {
                        newState[it.destination] = position.value.copy(hasMoved = true)
                    } else {
                        newState[it.destination] = position.value
                    }
                    destinations += Pair(newState, it.pathLength * position.value.weight())
                }
            }
            destinations
        },
        process = {
            if (it.destination.isComplete(roomLevelCount)) it.pathLength else null
        }
    ) ?: error("No path found")
}

private fun part1() {
    val map = parseMap("/2021/day23-1.txt") {
        it
    }

    timing {
        // Part 1
        println(solve(map, 2))
    }
}

private fun part2() {
    val map = parseMap("/2021/day23-2.txt") {
        it
    }

    timing {
        // Part 2
        println(solve(map, 4))
    }
}

fun main() {
    part1()
    part2()
}