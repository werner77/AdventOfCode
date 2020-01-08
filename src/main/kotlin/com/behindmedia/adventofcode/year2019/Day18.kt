package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.min

// private utility method to determine the type of identifier (key, door, wall, current position or empty)
private val Char.isKey: Boolean
    inline get() = (this - 'a') in 0..25

private val Char.isDoor: Boolean
    inline get() = (this - 'A') in 0..25

private val Char.isWall: Boolean
    inline get() = (this == '#')

private val Char.isCurrentPosition: Boolean
    inline get() = (this == '@' || (this - '0') in 0..9)

private val Char.isEmpty: Boolean
    inline get() = (this == '.')

// Gets the key corresponding with this door
private fun Char.correspondingKey(): Char {
    assert(this.isDoor)
    return this.toLowerCase()
}

class Day18 {

    /**
     * Optimized data structure based on bit masks to store which keys have been collected
     */
    private data class KeyCollection(private val state: Int) {

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
         * Whether the specified key is in the collection
         */
        fun contains(key: Char): Boolean {
            assert(key.isKey)
            val mask = 1 shl key.shift
            return state.and(mask) == mask
        }

        /**
         * Whether this collection contains all keys in the specified collection (i.e. is a super set of)
         */
        fun containsAll(other: KeyCollection): Boolean {
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
     * Class describing a point on the map with its identifier and coordinate.
     *
     * Each node is uniquely identified by its identifier.
     */
    private data class Node(val identifier: Char, val coordinate: Coordinate) {

        fun isAccessible(keysInPossession: KeyCollection): Boolean {
            val door = identifier.isDoor
            return (!door && !identifier.isWall) || (door && keysInPossession.contains(identifier.toLowerCase()))
        }

        override fun equals(other: Any?): Boolean {
            return if (other is Node) identifier == other.identifier else false
        }

        override fun hashCode(): Int {
            return identifier.hashCode()
        }
    }

    /**
     * Combination of node and collected (or required) keys.
     */
    private data class KeyedNode(val node: Node, val keys: KeyCollection)

    /**
     * Combination of multiple nodes (representing the different robot positions) and combination of keys in possession.
      */
    private data class KeyedNodeCollection(val nodes: List<Node>, val keys: KeyCollection)

    /**
     * Path from a node to a target node (not necessarily the shortest).
     *
     * The weight is the path length and the requiredKeys is the collection of keys in possession required to be able
     * to traverse this path.
     */
    private data class Edge(val from: Node, val to: Node, val weight: Int, val requiredKeys: KeyCollection) {
        fun isAvailable(keysInPossession: KeyCollection): Boolean {
            return keysInPossession.containsAll(requiredKeys)
        }
    }

    /**
     * Data structure holding the map information (all the nodes that exist on the map, without any key collection info)
     */
    private class NodeMap(private val matrix: Array<Array<Node>>) {

        companion object {
            fun from(input: String): NodeMap {
                val lines = input.split('\n')

                val sizeY = lines.size
                val sizeX = lines.map { it.length }.max() ?: 0

                val matrix = Array(sizeX) { x ->
                    Array(sizeY) { y ->
                        Node('#', Coordinate(x, y))
                    }
                }

                var currentPositionIndex = 1
                for ((y, line) in lines.withIndex()) {
                    for ((x, c) in line.withIndex()) {
                        val coordinate = Coordinate(x, y)
                        matrix[x][y] = if (c.isCurrentPosition) {
                            // Give each current position a unique index (1, 2, 3, 4)
                            Node('0' + currentPositionIndex++, coordinate)
                        } else {
                            Node(c, coordinate)
                        }
                    }
                }
                return NodeMap(matrix)
            }
        }

        fun nodeAt(coordinate: Coordinate): Node {
            return matrix[coordinate.x][coordinate.y]
        }

        fun isAccessible(coordinate: Coordinate, keysInPossession: KeyCollection): Boolean {
            val x = coordinate.x
            val y = coordinate.y

            if (x in matrix.indices) {
                val s = matrix[x]
                if (y in s.indices) {
                    val node = s[y]
                    return node.isAccessible(keysInPossession)
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

        /**
         * Method to find the paths to all keys (not necessarily the shortest) from a designated source node.
         *
         * For every unique path found the `onFound` lambda is called.
         *
         * This method uses a depth first search to traverse all possible unique paths.
         */
        private fun findAllKeyPaths(from: Node, onFound: (Path<KeyedNode>) -> Unit) {
            fun recurse(current: Node, visited: MutableSet<Coordinate>, pathLength: Int, requiredKeys: KeyCollection) {
                if (current.identifier.isKey && pathLength != 0) {
                    // Found
                    onFound(Path(KeyedNode(current, requiredKeys), pathLength))
                }
                val currentCoordinate = current.coordinate
                visited.add(currentCoordinate)
                currentCoordinate.forDirectNeighbours {
                    if (this.isAccessible(it, KeyCollection.all) && !visited.contains(it)) {
                        val neighbourNode = this.nodeAt(it)
                        val nextRequiredKeys = if (neighbourNode.identifier.isDoor) {
                            requiredKeys + neighbourNode.identifier.correspondingKey()
                        } else {
                            requiredKeys
                        }
                        recurse(neighbourNode, visited, pathLength + 1, nextRequiredKeys)
                    }
                }
                visited.remove(currentCoordinate)
            }
            recurse(from, HashSet(), 0, KeyCollection.none)
        }

        /**
         * Creates a weighted graph of all paths that exist between key nodes on the map. The nodes include all keys and the
         * initial position(s).
         *
         * Returned is a map with the Node as key and all reachable edges (paths) from this node as value.
         */
        fun asWeightedGraph(): Map<Node, List<Edge>> {
            val result = HashMap<Node, MutableList<Edge>>()

            for (baseNode in this.getNodes { it.identifier.isKey || it.identifier.isCurrentPosition }) {
                val foundPaths = HashMap<KeyedNode, Int>()
                findAllKeyPaths(baseNode) { nodePath ->
                    foundPaths[nodePath.destination] = min(nodePath.pathLength, foundPaths[nodePath.destination] ?: Int.MAX_VALUE)
                }
                val edges = result.getOrPut(baseNode) { ArrayList(26) }
                edges.addAll(foundPaths.map { Edge(baseNode, it.key.node, it.value, it.key.keys) })
            }
            return result
        }
    }

    /**
     * Performs the specified lambda for each relevant edge of the specified node collection
     *
     * Relevant means that the key the edge points to has not been collected yet and that it is reachable given the keys
     * in possession.
     */
    private inline fun Map<Node, List<Edge>>.forEachRelevantEdge(nodeCollection: KeyedNodeCollection, perform: (Int, Edge) -> Unit) {
        nodeCollection.nodes.forEachIndexed { index, subNode ->
            val subEdges = this[subNode] ?: throw IllegalStateException("Could not find specified subNode ${subNode} in graph")
            subEdges.forEach { edge ->
                // Only add keys that have not yet been collected and from paths that are actually available
                if (edge.isAvailable(nodeCollection.keys) && !nodeCollection.keys.contains(edge.to.identifier)) {
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
    private fun Map<Node, List<Edge>>.minimumPathToCollectAllKeys(): Int? {
        // Use a FibonacciHeap to optimize performance. A FibonacciHeap allows reordering of the priority queue without a
        // remove and subsequent add.
        // The heap is automatically ordered by NodePath ascending (increasing path length, so shortest paths are first)
        val pending = FibonacciHeap<KeyedNodeCollection>()

        // The initial positions
        val initialNodes = this.keys.filter {
            it.identifier.isCurrentPosition
        }

        // This is the collection of keys to check against for completeness
        val completeKeyCollection = KeyCollection.from(this.keys.map { it.identifier }.filter { it.isKey })

        // The initial nodes (starting positions of the robots) in combination with an empty key collection
        val initialNodeCollection = KeyedNodeCollection(initialNodes, KeyCollection.none)

        // Add the initial nodes to the pending queue
        pending.update(initialNodeCollection, 0)
        val settled = HashSet<KeyedNodeCollection>()

        while (true) {
            // If the queue is empty: break out of the loop
            val current = pending.poll() ?: break

            // Process the node collection with the lowest path length
            val currentNodeCollection = current.destination

            // If this collection contains all the keys we were looking for: we're done!
            if (currentNodeCollection.keys.containsAll(completeKeyCollection)) {
                return current.pathLength
            }

            // Add to the settled set to avoid revisiting the same node
            settled.add(currentNodeCollection)

            forEachRelevantEdge(currentNodeCollection) { nodeIndex, edge ->
                val nextList = currentNodeCollection.nodes.toMutableList()
                nextList[nodeIndex] = edge.to
                val neighbour = KeyedNodeCollection(nextList, currentNodeCollection.keys + edge.to.identifier)
                if (!settled.contains(neighbour)) {
                    pending.update(neighbour, current.pathLength + edge.weight)
                }
            }
        }
        return null
    }

    /**
     * Main method which gives the answer to both part 1 and part 2
     */
    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val graph = NodeMap.from(input).asWeightedGraph()
        return graph.minimumPathToCollectAllKeys() ?: throw IllegalStateException("Could not find minimum path")
    }
}