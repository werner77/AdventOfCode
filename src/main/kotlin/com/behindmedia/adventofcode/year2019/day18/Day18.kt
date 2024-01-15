package com.behindmedia.adventofcode.year2019.day18

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.Path
import com.behindmedia.adventofcode.common.plusAssign
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing
import kotlin.math.abs
import java.util.PriorityQueue

private data class State(val positions: PositionSet, val keys: KeySet)

fun main() {
    val grid1 = CharGrid(read("/2019/day18.txt"))

    timing {
        println(findShortestPath(grid1.findAllEdges().toArray(), '@'))
    }

    timing {
        println(findShortestPath(grid1.modifiedCopy().findAllEdges().toArray(), '1', '2', '3', '4'))
    }
}

private fun CharGrid.modifiedCopy(): CharGrid {
    val modifiedGrid = mutableCopy()
    val startCoordinate = single { it.value == '@' }.key
    modifiedGrid[startCoordinate] = '#'
    var startIndex = 1
    for (n in startCoordinate.allNeighbours) {
        if (abs(n.x - startCoordinate.x) == 1 && abs(n.y - startCoordinate.y) == 1) {
            modifiedGrid[n] = (startIndex++).digitToChar()
        } else {
            modifiedGrid[n] = '#'
        }
    }
    return modifiedGrid
}

private fun CharGrid.findAllEdges(): Map<Char, List<Pair<Char, Int>>> {
    val specialCoordinates = filter { it.value !in listOf('.', '#') }
    val edges = mutableMapOf<Char, List<Pair<Char, Int>>>()
    for ((c, value) in specialCoordinates) {
        // Find edges to nearest reachable coordinates?
        edges[value] = findEdges(c)
    }
    return edges
}

private fun Map<Char, List<Pair<Char, Int>>>.toArray(): Array<Array<Pair<Char, Int>>> {
    return Array(256) { code ->
        val char = Char(code = code)
        this[char]?.toTypedArray() ?: emptyArray()
    }
}

@JvmInline
private value class KeySet(val value: Int): Iterable<Char> {
    companion object {
        val NONE: KeySet = KeySet(0)
        val ALL: KeySet = KeySet((1 shl 26) - 1)
    }
    operator fun contains(key: Char): Boolean {
        val index = key - 'a'
        require(index in 0 until 26)
        val mask = 1 shl index
        return value and mask == mask
    }

    operator fun plus(key: Char): KeySet {
        val index = key - 'a'
        require(index in 0 until 26)
        val mask = 1 shl index
        return KeySet(value = this.value or mask)
    }

    override fun iterator(): Iterator<Char> {
        return object: Iterator<Char> {
            var bit = -1

            override fun hasNext(): Boolean {
                return 1 shl (bit + 1) <= value
            }

            override fun next(): Char {
                while (bit < 26) {
                    bit++
                    val mask = 1 shl bit
                    if ((mask and value) == mask) {
                        return 'a' + bit
                    }
                }
                throw NoSuchElementException()
            }
        }
    }
    override fun toString() = this.sorted().joinToString("")
}

/**
 * Optimized data structure to represent up to 4 nodes (current positions) with a single integer.
 */
@JvmInline
private value class PositionSet(private val state: Int): Iterable<Char> {

    companion object {
        operator fun invoke(vararg positions: Char): PositionSet {
            return PositionSet(positions.foldIndexed(0) { index, state, node ->
                state or (node.code shl shiftFor(index))
            })
        }

        private fun shiftFor(index: Int): Int {
            return 8 * index
        }
    }

    fun replacing(index: Int, position: Char): PositionSet {
        val shift = shiftFor(index)
        var newState = state and (0xFF shl shift).inv()
        newState = newState or (position.code shl shift)
        return PositionSet(newState)
    }

    override fun iterator(): Iterator<Char> {
        return object: Iterator<Char> {
            var index = -1

            override fun hasNext(): Boolean {
                return index < 3
            }

            override fun next(): Char {
                index++
                if (index >= 4) {
                    throw NoSuchElementException()
                }
                return ((state shr shiftFor(index)) and 0xFF).toChar()
            }
        }
    }

    override fun toString(): String = this.toList().toString()
}


private fun findShortestPath(edges: Array<Array<Pair<Char, Int>>>, vararg startPositions: Char): Int {
    val pending = PriorityQueue<Path<State>>()
    val seen = hashSetOf<State>()
    pending.add(Path(State(positions = PositionSet(*startPositions), keys = KeySet.NONE), 0, null))
    while (pending.isNotEmpty()) {
        val path = pending.poll()
        val state = path.destination
        if (state.keys == KeySet.ALL) return path.length
        if (state in seen) continue
        seen += state
        for ((i, position) in state.positions.withIndex()) {
            for ((value, weight) in edges[position.code]) {
                if (value.isUpperCase() && value.lowercaseChar() !in state.keys) continue
                val newKeys = if (value.isLowerCase()) state.keys + value else state.keys
                pending += Path(destination = State(
                    positions = state.positions.replacing(index = i, position = value),
                    keys = newKeys
                ), length = path.length + weight, parent = path)
            }
        }
    }
    error("No path found")
}

private fun CharGrid.findEdges(start: Coordinate): List<Pair<Char, Int>> {
    val result = mutableListOf<Pair<Char, Int>>()
    val pending = ArrayDeque<Pair<Coordinate, Int>>()
    val seen = hashSetOf(start)
    pending += start to 0
    while (pending.isNotEmpty()) {
        val (coordinate, length) = pending.removeFirst()
        for (neighbour in coordinate.directNeighbours) {
            val value = this.getOrNull(neighbour)?.takeIf { it != '#' } ?: continue
            if (neighbour in seen) continue
            seen += neighbour
            if (value == '.') {
                pending += neighbour to length + 1
            } else {
                result += value to length + 1
            }
        }
    }
    return result
}