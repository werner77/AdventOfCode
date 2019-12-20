package com.behindmedia.adventofcode2019

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.commons.collections4.bag.TreeBag
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

class Day18 {

    class NodeMap {

        val size = 82

        val matrix = Array<Array<Char>>(size) {
            Array<Char>(size) { '#' }
        }

        fun nodeAt(coordinate: Coordinate): Char {
            return matrix[coordinate.x][coordinate.y]
        }

        inline fun forAccessibleNeighbours(coordinate: Coordinate, keys: KeyCollection, perform: (Coordinate) -> Unit) {
            for (neighbour in coordinate.neighbours) {
                if (neighbour.x < 0 || neighbour.y < 0 || neighbour.x >= size || neighbour.y >= size) continue
                if (nodeAt(neighbour).isAccessible(keys)) {
                    perform(neighbour)
                }
            }
        }

        fun getCurrentPositions(): List<Coordinate> {
            val result = mutableListOf<Coordinate>()
            for (x in 0 until size) {
                for (y in 0 until size) {
                    if (matrix[x][y].isCurrentPosition) {
                        result.add(Coordinate(x, y))
                    }
                }
            }
            return result
        }

        fun isKeyNode(node: Char): Boolean {
            return node.isKey
        }

        private val Char.isKey: Boolean
            inline get() = (this - 'a') in 0..25

        private val Char.isDoor: Boolean
            inline get() = (this - 'A') in 0..25

        private val Char.isWall: Boolean
            inline get() = (this == '#')

        private val Char.isCurrentPosition: Boolean
            inline get() = (this == '@')

        private val Char.isEmpty: Boolean
            inline get() = (this == '.')

        fun Char.isAccessible(keys: KeyCollection): Boolean {
            return isEmpty || isKey || isCurrentPosition || (isDoor && keys.contains(this.toLowerCase()))
        }

        fun print() {
            for (y in 0 until size) {
                for (x in 0 until size) {
                    print(matrix[x][y])
                }
                println()
            }
        }
    }

    fun getNodeMap(input: String): NodeMap {
        val lines = input.split('\n')
        val map = NodeMap()
        for ((y, line) in lines.withIndex()) {
            for ((x, c) in line.withIndex()) {
                map.matrix[x][y] = c
            }
        }
        return map
    }

    private class KeyNode(val key: Char, val coordinate: Coordinate, val pathLength: Int, val positionIndex: Int)

    private val pathComparator = kotlin.Comparator<KeyNode> { node1, node2 ->
        node1.pathLength.compareTo(node2.pathLength)
    }

    private fun minimumPath(currentPositions: List<Node>,
                            keysInPossession: KeyCollection,
                            map: NodeMap,
                            pathLengthCache: MutableMap<Long, Int>,
                            reachableCache: MutableMap<Long, List<KeyNode>>,
                            multiThreaded: Boolean = false): Int {

        val reachableKeys = TreeBag<KeyNode>(pathComparator)

        for(positionIndex in currentPositions.indices) {
            val individualReachableKeys = getReachableKeyNodes(currentPositions, positionIndex, keysInPossession, map, reachableCache)
            reachableKeys.addAll(individualReachableKeys)
        }

        if (reachableKeys.isEmpty()) {
            return 0
        }

        val minPath = AtomicInteger(Int.MAX_VALUE)

        fun processKey(key: KeyNode) {
            // Move to this key
            val nextPosition = key.coordinate

            val node = map.nodeAt(nextPosition)

            var minimumPathLengthForThisKey = key.pathLength
            for (otherKey in reachableKeys) {
                minimumPathLengthForThisKey += otherKey.coordinate.manhattenDistance(nextPosition)
            }

            if (minimumPathLengthForThisKey >= minPath.toInt()) {
                return
            }

            val nextKeys = keysInPossession.plus(node)

            val nextPositions = List(currentPositions.size) {
                if (it == key.positionIndex) Node(nextPosition, node) else currentPositions[it]
            }

            val cacheKey = nextKeys.cacheKey(nextPositions)

            val nextPathLength = pathLengthCache.getOrPut(cacheKey) {
                minimumPath(nextPositions, nextKeys, map, pathLengthCache, reachableCache)
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

    private fun getReachableKeyNodes(positions: List<Node>, positionIndex: Int, keys: KeyCollection,
                             map: NodeMap, reachableCache: MutableMap<Long, List<KeyNode>>): List<KeyNode> {

        val currentNode = positions[positionIndex]
        val cacheKey = keys.cacheKey(currentNode)
        return reachableCache.getOrPut(cacheKey) {

            val from = currentNode.coordinate
            val result = mutableListOf<KeyNode>()
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
                    val node = map.nodeAt(current.coordinate)
                    if (map.isKeyNode(node) && !keys.contains(node)) {
                        result.add(KeyNode(node, current.coordinate, current.pathLength, positionIndex))
                    }
                }

                map.forAccessibleNeighbours(current.coordinate, keys) { neighbour ->
                    if (!visited.contains(neighbour)) {
                        list.add(CoordinatePath(neighbour, current.pathLength + 1))
                    }
                }
                visited.add(current.coordinate)
            }
            return result
        }
    }

    /**
     * Class describing the items on the map
     */
    data class Node(val coordinate: Coordinate, val identifier: Char) {

        override fun toString(): String {
            return "$identifier(${coordinate.x},${coordinate.y})"
        }
    }

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
        val map = getNodeMap(input)
        val currentPositions = map.getCurrentPositions().mapIndexed { index, coordinate ->
            // Create a unique identifier for each position to be able to cache independently
            Node(coordinate, '1' + index)
        }

        return minimumPath(currentPositions, KeyCollection(), map,
            Collections.synchronizedMap(mutableMapOf()),
            Collections.synchronizedMap(mutableMapOf()), true
        )
    }

    private class CoordinatePath(val coordinate: Coordinate, val pathLength: Int)
}