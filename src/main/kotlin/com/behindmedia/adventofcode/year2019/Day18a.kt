package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.reachableNodes

class Day18a {

    class NodeMap {
        companion object {
            fun from(input: String): NodeMap {
                val lines = input.split('\n')
                val map = NodeMap()
                for ((y, line) in lines.withIndex()) {
                    for ((x, c) in line.withIndex()) {
                        map.matrix[x][y] = c
                    }
                }
                return map
            }
        }

        val size = 82

        private val matrix = Array(size) {
            Array(size) { '#' }
        }

        fun nodeAt(coordinate: Coordinate): Char {
            return matrix[coordinate.x][coordinate.y]
        }

        fun isAccessible(coordinate: Coordinate, keys: KeyCollection): Boolean {
            return (coordinate.x in 0 until size && coordinate.y in 0 until size && nodeAt(coordinate).isAccessible(keys))
        }

        inline fun forAccessibleNeighbours(coordinate: Coordinate, keys: KeyCollection, perform: (Coordinate) -> Unit) {
            coordinate.forDirectNeighbours { neighbour ->
                if (neighbour.x in 0 until size && neighbour.y in 0 until size && nodeAt(neighbour).isAccessible(keys)) {
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

    data class KeyNode(val identifier: Char, val coordinate: Coordinate, val keysInPossession: KeyCollection)

    /**
     * Optimized data structure based on a bitmask to store which keys have been collected
     */
    class KeyCollection {
        companion object {
            private val completeMask: Int
            init {
                var mask = 0
                for (i in 0 until 26) {
                    mask = mask.or(1 shl i)
                }
                completeMask = mask
            }
        }

        private var internalState = 0

        val representation: Int
            get() = this.internalState

        private fun addKey(key: Char) {
            val shift = key - 'a'
            internalState = internalState.or( 1.shl(shift) )
        }

        fun contains(key: Char): Boolean {
            val shift = key - 'a'
            val mask = 1.shl(shift)
            return internalState.and(mask) == mask
        }

        fun isComplete(): Boolean {
            return internalState.and(completeMask) == completeMask
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

        operator fun plus(key: Char): KeyCollection {
            val result = KeyCollection()
            result.internalState = this.internalState
            result.addKey(key)
            return result
        }
    }

    private fun weightedNeighboursForNode(node: KeyNode, map: NodeMap): List<Pair<KeyNode, Int>> {
        val result = mutableListOf<Pair<KeyNode, Int>>()
        var collectedKeys = node.keysInPossession

        reachableNodes(node.coordinate, neighbours = {
            it.directNeighbours
        }, reachable = {
            map.isAccessible(it, node.keysInPossession)
        }, process = {
            val c = map.nodeAt(it.node)
            if (map.isKeyNode(c) && !collectedKeys.contains(c)) {
                result.add(Pair(KeyNode(c, it.node, node.keysInPossession + c), it.pathLength))
                collectedKeys += c
            }
            if (collectedKeys.isComplete()) result else null
        })
        return result
    }

    fun getMinimumNumberOfMovesToCollectAllKeys(input: String): Int {
        val map = NodeMap.from(input)
        val currentPosition = map.getCurrentPositions().first()
        val initialNode = KeyNode('@', currentPosition, KeyCollection())

        return reachableNodes(initialNode, neighbours = { node ->
            weightedNeighboursForNode(node, map)
        }, process = { nodePath ->
            if (nodePath.node.keysInPossession.isComplete()) nodePath.pathLength else null
        }) ?: throw IllegalStateException("Could not find minimum path")
    }
}