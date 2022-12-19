package com.behindmedia.adventofcode.year2022.day19

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.timing
import com.behindmedia.adventofcode.year2022.day19.Kind.Clay
import com.behindmedia.adventofcode.year2022.day19.Kind.Companion.allKindsWithNull
import com.behindmedia.adventofcode.year2022.day19.Kind.Geode
import com.behindmedia.adventofcode.year2022.day19.Kind.Obsidian
import com.behindmedia.adventofcode.year2022.day19.Kind.Ore
import kotlin.math.max

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

private data class Cost(val kind: Kind, val cost: Int)
private class BluePrint(val id: Int, val robots: Array<Set<Cost>>) {

    companion object {
        private val regex =
            """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()

        operator fun invoke(string: String): BluePrint {
            val match = regex.matchEntire(string)?.groupValues ?: error("Invalid string: $string")
            val id = match[1].toInt()
            val robots = mutableMapOf<Kind, Set<Cost>>()
            robots[Ore] = setOf(Cost(Ore, match[2].toInt()))
            robots[Clay] = setOf(Cost(Ore, match[3].toInt()))
            robots[Obsidian] = setOf(Cost(Ore, match[4].toInt()), Cost(Clay, match[5].toInt()))
            robots[Geode] = setOf(Cost(Ore, match[6].toInt()), Cost(Obsidian, match[7].toInt()))
            return BluePrint(id, Array(Kind.values().size) {
                robots[Kind.from(it)] ?: emptySet()
            })
        }
    }


    fun qualityLevel(maxGeodes: Int): Int {
        return id * maxGeodes
    }

    private fun calculateCostInOre(kind: Kind): Int {
        val costs = robots[kind.index]
        var result = 0
        for (c in costs) {
            result += if (c.kind == Ore) {
                c.cost
            } else {
                calculateCostInOre(c.kind) * c.cost
            }
        }
        return result
    }

    private val costsInOre = Array(Kind.values().size) { index ->
        calculateCostInOre(Kind.from(index))
    }

    fun costInOre(kind: Kind): Int {
        return costsInOre[kind.index]
    }
}

fun main() {
    val bluePrints = parseLines("/2022/day19.txt") { line ->
        BluePrint(line)
    }
    timing {
        part1(bluePrints)
    }
    timing {
        part2(bluePrints)
    }
}

private fun part1(bluePrints: List<BluePrint>) {
    var sum = 0L
    val initialState = State(Ore to 1)
    for (bp in bluePrints) {
        val maxValue = findMaxValue(bp, initialState, 0, 24, mutableMapOf(), mutableMapOf())
        sum += bp.qualityLevel(maxValue)
    }
    println(sum)
}

private fun part2(bluePrints: List<BluePrint>) {
    var product = 1L
    val initialState = State(Ore to 1)
    for (bp in bluePrints.subList(0, 3)) {
        val maxValue = findMaxValue(bp, initialState, 0, 32, mutableMapOf(), mutableMapOf())
        product *= maxValue
    }
    println(product)
}

private typealias StateValue = StateValueImpl

private class State(private val values: Array<Int> = Array(8) { 0 }) {

    companion object {
        operator fun invoke(vararg robots: Pair<Kind, Int>): State {
            val state = State()
            for ((kind, amount) in robots) {
                state.values[kind.index] = amount
            }
            return state
        }
    }

    fun getRobotAmount(kind: Kind): Int {
        return values[kind.index]
    }

    fun getMaterialAmount(kind: Kind): Int {
        return values[kind.index + 4]
    }

    private fun incrementRobotAmount(kind: Kind): Int {
        values[kind.index] += 1
        return values[kind.index]
    }

    private fun addMaterialAmount(kind: Kind, amount: Int): Int {
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

    fun value(bluePrint: BluePrint, minutesRemaining: Int): StateValue {
        // Calculate the amount of ore we would have until the end with this state
        // This is the sum of all materials expressed in ore plus the amount of materials each robot can produce till the end
//        var result = 0L
//        for (kind in Kind.values()) {
//            val costInOre = bluePrint.costInOre(kind)
//            result += costInOre * getMaterialAmount(kind)
//            result += costInOre * getRobotAmount(kind) * minutesRemaining
//        }
//        return result

        return StateValueImpl { kind ->
            getMaterialAmount(kind) + getRobotAmount(kind) * minutesRemaining
        }

//        var value = 0L
//        for (kind in Kind.values()) {
//
//            value = value or (projectedAmount shl (8 * kind.index))
//        }
//        return value
    }
}

private class StateValueImpl(val values: Array<Int>) {
    constructor(initializer: (Kind) -> Int) : this(Array<Int>(Kind.values().size) { initializer(Kind.from(it)) })
    fun isGreaterThan(other: StateValueImpl): Boolean {
        for (i in values.indices) {
            if (this.values[i] < other.values[i]) {
                return false
            }
        }
        return true
    }
}

private fun findMaxValue(bp: BluePrint, state: State, time: Int, maxTime: Int, maxVisitedValues: MutableMap<Int, StateValue>, cache: MutableMap<Any, Int>): Int {
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
    val currentValue = state.value(bp, maxTime - time)
    val maxVisitedValue = maxVisitedValues[time]
    if (maxVisitedValue != null && maxVisitedValue.isGreaterThan(currentValue)) {
        return -1
    }

    val cacheKey = Pair(time, state)
    val cachedValue = cache[cacheKey]
    if (cachedValue != null) return cachedValue

    maxVisitedValues[time] = currentValue

    var maxValue = 0

    for (kind in allKindsWithNull) {
        val nextState = state.nextState(bp, kind) ?: continue
        val value = findMaxValue(bp, nextState, time + 1, maxTime, maxVisitedValues, cache)
        maxValue = max(maxValue, value)
    }

    cache[cacheKey] = maxValue
    return maxValue
}
