
package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

private typealias Node = Char

// private utility method to determine the type of identifier (key, door, wall, current position or empty)
private val Node.isKey: Boolean
    inline get() = (this - 'a') in 0..25

private val Node.isDoor: Boolean
    inline get() = (this - 'A') in 0..25

private val Node.isWall: Boolean
    inline get() = (this == '#')

private val Node.isCurrentPosition: Boolean
    inline get() = (this == '@' || (this - '0') in 0..9)

private val Node.isEmpty: Boolean
    inline get() = (this == '.')

// Gets the key corresponding with the door, does not make any sense for none door nodes.
private val Node.correspondingKey: Char
    inline get() {
        assert(this.isDoor)
        return this.lowercaseChar()
    }

/**
 * Optimized data structure based on bit masks to store which keys have been collected
 */
@JvmInline
private value class KeyCollection(private val state: Int) {

    companion object {
        private const val completeState: Int = (1 shl 26) - 1

        private val Char.shift: Int
            inline get() = this - 'a'

        fun from(keys: List<Char>): KeyCollection {
            var state = 0
            for (key in keys) {
                assert(key.isKey)
                state = state or (1 shl key.shift)
            }
            return KeyCollection(state)
        }

        /**
         * All keys
         */
        val all = KeyCollection(completeState)

        /**
         * No keys
         */
        val none = KeyCollection(0)
    }

    /**
     * Whether this collection is a subset and contains less keys than the supplied instance.
     */
    fun isProperSubsetOf(other: KeyCollection): Boolean {
        return this in other && other.state != this.state
    }

    /**
     * Whether the specified key is in the collection
     */
    operator fun contains(key: Char): Boolean {
        assert(key.isKey)
        val mask = 1 shl key.shift
        return state.and(mask) == mask
    }

    /**
     * Whether this collection contains all keys in the specified collection (i.e. is a super set of)
     */
    operator fun contains(other: KeyCollection): Boolean {
        return (state and other.state) == other.state
    }

    /**
     * Adds the supplied key
     */
    operator fun plus(key: Char): KeyCollection {
        assert(key.isKey)
        return KeyCollection(this.state or (1 shl key.shift))
    }

    /**
     * Adds all keys in the supplied key collection
     */
    operator fun plus(other: KeyCollection): KeyCollection {
        return KeyCollection(state or other.state)
    }

    override fun toString(): String {
        return (0..25).mapNotNull {
            val c = 'a' + it
            if (contains(c)) c else null
        }.toString()
    }
}

/**
 * Optimized data structure to represent up to 4 nodes (current positions) with a single integer.
 */
@JvmInline
private value class NodeCollection(private val state: Int) {

    companion object {
        fun from(nodes: List<Node>): NodeCollection {
            var state = 0
            nodes.forEachIndexed { index, node ->
                val shift = shiftFor(index)
                state = state or (node.code shl shift)
            }
            return NodeCollection(state)
        }

        fun shiftFor(index: Int): Int {
            return 8 * index
        }
    }

    inline fun forEachIndexed(perform: (Int, Node) -> Unit) {
        for (i in 0..3) {
            val shift = shiftFor(i)
            val node = (state shr shift) and 0xFF
            if (node == 0) break
            perform(i, node.toChar())
        }
    }

    fun replacingNode(index: Int, node: Node): NodeCollection {
        val shift = shiftFor(index)
        var newState = state and (0xFF shl shift).inv()
        newState = newState or (node.code shl shift)
        return NodeCollection(newState)
    }
}

class Day18 {

    /**
     * Combination of node and collected (or required) keys.
     */
    private data class KeyedNode(val node: Node, val keys: KeyCollection) {
        fun isAvailable(keysInPossession: KeyCollection): Boolean {
            return keys in keysInPossession
        }
    }

    /**
     * Combination of multiple nodes (representing the different robot positions) and combination of keys in possession.
      */
    private data class KeyedNodeCollection(val nodes: NodeCollection, val keys: KeyCollection)

    /**
     * Data structure holding the map information (all the nodes that exist on the map, without any key collection info)
     */
    private class NodeMap(private val matrix: Array<Array<Node>>, private val keyNodeCoordinates: Array<Coordinate?>) {

        companion object {
            fun from(input: String): NodeMap {
                val lines = input.reader().readLines()
                val sizeY = lines.size
                val sizeX = lines.maxByOrNull { it.length }?.length ?: 0
                val matrix = Array(sizeX) {
                    Array(sizeY) {
                        '#'
                    }
                }
                val keyNodeCoordinates = Array<Coordinate?>(128) { null }
                var currentPositionIndex = 1
                for ((y, line) in lines.withIndex()) {
                    for ((x, c) in line.withIndex()) {
                        val node = if (c.isCurrentPosition) {
                            // Give each current position a unique index (1, 2, 3, 4)
                            '0' + currentPositionIndex++
                        } else {
                            c
                        }
                        if (node.isCurrentPosition || node.isKey) {
                            keyNodeCoordinates[node.code] = Coordinate(x, y)
                        }
                        matrix[x][y] = node
                    }
                }
                return NodeMap(matrix, keyNodeCoordinates)
            }
        }

        fun nodeAt(coordinate: Coordinate): Node {
            return matrix[coordinate.x][coordinate.y]
        }

        fun coordinateFor(node: Node): Coordinate {
            return keyNodeCoordinates[node.code] ?: throw IllegalArgumentException("Coordinate requested for none key node: $node")
        }

        fun isAccessible(coordinate: Coordinate, keysInPossession: KeyCollection): Boolean {
            val x = coordinate.x
            val y = coordinate.y

            if (x in matrix.indices) {
                val s = matrix[x]
                if (y in s.indices) {
                    val node = s[y]
                    val door = node.isDoor
                    return (!door && !node.isWall) || (door && keysInPossession.contains(node.lowercaseChar()))
                }
            }
            return false
        }

        inline fun getNodes(predicate: (Node) -> Boolean): List<Node> {
            val result = mutableListOf<Node>()
            for (x in matrix.indices) {
                for (y in matrix[x].indices) {
                    val node = matrix[x][y]
                    if (predicate(node)) {
                        result.add(node)
                    }
                }
            }
            return result
        }

        var pathEvaluationCount = 0
        var nodeEvaluationCount = 0

        /**
         * Method to find the paths to all keys (not necessarily the shortest) from a designated source node.
         *
         * For every unique path found the `onFound` lambda is called.
         *
         * This method uses a depth first search to traverse all possible unique paths.
         */
        private fun findAllKeyPaths(from: Node, onFound: (Path<KeyedNode>) -> Boolean) {
            fun recurse(currentCoordinate: Coordinate, currentNode: Node, visited: MutableSet<Coordinate>,
                        pathLength: Int, requiredKeys: KeyCollection) {
                if (currentNode.isKey && pathLength != 0) {
                    // Found
                    if (onFound(Path(KeyedNode(currentNode, requiredKeys), pathLength, null))) {
                        return
                    }
                }
                pathEvaluationCount++
                visited.add(currentCoordinate)
                currentCoordinate.directNeighbourSequence().forEach {
                    if (this.isAccessible(it, KeyCollection.all) && !visited.contains(it)) {
                        val neighbourNode = this.nodeAt(it)
                        val nextRequiredKeys = if (neighbourNode.isDoor) {
                            requiredKeys + neighbourNode.correspondingKey
                        } else {
                            requiredKeys
                        }
                        recurse(it, neighbourNode, visited, pathLength + 1, nextRequiredKeys)
                    }
                }
                visited.remove(currentCoordinate)
            }
            recurse(coordinateFor(from), from, HashSet(), 0, KeyCollection.none)
        }

        private val edgeCache = HashMap<Node, Collection<Path<KeyedNode>>>(32)

        private fun edgesFrom(node: Node, currentKeys: KeyCollection): Collection<Path<KeyedNode>> {
            return edgeCache.getOrPut(node) {
                val foundPaths = HashMap<Node, Path<KeyedNode>>(32)
                findAllKeyPaths(node) { nodePath ->
                    // As an optimization we only need to find keys which are not yet collected, because at the time this
                    // method is called the optimal path to those nodes will already be found using Dijkstra
                    if (!currentKeys.contains(nodePath.destination.node)) {

                        // Stop when a key is found that is not in the current collection.
                        // It does not make sense to traverse the same path beyond that,
                        // because we arrive at a different key calling this method again

                        val existingEdge = foundPaths[nodePath.destination.node]
                        if (
                            existingEdge == null || // add non-existent paths
                            nodePath.length < existingEdge.length  || // add shorter paths
                            nodePath.destination.keys.isProperSubsetOf(existingEdge.destination.keys) // less keys needed
                        ) {
                            foundPaths[nodePath.destination.node] = nodePath
                        }
                        true
                    } else {
                        false
                    }
                }
                foundPaths.values
            }
        }

        /**
         * Performs the specified lambda for each relevant edge of the specified node collection
         *
         * Relevant means that the key the edge points to has not been collected yet and that it is reachable given the keys
         * in possession.
         */
        private inline fun forEachRelevantEdge(nodeCollection: KeyedNodeCollection, perform: (Int, Path<KeyedNode>) -> Unit) {
            nodeCollection.nodes.forEachIndexed { index, subNode ->
                edgesFrom(subNode, nodeCollection.keys).forEach { edge ->
                    // Only add keys that have not yet been collected and from paths that are actually available
                    if (edge.destination.isAvailable(nodeCollection.keys) && !nodeCollection.keys.contains(edge.destination.node)) {
                        perform(index, edge)
                    }
                }
            }
        }

        /**
         * Finds the minimum path given the initial positions and keys in the graph to collect all keys.
         *
         * This uses the Dijkstra algorithm with a Fibonacci Heap as a priority queue for faster performance.
         *
         * The graph contains all info about the maze (weight of edges are lengths of paths between key nodes)
         */
        fun minimumPathToCollectAllKeys(): Int? {

            // Use a FibonacciHeap to optimize performance. A FibonacciHeap allows reordering of the priority queue without a
            // remove and subsequent add.
            // The heap is automatically ordered by NodePath ascending (increasing path length, so shortest paths are first)
            val pending = FibonacciHeap<KeyedNodeCollection>()

            // The initial positions
            val initialNodes = this.getNodes { it.isCurrentPosition }

            // This is the collection of keys to check against for completeness
            val completeKeyCollection = KeyCollection.from(this.getNodes { it.isKey })

            // The initial nodes (starting positions of the robots) in combination with an empty key collection
            val initialNodeCollection = KeyedNodeCollection(NodeCollection.from(initialNodes), KeyCollection.none)

            // Add the initial nodes to the pending queue
            pending.update(initialNodeCollection, 0)
            val settled = HashSet<KeyedNodeCollection>()

            while (true) {
                // If the queue is empty: break out of the loop
                val current = pending.poll() ?: break

                // Process the node collection with the lowest path length
                val currentNodeCollection = current.destination

                // If this collection contains all the keys we were looking for: we're done!
                if (completeKeyCollection in currentNodeCollection.keys) {
                    return current.length.toInt()
                }

                nodeEvaluationCount++

                // Add to the settled set to avoid revisiting the same node
                settled.add(currentNodeCollection)

                forEachRelevantEdge(currentNodeCollection) { nodeIndex, edge ->
                    val neighbour = KeyedNodeCollection(currentNodeCollection.nodes.replacingNode(nodeIndex, edge.destination.node),
                        currentNodeCollection.keys + edge.destination.node)
                    if (!settled.contains(neighbour)) {
                        pending.update(neighbour, current.length.toInt() + edge.length.toInt())
                    }
                }
            }
            return null
        }
    }

    /**
     * Finds the reachable nodes of the map, given the current set of keys in possession
     * Main method which gives the answer to both part 1 and part 2
     */
    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val map = NodeMap.from(input)
        val result = map.minimumPathToCollectAllKeys() ?: throw IllegalStateException("Could not find minimum path")

        println("Processed ${map.pathEvaluationCount} coordinates and ${map.nodeEvaluationCount} nodes")

        return result
    }
}