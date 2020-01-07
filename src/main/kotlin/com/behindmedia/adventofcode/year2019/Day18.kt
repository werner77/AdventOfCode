package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.CoordinatePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class Day18 {

    /**
     * Class describing the items on the map. The identifier is the character describing the item.
     */
    private data class Node(val coordinate: Coordinate, val identifier: Char) {

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

    /**
     * Optimized data structure based on bitmasks to store which keys have been collected
     */
    private class KeyCollection(initialState: Int = 0) {

        companion object {
            private const val completeState: Int = (1 shl 26) - 1
        }

        val isComplete: Boolean
            get() = internalState == completeState

        private var internalState = initialState

        private fun addKey(key: Char) {
            val shift = key - 'a'
            assert(shift in 0..25)
            internalState = internalState.or( 1.shl(shift) )
        }

        fun contains(key: Char): Boolean {
            val shift = key - 'a'
            assert(shift in 0..25)
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

        /**
         * Adds the supplied key to this key collection
         */
        operator fun plus(key: Char): KeyCollection {
            assert(key - 'a' in 0..25)
            val result = KeyCollection(this.internalState)
            result.addKey(key)
            return result
        }

        /**
         * Optimized cache key which stores the current node
         * (the identifier of the current position which is either a key or the initial position: '@')
         * and internalState of this instance in a single Long.
         *
         * The identifier of a the node supplied is stored in the higher order bits (32+) while the internal state of
         * the key collection is stored in the lower order bits (<32)
         */
        fun cacheKey(currentNode: Node): Long {
            var result = internalState.toLong()
            result = result or currentNode.identifier.toLong().shl(32)
            return result
        }

        /**
         * Optimized cache key which stores the current node and internalState of this instance in a single Long.
         *
         * The identifier of a the nodes supplied is stored in the higher order bits (32+) while the internal state of
         * the key collection is stored in the lower order bits (<32).
         * The initial positions for the 4 drones should be uniquely encoded (e.g. as '1', '2', '3', '4' to avoid conflicts).
         *
         * The list of currentNodes should be of size <= 4.
         */
        fun cacheKey(currentNodes: List<Node>): Long {
            assert(currentNodes.size <= 4)
            var result = internalState.toLong()
            var bitCount = 64
            for (node in currentNodes) {
                bitCount -= 8
                result = result or node.identifier.toLong().shl(bitCount)
            }
            return result
        }
    }

    private fun parseMap(input: String): Map<Coordinate, Node> {
        val lines = input.split('\n')
        val map = mutableMapOf<Coordinate, Node>()
        for ((y, line) in lines.withIndex()) {
            for ((x, c) in line.withIndex()) {
                val coordinate = Coordinate(x, y)
                val node = Node(coordinate, c)
                if (!node.isWall) {
                    map[coordinate] = node
                }
            }
        }
        return map
    }

    private fun Map<Coordinate, Node>.getCurrentPositions(): List<Node> {
        return values.filter { it.isCurrentPosition }.mapIndexed { index, node ->
            // Create a unique identifier (different than all the keys) for each position to be able to cache independently
            Node(node.coordinate, '1' + index)
        }
    }

    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val map = parseMap(input)
        val currentPositions = map.getCurrentPositions()

        // Call the recursive function to find all minimum paths. The top level is multithreaded.
        return minimumPathToCollectKeys(
            currentPositions,
            KeyCollection(),
            map,
            Collections.synchronizedMap(mutableMapOf()),
            Collections.synchronizedMap(mutableMapOf()),
            true)
    }

    /**
     * Recursive function to determine the minimumPathLength given the list of currentPositions of all robots
     * and the supplied keys in possession.
     */
    private fun minimumPathToCollectKeys(currentPositions: List<Node>,
                                         keysInPossession: KeyCollection,
                                         map: Map<Coordinate, Node>,
                                         pathLengthCache: MutableMap<Long, Int>,
                                         reachableCache: MutableMap<Long, List<CoordinatePath>>,
                                         multiThreaded: Boolean = false): Int {

        val reachableKeys: List<Pair<Int, CoordinatePath>> = currentPositions.foldIndexed(mutableListOf()) { index, list, node ->
            val individualReachableKeys = getReachableKeys(node, keysInPossession, map, reachableCache)
            list.addAll(individualReachableKeys.map { Pair(index, it) })
            list
        }

        if (reachableKeys.isEmpty()) {
            return 0
        }

        val minPath = AtomicInteger(Int.MAX_VALUE)
        if (multiThreaded) {
            runBlocking {
                withContext(Dispatchers.Default) {
                    for (reachableKey in reachableKeys) {
                        launch {
                            processKey(
                                reachableKey.first,
                                reachableKey.second,
                                keysInPossession,
                                currentPositions,
                                reachableKeys,
                                minPath,
                                map,
                                pathLengthCache,
                                reachableCache
                            )
                        }
                    }
                }
            }
        } else {
            for (reachableKey in reachableKeys) {
                processKey(
                    reachableKey.first,
                    reachableKey.second,
                    keysInPossession,
                    currentPositions,
                    reachableKeys,
                    minPath,
                    map,
                    pathLengthCache,
                    reachableCache
                )
            }
        }
        return minPath.toInt()
    }

    private fun processKey(
        currentPositionIndex: Int,
        keyPath: CoordinatePath,
        keysInPossession: KeyCollection,
        currentPositions: List<Node>,
        reachableKeys: Collection<Pair<Int, CoordinatePath>>,
        minPath: AtomicInteger,
        map: Map<Coordinate, Node>,
        pathLengthCache: MutableMap<Long, Int>,
        reachableCache: MutableMap<Long, List<CoordinatePath>>
    ) {

        // Move to this key
        val nextPosition = keyPath.coordinate
        val node = map[nextPosition] ?: throw IllegalStateException("No node found at coordinate $nextPosition")

        var minimumProjectedPath = keyPath.pathLength
        for (otherKey in reachableKeys) {
            val baseNode = if (otherKey.first == currentPositionIndex) {
                node
            } else {
                currentPositions[otherKey.first]
            }
            minimumProjectedPath += baseNode.coordinate.manhattenDistance(otherKey.second.coordinate)
            if (minimumProjectedPath >= minPath.toInt()) {
                return
            }
        }

        val nextKeys = keysInPossession.plus(node.identifier)

        val nextPositions = List(currentPositions.size) {
            if (it == currentPositionIndex) node else currentPositions[it]
        }

        val cacheKey = nextKeys.cacheKey(nextPositions)

        // Do a recursive call
        val nextPathLength = pathLengthCache.getOrPut(cacheKey) {
            minimumPathToCollectKeys(nextPositions, nextKeys, map, pathLengthCache, reachableCache)
        }

        minPath.getAndUpdate { current ->
            min(current, nextPathLength + keyPath.pathLength)
        }
    }


    /**
     * Finds the reachable nodes of the map, given the current set of keys in possession
     */
    private fun getReachableKeys(currentPosition: Node,
                         keysInPossession: KeyCollection,
                         map: Map<Coordinate, Node>,
                         cache: MutableMap<Long, List<CoordinatePath>>
    ): List<CoordinatePath> {
        val cacheKey = keysInPossession.cacheKey(currentPosition)
        return cache.getOrPut(cacheKey) {
            val result = mutableListOf<CoordinatePath>()
            var keys = keysInPossession
            currentPosition.coordinate.reachableCoordinates(
                reachable = { coordinate ->
                    map[coordinate]?.isAccessible(keysInPossession) ?: false
                },
                process= { coordinatePath ->
                    map[coordinatePath.coordinate]?.let { node ->
                        if (node.isKey && !keys.contains(node.identifier)) {
                            result.add(coordinatePath)
                            keys += node.identifier
                        }
                    }
                    if (keys.isComplete) true else null
                }
            )
            result
        }
    }
}