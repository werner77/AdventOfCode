package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.NodePath
import java.util.*
import kotlin.collections.ArrayList

private const val completeMask: Int = (1 shl 26) - 1

private fun Int.containsKey(key: Char): Boolean {
    val shift = key - 'a'
    val mask = 1.shl(shift)
    return this.and(mask) == mask
}

private fun Int.addKey(key: Char): Int {
    val shift = key - 'a'
    return this.or( 1.shl(shift) )
}

private val Int.isComplete: Boolean
    inline get() = this.and(completeMask) == completeMask

private fun Int.setIdentifier(identifier: Int): Int {
    assert(identifier < 32)
    return (this and completeMask) or (identifier shl 26)
}

private fun Int.isSuperSetOf(other: Int): Boolean {
    return (other and this) == other
}

private val Int.identifier: Int
    inline get() = this.shr(26)

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

private fun Char.isAccessible(keys: Int): Boolean {
    val door = isDoor
    return (!door && !isWall) || (door && keys.containsKey(this.toLowerCase()))
}

private typealias KeyNodePath = NodePath<Int>

class Day18a {

    data class Edge(val to: Int, val weight: Int, val requiredKeys: Int) {
        fun isAvailable(currentKeys: Int): Boolean {
            return currentKeys.isSuperSetOf(requiredKeys)
        }
    }

    fun findAllPaths(from: Coordinate, map: NodeMap, onFound: (NodePath<Pair<Char, Int>>) -> Unit) {
        fun recurse(current: Coordinate, visited: MutableSet<Coordinate>, pathLength: Int, requiredKeys: Int) {
            val node = map.nodeAt(current)
            if (node.isKey && pathLength != 0) {
                // Found
                onFound(NodePath(Pair(node, requiredKeys), pathLength))
                return
            }
            visited.add(current)
            current.forDirectNeighbours {
                if (map.isAccessible(it, completeMask) && !visited.contains(it)) {
                    val neighbourNode = map.nodeAt(it)
                    val nextRequiredKeys = if (neighbourNode.isDoor) {
                        requiredKeys.addKey(neighbourNode.toLowerCase())
                    } else {
                        requiredKeys
                    }
                    recurse(it, visited, pathLength + 1, nextRequiredKeys)
                }
            }
            visited.remove(current)
        }
        recurse(from, mutableSetOf(), 0, 0)
    }

    fun constructWeightedMap(input: String): Map<Int, List<Edge>> {
        val map = NodeMap.from(input)
        val result = mutableMapOf<Int, MutableList<Edge>>()
        for (key in map.keyIndices) {
            val edges = result.getOrPut(key) { mutableListOf() }
            val keyCoordinate = map.keyCoordinate(key) ?: throw IllegalStateException("Could not find coordinate for key")
            findAllPaths(keyCoordinate, map) { nodePath ->
                val (keyNode, requiredKeys) = nodePath.node
                edges.add(Edge(keyNode - 'a', nodePath.pathLength, requiredKeys))
            }
        }
        return result
    }

    class NodeMap {
        companion object {
            fun from(input: String): NodeMap {
                var currentPositionIndex = 0
                val lines = input.split('\n')
                val map = NodeMap()
                for ((y, line) in lines.withIndex()) {
                    for ((x, c) in line.withIndex()) {
                        map.matrix[x][y] = c
                        if (c.isKey) {
                            map.keyCoordinates[c - 'a'] = Coordinate(x, y)
                        } else if (c.isCurrentPosition) {
                            map.keyCoordinates[26 + currentPositionIndex++] = Coordinate(x, y)
                        }
                    }
                }
                map.numberOfCurrentPositions = currentPositionIndex
                return map
            }
        }

        private val size = 82
        private var numberOfCurrentPositions = 0

        private val matrix = Array(size) {
            Array(size) { '#' }
        }

        private val keyCoordinates = Array<Coordinate?>(32) {
            null
        }

        val keyIndices: List<Int>
            get() = keyCoordinates.mapIndexedNotNull { index, coordinate -> if (coordinate == null) null else index }

        fun nodeAt(coordinate: Coordinate): Char {
            return matrix[coordinate.x][coordinate.y]
        }

        fun isAccessible(coordinate: Coordinate, keys: Int): Boolean {
            return (coordinate.x in 0 until size && coordinate.y in 0 until size && nodeAt(coordinate).isAccessible(keys))
        }

        fun getInitialNodes(): List<Int> {
            val result = mutableListOf<Int>()
            for (x in 0 until numberOfCurrentPositions) {
                result.add(0.setIdentifier(26 + x))
            }
            return result
        }

        fun keyCoordinate(keyIndex: Int): Coordinate? {
            return keyCoordinates[keyIndex]
        }

        fun isKeyNode(node: Char): Boolean {
            return node.isKey
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

    private fun weightedNeighboursForNode(node: Int, graph: Map<Int, List<Edge>>): List<Pair<Int, Int>> {
        val edges = graph[node.identifier] ?: throw IllegalStateException("Could not find node in graph")
        return edges.mapNotNull { edge ->
            val keyNode: Char = 'a' + edge.to
            assert(edge.to in 0..25)
            if (node.isSuperSetOf(edge.requiredKeys) && !node.containsKey(keyNode)) Pair(node.addKey(keyNode).setIdentifier(edge.to), edge.weight) else null
        }
    }

    private fun minimumPath(graph: Map<Int, List<Edge>>): Int? {
        var count = 0
        val pending = PriorityQueue<KeyNodePath>()
        pending.add(KeyNodePath(0.setIdentifier(26), 0))
        val settled = mutableSetOf<Int>()
        while (true) {
            val current = pending.poll() ?: break
            val currentNode = current.node
            if (settled.contains(currentNode)) continue
            if (current.node.isComplete) {
                println("Number of iterations: $count")
                return current.pathLength
            }
            settled.add(currentNode)
            count++
            for ((neighbour, neighbourWeight) in weightedNeighboursForNode(currentNode, graph)) {
                if (!settled.contains(neighbour)) {
                    val newDistance = current.pathLength + neighbourWeight
                    pending.add(KeyNodePath(neighbour, newDistance))
                }
            }
        }
        return null
    }

    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val graph = constructWeightedMap(input)
        return minimumPath(graph) ?: throw IllegalStateException("Could not find minimum path")
    }
}