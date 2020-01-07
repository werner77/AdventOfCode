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

    private fun weightedNeighboursForNode(node: Int, map: NodeMap): List<Pair<Int, Int>> {
        val result = ArrayList<Pair<Int, Int>>(26)
        var collectedKeys = node
        val nodeCoordinate = map.keyCoordinate(node.identifier) ?: throw IllegalStateException("Invalid node identifier: ${node.identifier}")

        nodeCoordinate.reachableCoordinates(reachable = {
            map.isAccessible(it, node)
        }, process = {
            val c = map.nodeAt(it.coordinate)
            if (map.isKeyNode(c) && !collectedKeys.containsKey(c)) {
                result.add(Pair(node.addKey(c).setIdentifier(c - 'a'), it.pathLength))
                collectedKeys = collectedKeys.addKey(c)
            }
            if (collectedKeys.isComplete) true else null
        })
        return result
    }

    private fun minimumPath(initialNodes: List<Int>, map: NodeMap): Int? {
        var count = 0
        val pending = PriorityQueue<KeyNodePath>()
        for (initialNode in initialNodes) {
            pending.add(KeyNodePath(initialNode, 0))
        }
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
            for ((neighbour, neighbourWeight) in weightedNeighboursForNode(currentNode, map)) {
                if (!settled.contains(neighbour)) {
                    val newDistance = current.pathLength + neighbourWeight
                    pending.add(KeyNodePath(neighbour, newDistance))
                }
            }
        }
        return null
    }

    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val map = NodeMap.from(input)
        val initialNodes = map.getInitialNodes()
        return minimumPath(initialNodes, map) ?: throw IllegalStateException("Could not find minimum path")
    }
}