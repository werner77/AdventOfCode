package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import kotlin.math.max
import kotlin.math.min

class Day24 {

    /**
     * Parses the input and returns the map represented as an integer where each bit denotes whether the coordinate
     * at that position is populated or not
     */
    fun parseInput(input: String): Int {
        var state = 0
        var i = 0
        for (line in input.split('\n')) {
            for (c in line) {
                if (c == '#') {
                    state = state or (1 shl i)
                }
                i++
            }
        }
        return state
    }

    fun findFirstRepeatedState(input: String): Int {
        var state = parseInput(input)
        val encounteredStates = mutableSetOf<Int>()
        encounteredStates.add(state)

        while (true) {
            state = process(state)
            if (encounteredStates.contains(state)) {
                return state
            }
            encounteredStates.add(state)
        }
    }

    fun numberOfBugs(input: String, minutes: Int = 200): Int {

        val initialState = parseInput(input)
        var states = mapOf<Int, Int>(Pair(0, initialState))

        for (i in 0 until minutes) {
            states = processRecursive(states)
        }
        return states.values.sumOf { it.occupiedCount() }
    }

    fun print(state: Int) {
        for (i in 0 until 25) {
            val mask = 1 shl i
            if (state and mask == mask) {
                print("#")
            } else {
                print(".")
            }
            if ((i + 1) % 5 == 0) {
                println()
            }
        }
    }

    private fun Int.occupiedCount(): Int {
        var state = this
        var count = 0
        while (state != 0) {
            if (state and 1 == 1) {
                count++
            }
            state = state shr 1
        }
        return count
    }

    private fun Int.isOccupied(coordinate: Coordinate): Boolean {
        val index = coordinate.y * 5 + coordinate.x
        val mask = 1 shl index
        return (this and mask) == mask
    }

    private fun Coordinate.isCenter(): Boolean {
        return this.x == 2  && this.y == 2
    }

    private fun Int.occupiedNeighbourCount(neighbours: List<Coordinate>, recursive: Boolean): Int {
        var neighbourCount = 0
        for (neighbour in neighbours) {
            if (neighbour.x in 0..4 && neighbour.y in 0..4) {
                if (recursive && neighbour.isCenter()) continue
                if (this.isOccupied(neighbour)) neighbourCount++
            }
        }
        return neighbourCount
    }

    private fun processRecursive(states: Map<Int, Int>): Map<Int, Int> {

        // For each state in the map process it
        val nextStates = mutableMapOf<Int, Int>()

        var minLevel = Int.MAX_VALUE
        var maxLevel = Int.MIN_VALUE

        for (level in states.keys) {
            val newState = process(states, level, true)
            nextStates[level] = newState

            minLevel = min(minLevel, level)
            maxLevel = max(maxLevel, level)
        }

        // Process one additional level, up and below
        for (level in listOf(minLevel - 1, maxLevel + 1)) {
            val state = process(states, level, true)
            if (state != 0) {
                nextStates[level] = state
            }
        }
        return nextStates
    }

    private val Coordinate.previousLevelNeighbours: List<Coordinate>
        get() {
            val result = mutableListOf<Coordinate>()
            if (this.y == 0) {
                // Top
                result.add(Coordinate(2, 1))
            } else if (this.y == 4) {
                // Bottom
                result.add(Coordinate(2, 3))
            }

            if (this.x == 0) {
                // Left
                result.add(Coordinate(1, 2))
            } else if (this.x == 4) {
                // Right
                result.add(Coordinate(3, 2))
            }
            return result
        }

    private val Coordinate.nextLevelNeighbours: List<Coordinate>
        get() {
            val result = mutableListOf<Coordinate>()
            if (this.x == 1 && this.y == 2) {
                // Left
                for (y in 0 until 5) {
                    result.add(Coordinate(0, y))
                }
            } else if (this.x == 3 && this.y == 2) {
                // Right
                for (y in 0 until 5) {
                    result.add(Coordinate(4, y))
                }
            } else if (this.x == 2 && this.y == 1) {
                // Top
                for (x in 0 until 5) {
                    result.add(Coordinate(x, 0))
                }
            } else if (this.x == 2 && this.y == 3) {
                // Bottom
                for (x in 0 until 5) {
                    result.add(Coordinate(x, 4))
                }
            }
            return result
        }

    private fun process(states: Map<Int, Int>, stateLevel: Int, recursive: Boolean): Int {

        var newState = 0

        val state = states[stateLevel] ?: 0
        val nextState = states[stateLevel + 1] ?: 0
        val previousState = states[stateLevel - 1] ?: 0

        for (i in 0 until 25) {

            val mask = 1 shl i

            val coordinate = Coordinate(i % 5, i / 5)

            if (recursive && coordinate.isCenter()) continue

            var neighbourCount = 0
            neighbourCount += state.occupiedNeighbourCount(coordinate.directNeighbours.toList(), recursive)

            if (recursive) {
                neighbourCount += nextState.occupiedNeighbourCount(coordinate.nextLevelNeighbours, recursive)
                neighbourCount += previousState.occupiedNeighbourCount(coordinate.previousLevelNeighbours, recursive)
            }

            val bitState = (state and mask)

            newState = if (bitState == mask && neighbourCount != 1) {
                newState and mask.inv()
            } else if (bitState == 0 && neighbourCount in 1..2) {
                newState or mask
            } else {
                newState or bitState
            }
        }

        return newState
    }

    private fun process(state: Int): Int {
        return process(mapOf(Pair(0, state)), 0, false)
    }

}