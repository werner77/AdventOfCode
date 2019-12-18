package com.behindmedia.adventofcode2019

import kotlin.math.min

class Day18 {

    data class Node(val coordinate: Coordinate, val identifier: Char) {

        val isKey: Boolean
            get() = (identifier - 'a') in 0..25

        val isDoor: Boolean
            get() = (identifier - 'A') in 0..25

        val isWall: Boolean
            get() = (identifier == '#')

        val isCurrentPosition: Boolean
            get() = (identifier == '@')

        val isEmpty: Boolean
            get() = (identifier == '.')

        fun isOpen(keys: KeyCollection): Boolean {
            return isEmpty || isKey || isCurrentPosition || keys.contains(identifier.toLowerCase())
        }

        override fun toString(): String {
            return "$identifier(${coordinate.x},${coordinate.y})"
        }
    }

    /**
     * Optimized data structure based on a bitmask to store which keys have been collected
     */
    class KeyCollection {

        var internalState = 0

        fun addKey(key: Char) {
            val shift = key - 'a'
            internalState = internalState.or( 1.shl(shift) )
        }

        fun contains(key: Char): Boolean {
            val shift = key - 'a'
            val mask = 1.shl(shift)
            return internalState.and(mask) == mask
        }

        override fun hashCode(): Int {
            return internalState.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other is KeyCollection) {
                return this.internalState == other.internalState
            }
            return false
        }

        fun plus(key: Char): KeyCollection {
            val result = KeyCollection()
            result.internalState = this.internalState
            result.addKey(key)
            return result
        }

        fun cacheKey(coordinate: Coordinate): Long {
            var result = internalState.toLong()
            result = result or coordinate.x.toLong().shl(48)
            result = result or coordinate.y.toLong().shl(32)
            return result
        }
    }

    fun getMinimumNumberOfMoves(input: String): Int {
        val map = getMap(input)
        val currentPosition = getCurrentPosition(map)
        return minimumPathToCollectKeys(currentPosition, KeyCollection(), map, mutableMapOf(), mutableMapOf())
    }

    fun minimumPathToCollectKeys(currentPosition: Coordinate,
                                 keysInPossession: KeyCollection,
                                 map: Map<Coordinate, Node>,
                                 pathLengthCache: MutableMap<Long, Int>,
                                 reachableCache: MutableMap<Long, List<CoordinatePath>>): Int {

        val reachableKeys = orderedReachableKeyPositions(currentPosition, keysInPossession, map, reachableCache)

        if (reachableKeys.isEmpty()) {
            return 0
        }

        var minPath = Int.MAX_VALUE

        for (reachableKey in reachableKeys) {

            // Move to this key
            val nextPosition = reachableKey.coordinate

            val node = map[nextPosition] ?: throw IllegalStateException("No node found at coordinate")
            assert(node.isKey)

            if ((reachableKey.pathLength + reachableKeys.size - 1) >= minPath) {
                break
            }

            val nextKeys = keysInPossession.plus(node.identifier)
            val cacheKey = nextKeys.cacheKey(nextPosition)

            val nextPathLength = pathLengthCache.getOrPut(cacheKey) {
                minimumPathToCollectKeys(nextPosition, nextKeys,
                    map, pathLengthCache, reachableCache)
            }

            minPath = min(minPath, nextPathLength + reachableKey.pathLength)
        }

        return minPath
    }

    fun getMap(input: String, includeWalls: Boolean = false): Map<Coordinate, Node> {

        val lines = input.split('\n')

        val map = mutableMapOf<Coordinate, Node>()
        for ((y, line) in lines.withIndex()) {
            for ((x, c) in line.withIndex()) {
                val coordinate = Coordinate(x, y)

                val node = Node(coordinate, c)

                if (!node.isWall || includeWalls) {
                    map[coordinate] = node
                }
            }
        }
        return map
    }

    fun getCurrentPosition(map: Map<Coordinate, Node>): Coordinate {
        return map.entries.find { it.value.isCurrentPosition }?.key ?: throw IllegalStateException("Current position not found")
    }

    /**
     * Finds the reachable nodes of the map, given the current set of keys in posession
     */
    fun orderedReachableKeyPositions(currentPosition: Coordinate, currentKeys: KeyCollection, map: Map<Coordinate, Node>, cache: MutableMap<Long, List<CoordinatePath>>): List<CoordinatePath> {
        val cacheKey = currentKeys.cacheKey(currentPosition)
        return cache.getOrPut(cacheKey) {
            val result = mutableListOf<CoordinatePath>()
            currentPosition.reachableCoordinates(currentPosition, reachable = { map[it]?.isOpen(currentKeys) ?: false }) { coordinatePath ->
                map[coordinatePath.coordinate]?.let { node ->
                    if (node.isKey && !currentKeys.contains(node.identifier)) {
                        result.add(coordinatePath)
                    }
                }
                null
            }
            result
        }
    }

    fun printMap(map: Map<Coordinate, Node>) {
        val range = map.keys.range()

        var numberOfKeys = 0
        var numberOfDoors = 0
        for (coordinate in range) {
            val state = map[coordinate] ?: Node(coordinate, '#')

            print(state.identifier)

            if (state.isDoor) {
                numberOfDoors++
            }

            if (state.isKey) {
                numberOfKeys++
            }

            if (coordinate.x == range.endInclusive.x) {
                println()
            }
        }

        println("Number of keys: $numberOfKeys")
        println("Number of doors: $numberOfDoors")
    }
}