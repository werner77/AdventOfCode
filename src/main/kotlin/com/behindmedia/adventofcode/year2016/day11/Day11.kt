package com.behindmedia.adventofcode.year2016.day11

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.popFirst
import com.behindmedia.adventofcode.common.timing

private val regex1 = """.* ([a-zA-Z]+) generator.*""".toRegex()
private val regex2 = """.* ([a-zA-Z]+)-compatible microchip.*""".toRegex()

private enum class ItemType {
    Generator,
    Chip,
    Elevator
}

private data class Item(val index: Int, val type: ItemType) {
    companion object {
        var indexToNameMap: Map<Int, String> = mapOf()
    }

    fun isCompatibleWith(other: Item): Boolean {
        return this.type == ItemType.Elevator || other.type == ItemType.Elevator || this.index == other.index || this.type == other.type
    }

    override fun toString(): String {
        return if (type == ItemType.Elevator) {
            type.name.lowercase()
        } else {
            (indexToNameMap[index] ?: index.toString()) + "-" + type.name.lowercase()
        }
    }
}

@JvmInline
private value class State(private val representation: Long) {

    fun addingItem(level: Int, index: Int, type: ItemType): State {
        val mask = maskForItem(level, index, type)
        return State(representation = representation or mask)
    }

    fun addingItem(level: Int, item: Item): State {
        return this.addingItem(level, item.index, item.type)
    }

    fun removingItem(level: Int, index: Int, type: ItemType): State {
        val mask = maskForItem(level, index, type)
        return State(representation = representation and mask.inv())
    }

    fun removingItem(level: Int, item: Item): State {
        return this.removingItem(level, item.index, item.type)
    }

    fun itemsAtLevel(level: Int): Sequence<Item> {
        return sequence {
            for (i in 0 until 16) {
                val mask = 1L shl (16 * level + i)
                val type = when(i) {
                    0, 8 -> ItemType.Elevator
                    in 1 until 8 -> ItemType.Chip
                    else -> ItemType.Generator
                }
                val index = if (i < 8) i else i - 8
                if (representation and mask == mask) yield(Item(index, type))
            }
        }
    }

    private fun maskForItem(level: Int, index: Int, type: ItemType): Long {
        val effectiveIndex = when(type) {
            ItemType.Elevator -> 0
            ItemType.Generator -> 8 + index
            ItemType.Chip -> index
        }
        return 1L shl (level * 16 + effectiveIndex)
    }

    // Each 16 bits in the representation represents one level

    // Count the number of 1 bits
    val numberOfItems: Int
        get() {
            return countElements(0, 64)
        }

    val isEnd: Boolean
        get() {
            return countElements(0, 48) == 0
        }

    val elevatorLevel: Int
        get() {
            // The elevator is the zero bit for each byte
            for (i in 0 until 4) {
                val mask = 1L shl (i * 16)
                if (representation and mask == mask) return i
            }
            error("Elevator not found")
        }

    val isValid: Boolean
        get() {
            // Check for each level if there is a machine for each chip
            for (level in 0 until 4) {
                var foundChipWithoutGenerator = false
                var foundGenerator = false
                for (index in 1 until 8) {
                    // For each chip/generator
                    val chipMask = 1L shl (level * 16 + index)
                    val generatorMask = 1L shl (level * 16 + 8 + index)
                    val chipPresent = (representation and chipMask) == chipMask
                    val generatorPresent = (representation and generatorMask) == generatorMask
                    if (generatorPresent) {
                        foundGenerator = true
                    }
                    if (chipPresent && !generatorPresent) {
                        foundChipWithoutGenerator = true
                    }
                    if (foundGenerator && foundChipWithoutGenerator) return false
                }
            }
            return true
        }

    private fun countElements(fromBit: Int, toBit: Int): Int {
        var count = 0
        for (i in fromBit until toBit) {
            val mask = 1L shl i
            if ((representation and mask) == mask) count++
        }
        return count
    }

    fun reachableStates(): Sequence<State> {
        val itemsAtCurrentLevel = this.itemsAtLevel(elevatorLevel).toList()
        val currentState = this
        return sequence {
            for (newLevel in listOf(elevatorLevel + 1, elevatorLevel - 1)) {
                if (newLevel !in 0 until 4) continue

                // Move the elevator to the new level
                val newState = currentState
                    .removingItem(elevatorLevel, 0, ItemType.Elevator)
                    .addingItem(newLevel, 0, ItemType.Elevator)

                for (i in 1 until itemsAtCurrentLevel.size) {
                    val firstItem = itemsAtCurrentLevel[i]
                    val firstState = newState
                        .removingItem(elevatorLevel, firstItem)
                        .addingItem(newLevel, firstItem)
                    for (j in i + 1 until itemsAtCurrentLevel.size) {
                        val secondItem = itemsAtCurrentLevel[j]
                        if (!firstItem.isCompatibleWith(secondItem)) continue
                        val secondState = firstState.removingItem(elevatorLevel, secondItem)
                            .addingItem(newLevel, secondItem)
                        yield(secondState)
                    }
                    yield(firstState)
                }
            }
        }
    }

    override fun toString(): String {
        val buffer = StringBuilder()
        for (level in 0 until 4) {
            buffer.append("Level $level: [")
            var first = true
            for (item in itemsAtLevel(level)) {
                if (first) {
                    first = false
                } else {
                    buffer.append(", ")
                }
                buffer.append(item.toString())
            }
            buffer.append("]")
            buffer.appendLine()
        }
        return buffer.toString()
    }
}

private fun getMinNumberOfSteps(from: State): Int {
    val candidateStates = ArrayDeque<Pair<State, Int>>()
    val handledStates = mutableSetOf<State>()
    candidateStates.add(Pair(from, 0))
    handledStates.add(from)
    while (candidateStates.isNotEmpty()) {
        val (state, count) = candidateStates.popFirst() ?: break
        if (state.isEnd) return count
        state.reachableStates().forEach {
            if (it.isValid && !handledStates.contains(it)) {
                handledStates += it
                candidateStates += Pair(it, count + 1)
            }
        }
    }
    error("No path found")
}

private fun solve(fileName: String) {
    var level = 0
    var currentItemIndex = 1
    val nameToIndexMap = mutableMapOf<String, Int>()
    var state = State(0)
    state = state.addingItem(0, 0, ItemType.Elevator)
    parseLines(fileName) { line ->
        val items = line.split(",", "and").filter { it.isNotBlank() }.map {
            it.trim()
        }.mapNotNull { item ->
            regex1.matchEntire(item)?.groupValues?.get(1)?.let {
                val index = nameToIndexMap.getOrPut(it) { currentItemIndex++ }
                Item(index, ItemType.Generator)
            } ?: regex2.matchEntire(item)?.groupValues?.get(1)?.let {
                val index = nameToIndexMap.getOrPut(it) { currentItemIndex++ }
                Item(index, ItemType.Chip)
            }
        }.toList()
        items.forEach {
            state = state.addingItem(level, it)
        }
        level++
    }
    Item.indexToNameMap = nameToIndexMap.entries.fold(mutableMapOf<Int, String>()) { map, entry ->
        map.apply {
            put(entry.value, entry.key)
        }
    }
    println(state)
    val stepCount = getMinNumberOfSteps(state)
    println(stepCount)
}

private fun part1() {
    timing {
        solve("/2016/day11.txt")
    }
}

private fun part2() {
    timing {
        solve("/2016/day11-2.txt")
    }
}

fun main() {
    part1()
    part2()
}