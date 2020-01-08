package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.NodePath
import com.behindmedia.adventofcode.common.printMap
import java.util.*
import kotlin.math.min

// private utility method to determine the type of identifier (key, door, wall, current position or empty)
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
         * Whether this collection contains all keys
         */
        val isComplete: Boolean
            get() = state == completeState

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
     * Class describing a point on the map with its identifier and coordinate
     */
    private data class Node(val identifier: Char, val coordinate: Coordinate) {
        fun isAccessible(keysInPossession: KeyCollection): Boolean {
            val door = identifier.isDoor
            return (!door && !identifier.isWall) || (door && keysInPossession.contains(identifier.toLowerCase()))
        }
    }

    /**
     * Combination of node and collected (or required) keys.
     */
    private data class KeyedNode(val node: Node, val keys: KeyCollection)

    /**
     * Combination of multiple nodes (representing the different droid positions) and combination of keys in possession.
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
    private class NodeMap(private val storage: Map<Coordinate, Node>) {

        companion object {
            fun from(input: String): NodeMap {
                val storage = mutableMapOf<Coordinate, Node>()
                val lines = input.split('\n')
                for ((y, line) in lines.withIndex()) {
                    for ((x, c) in line.withIndex()) {
                        if (!c.isWall) {
                            val coordinate = Coordinate(x, y)
                            storage[coordinate] = Node(c, coordinate)
                        }
                    }
                }
                return NodeMap(storage)
            }
        }

        fun nodeAt(coordinate: Coordinate): Node {
            return storage[coordinate] ?: throw IllegalStateException("Invalid coordinate supplied")
        }

        fun isAccessible(coordinate: Coordinate, keysInPossession: KeyCollection): Boolean {
            val node = storage[coordinate] ?: return false
            return node.isAccessible(keysInPossession)
        }

        inline fun getNodes(predicate: (Node) -> Boolean): List<Node> {
            return storage.values.filter { predicate(it) }
        }

        fun print() {
            storage.printMap('#')
        }
    }

    /**
     * Method to find the paths to all keys (not necessarily the shortest) from a designated source node.
     *
     * For every unique path found the `onFound` lambda is called.
     *
     * This method uses a depth first search to traverse all possible unique paths.
     */
    private fun findAllKeyPaths(from: Node, map: NodeMap, onFound: (NodePath<KeyedNode>) -> Unit) {
        fun recurse(current: Node, visited: MutableSet<Coordinate>, pathLength: Int, requiredKeys: KeyCollection) {
            if (current.identifier.isKey && pathLength != 0) {
                // Found
                onFound(NodePath(KeyedNode(current, requiredKeys), pathLength))
            }
            val currentCoordinate = current.coordinate
            visited.add(currentCoordinate)
            currentCoordinate.forDirectNeighbours {
                if (map.isAccessible(it, KeyCollection.all) && !visited.contains(it)) {
                    val neighbourNode = map.nodeAt(it)
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
        recurse(from, mutableSetOf(), 0, KeyCollection.none)
    }

    /**
     * Creates a weighted graph of all paths that exist between key nodes on the map. The nodes include all keys and the
     * initial position(s).
     *
     * Returned is a map with the Node as key and all reachable edges (paths) from this node as value.
     */
    private fun constructWeightedGraph(input: String): Map<Node, List<Edge>> {
        val map = NodeMap.from(input)
        val result = mutableMapOf<Node, MutableList<Edge>>()

        for (baseNode in map.getNodes { it.identifier.isKey || it.identifier.isCurrentPosition }) {
            val foundPaths = mutableMapOf<KeyedNode, Int>()
            findAllKeyPaths(baseNode, map) { nodePath ->
                foundPaths[nodePath.item] = min(nodePath.pathLength, foundPaths[nodePath.item] ?: Int.MAX_VALUE)
            }
            val edges = result.getOrPut(baseNode) { mutableListOf() }
            edges.addAll(foundPaths.map { Edge(baseNode, it.key.node, it.value, it.key.keys) })
        }
        return result
    }

    /**
     * Returns all the neighbours of the nodes in the specified node collection in a weighted graph.
     *
     * Returned are all possible paths to other key nodes with a weight (path length) attached.
     */
    private fun weightedNeighboursForNodes(nodeCollection: KeyedNodeCollection, graph: Map<Node, List<Edge>>): List<Pair<KeyedNodeCollection, Int>> {
        return nodeCollection.nodes.foldIndexed(mutableListOf()) { index, list, subNode ->
            val subEdges = graph[subNode] ?: throw IllegalStateException("Could not find specified subNode ${subNode} in graph")
            subEdges.forEach { edge ->
                if (edge.isAvailable(nodeCollection.keys)) {
                    val nextList = nodeCollection.nodes.toMutableList()
                    nextList[index] = edge.to
                    val nextNode = KeyedNodeCollection(nextList, nodeCollection.keys + edge.to.identifier)
                    list.add(Pair(nextNode, edge.weight))
                }
            }
            list
        }
    }

    /**
     * Finds the minimum path given the initial positions and keys in the graph to collect all keys.
     */
    private fun minimumPath(graph: Map<Node, List<Edge>>): Int? {
        val pending = PriorityQueue<NodePath<KeyedNodeCollection>>()
        val initialNodes = graph.keys.filter {
            it.identifier.isCurrentPosition
        }
        val completeCollection = KeyCollection.from(graph.keys.map { it.identifier }.filter { it.isKey })
        val initialNodeCollection = KeyedNodeCollection(initialNodes, KeyCollection.none)
        pending.add(NodePath(initialNodeCollection, 0))
        val settled = mutableSetOf<KeyedNodeCollection>()
        while (true) {
            val current = pending.poll() ?: break
            val currentNodeCollection = current.item
            if (settled.contains(currentNodeCollection)) continue

            if (currentNodeCollection.keys.containsAll(completeCollection)) {
                return current.pathLength
            }
            settled.add(currentNodeCollection)
            for ((neighbour, neighbourWeight) in weightedNeighboursForNodes(currentNodeCollection, graph)) {
                if (!settled.contains(neighbour)) {
                    val newDistance = current.pathLength + neighbourWeight
                    pending.add(NodePath(neighbour, newDistance))
                }
            }
        }
        return null
    }

    /**
     * Main method which gives the answer to both part 1 and part 2
     */
    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val graph = constructWeightedGraph(input)
        return minimumPath(graph) ?: throw IllegalStateException("Could not find minimum path")
    }
}