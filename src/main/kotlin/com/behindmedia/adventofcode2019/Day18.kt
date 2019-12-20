package com.behindmedia.adventofcode2019

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class Day18 {

    /**
     * Class describing the items on the map
     */
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

        /**
         * Whether the node is accessible given the supplied keys collection (representing the keys in possession)
         */
        fun isAccessible(keys: KeyCollection): Boolean {
            return isEmpty || isKey || isCurrentPosition || keys.contains(identifier.toLowerCase())
        }

        override fun toString(): String {
            return "$identifier(${coordinate.x},${coordinate.y})"
        }
    }

    data class IndexedNodePath(val node: Node, val pathLength: Int, val positionIndex: Int)

    /**
     * Optimized data structure based on a bitmask to store which keys have been collected
     */
    class KeyCollection {

        private var internalState = 0

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

        /**
         * Optimized cache key which stores the current node and internalState of this instance in a single Long
         */
        fun cacheKey(currentNode: Node): Long {
            var result = internalState.toLong()
            result = result or currentNode.identifier.toLong().shl(32)
            return result
        }

        fun cacheKey(currentNodes: List<Node>): Long {
            var result = internalState.toLong()

            assert(currentNodes.size <= 4)
            var bitCount = 64

            for (node in currentNodes) {
                bitCount -= 8
                result = result or node.identifier.toLong().shl(bitCount)
            }
            return result
        }
    }

    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val map = getMap(input, includeWalls = false)
        val currentPositions = map.getCurrentPositions().mapIndexed { index, node ->
            // Create a unique identifier for each position to be able to cache independently
            Node(node.coordinate, '1' + index)
        }

        return minimumPathToCollectKeys(currentPositions, KeyCollection(), map,
            Collections.synchronizedMap(mutableMapOf()),
            Collections.synchronizedMap(mutableMapOf()), true)
    }

    private fun Map<Coordinate, Node>.getCurrentPositions(): List<Node> {
        return values.filter { it.isCurrentPosition }
    }

    private class CoordinatePath(val coordinate: Coordinate, val pathLength: Int)

    private inline fun findReachableKeys(from: Coordinate, index: Int, keys: KeyCollection, map: Map<Coordinate, Node>):
            List<IndexedNodePath> {

        val result = mutableListOf<IndexedNodePath>()
        val list = ArrayDeque<CoordinatePath>()
        val visited = mutableSetOf<Coordinate>()
        visited.add(from)
        list.add(CoordinatePath(from, 0))

        while(true) {
            val current = try {
                list.pop()
            } catch (e: NoSuchElementException) {
                break
            }
            if (current.coordinate != from) {
                val node = map[current.coordinate]
                if (node != null) {
                    if (node.isKey && !keys.contains(node.identifier)) {
                        result.add(IndexedNodePath(node, current.pathLength, index))
                    }
                }
            }
            for (neighbour in current.coordinate.neighbours) {
                if (!visited.contains(neighbour) && map[neighbour]?.isAccessible(keys) == true) {
                    list.add(CoordinatePath(neighbour, current.pathLength + 1))
                }
            }
            visited.add(current.coordinate)
        }
        return result
    }

    /**
     * Recursive function to determine the minimumPathLength given the list of currentPositions of all robots
     * and the supplied keys in possession.
     */
    private fun minimumPathToCollectKeys(currentPositions: List<Node>,
                                 keysInPossession: KeyCollection,
                                 map: Map<Coordinate, Node>,
                                 pathLengthCache: MutableMap<Long, Int>,
                                 reachableCache: MutableMap<Long, List<IndexedNodePath>>,
                                    multiThreaded: Boolean = false): Int {

        val reachableKeys = mutableListOf<IndexedNodePath>()

        for(i in currentPositions.indices) {
            val individualReachableKeys = getReachableKeys(currentPositions[i], i, keysInPossession, map, reachableCache)
            reachableKeys.addAll(individualReachableKeys)
        }

        if (reachableKeys.isEmpty()) {
            return 0
        }

        val minPath = AtomicInteger(Int.MAX_VALUE)

        fun processKey(key: IndexedNodePath) {
            // Move to this key
            val nextPosition = key.node.coordinate

            val node = map[nextPosition] ?: throw IllegalStateException("No node found at coordinate")
            assert(node.isKey)

            // This is the lower bound on path needed, every additional key would take at least the
            // manhattenDistance to collect
            var minimumPathLengthForThisKey = key.pathLength
            for (otherKey in reachableKeys) {
                minimumPathLengthForThisKey += otherKey.node.coordinate.manhattenDistance(nextPosition)
            }

            if (minimumPathLengthForThisKey >= minPath.toInt()) {
                return
            }

            val nextKeys = keysInPossession.plus(node.identifier)

            val nextPositions = List(currentPositions.size) {
                if (it == key.positionIndex) node else currentPositions[it]
            }

            val cacheKey = nextKeys.cacheKey(nextPositions)

            val nextPathLength = pathLengthCache.getOrPut(cacheKey) {
                minimumPathToCollectKeys(nextPositions, nextKeys,
                    map, pathLengthCache, reachableCache)
            }

            minPath.getAndUpdate { current ->
                min(current, nextPathLength + key.pathLength)
            }
        }

        if (multiThreaded) {
            runBlocking {
                withContext(Dispatchers.Default) {
                    for (reachableKey in reachableKeys) {
                        launch {
                            processKey(reachableKey)
                        }
                    }
                }
            }
        } else {
            for (reachableKey in reachableKeys) {
                processKey(reachableKey)
            }
        }
        return minPath.toInt()
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

    /**
     * Finds the reachable nodes of the map, given the current set of keys in posession
     */
    private fun getReachableKeys(currentPosition: Node, index: Int, keysInPossession: KeyCollection, map: Map<Coordinate, Node>, cache: MutableMap<Long, List<IndexedNodePath>>): List<IndexedNodePath> {
        val cacheKey = keysInPossession.cacheKey(currentPosition)
        return cache.getOrPut(cacheKey) {
            findReachableKeys(currentPosition.coordinate, index, keysInPossession, map)
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