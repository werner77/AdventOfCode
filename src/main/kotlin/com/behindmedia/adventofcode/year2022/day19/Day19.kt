package com.behindmedia.adventofcode.year2022.day19

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2022.day19.Kind.Clay
import com.behindmedia.adventofcode.year2022.day19.Kind.Companion.allKindsWithNull
import com.behindmedia.adventofcode.year2022.day19.Kind.Geode
import com.behindmedia.adventofcode.year2022.day19.Kind.Obsidian
import com.behindmedia.adventofcode.year2022.day19.Kind.Ore
import kotlin.math.*

private enum class Kind(val index: Int) {
    Ore(0), Clay(1), Obsidian(2), Geode(3);

    companion object {
        val allKindsWithNull: List<Kind?> = listOf(null, Ore, Clay, Obsidian, Geode).reversed()

        fun from(index: Int): Kind {
            return when(index) {
                0 -> Ore
                1 -> Clay
                2 -> Obsidian
                3 -> Geode
                else -> error("Invalid index: $index")
            }
        }
    }
}

private data class Cost(val kind: Kind, val cost: Long)
private class BluePrint(val id: Long, val robots: Array<Set<Cost>>) {

    companion object {
        private val regex =
            """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()

        operator fun invoke(string: String): BluePrint {
            val match = regex.matchEntire(string)?.groupValues ?: error("Invalid string: $string")
            val id = match[1].toLong()
            val robots = mutableMapOf<Kind, Set<Cost>>()
            robots[Ore] = setOf(Cost(Ore, match[2].toLong()))
            robots[Clay] = setOf(Cost(Ore, match[3].toLong()))
            robots[Obsidian] = setOf(Cost(Ore, match[4].toLong()), Cost(Clay, match[5].toLong()))
            robots[Geode] = setOf(Cost(Ore, match[6].toLong()), Cost(Obsidian, match[7].toLong()))
            return BluePrint(id, Array(Kind.values().size) {
                robots[Kind.from(it)] ?: emptySet()
            })
        }
    }


    fun qualityLevel(maxGeodes: Long): Long {
        return id * maxGeodes
    }

    private fun calculateCostInOre(kind: Kind): Long {
        val costs = robots[kind.index]
        var result = 0L
        for (c in costs) {
            result += if (c.kind == Ore) {
                c.cost
            } else {
                calculateCostInOre(c.kind) * c.cost
            }
        }
        return result
    }

    private val robotCostsInOre = Array(Kind.values().size) { index ->
        calculateCostInOre(Kind.from(index))
    }

    fun robotCostInOre(kind: Kind): Long {
        return robotCostsInOre[kind.index]
    }
}

fun main() {
    val bluePrints = parseLines("/2022/day19.txt") { line ->
        BluePrint(line)
    }
    timing {
        var sum = 0L
        val initialState = State(Ore to 1L)
        for (bp in bluePrints) {
            val maxValue = findMaxValue(bp, initialState, 0, 24, mutableMapOf())
            sum += bp.qualityLevel(maxValue)
        }
        println(sum)
    }
}

private interface MutableState {
    fun addRobotAmount(kind: Kind, amount: Long): Long
    fun addMaterialAmount(kind: Kind, amount: Long): Long
}

private class State(private val values: Array<Long> = Array(8) { 0L }) {

    companion object {
        operator fun invoke(vararg robots: Pair<Kind, Long>): State {
            val state = State()
            for ((kind, amount) in robots) {
                state.values[kind.index] = amount
            }
            return state
        }
    }

    fun getRobotAmount(kind: Kind): Long {
        return values[kind.index]
    }

    fun getMaterialAmount(kind: Kind): Long {
        return values[kind.index + 4]
    }

    private fun incrementRobotAmount(kind: Kind): Long {
        values[kind.index] += 1L
        return values[kind.index]
    }

    private fun addMaterialAmount(kind: Kind, amount: Long): Long {
        val index = kind.index + 4
        values[index] += amount
        return values[index]
    }

    override fun equals(other: Any?): Boolean {
        val otherState = other as? State ?: return false
        return this.values.contentEquals(otherState.values)
    }

    override fun hashCode(): Int {
        return this.values.contentHashCode()
    }

    fun nextState(bluePrint: BluePrint, constructRobot: Kind?): State? {
        val copy: State = State(this.values.copyOf())
        if (constructRobot != null) {
            val cost = bluePrint.robots[constructRobot.index]
            for (c in cost) {
                val newValue = copy.addMaterialAmount(c.kind, -c.cost)
                if (newValue < 0L) return null
            }
            copy.incrementRobotAmount(constructRobot)
        }
        for (kind in Kind.values()) {
            copy.addMaterialAmount(kind, this.getRobotAmount(kind))
        }
        return copy
    }

    fun encodeMaterials(): Long {
        var value = 0L
        for (kind in Kind.values()) {
            value = value or (getMaterialAmount(kind) shl (8 * kind.index))
        }
        return value
    }

    fun value(bluePrint: BluePrint, minutesRemaining: Int): Long {

        // Calculate the amount of ore we would have until the end with this state
        // This is the sum of all materials expressed in ore plus the amount of materials each robot can produce till the end

        var result = 0L
        for (kind in Kind.values()) {
            val costInOre = bluePrint.robotCostInOre(kind)
            result += costInOre * getMaterialAmount(kind)
            result += costInOre * getRobotAmount(kind) * minutesRemaining
        }
        return result
    }
}

private fun findMaxValue(bp: BluePrint, state: State, time: Int, maxTime: Int, cache: MutableMap<Int, Array<Long>>): Long {
    // State:
    // - number of robots active of each kind
    // - the amount of material of each kind

    // Find:
    // - max value of Geodes
    // - this means: max value of geode robots
    // - this means: max value of materials needed to get geode robots
    if (time >= maxTime) {
        return state.getMaterialAmount(kind = Geode)
    }
    val minutesRemaining = maxTime - time
    val currentValue = Array<Long>(4) {
        val kind = Kind.from(it)
        state.getMaterialAmount(kind) + state.getRobotAmount(kind) * minutesRemaining
    }
    val maxVisitedValue = cache[time]
    if (maxVisitedValue != null) {
        var bigger = true
        for (i in 0 until 4) {
            if (maxVisitedValue[i] < currentValue[i]) {
                bigger = false
            }
        }
        if (bigger) return -1L
    }
    cache[time] = currentValue

    var maxValue = 0L

    for (kind in allKindsWithNull) {
        val nextState = state.nextState(bp, kind) ?: continue
        val value = findMaxValue(bp, nextState, time + 1, maxTime, cache)
        maxValue = max(maxValue, value)
    }

    return maxValue
}
