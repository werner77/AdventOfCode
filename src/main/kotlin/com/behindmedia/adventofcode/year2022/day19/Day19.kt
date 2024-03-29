package com.behindmedia.adventofcode.year2022.day19

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.timing
import com.behindmedia.adventofcode.year2022.day19.Kind.Clay
import com.behindmedia.adventofcode.year2022.day19.Kind.Companion.allKindsWithNull
import com.behindmedia.adventofcode.year2022.day19.Kind.Geode
import com.behindmedia.adventofcode.year2022.day19.Kind.Obsidian
import com.behindmedia.adventofcode.year2022.day19.Kind.Ore
import kotlin.math.max
import kotlin.math.min
import java.util.concurrent.atomic.AtomicInteger

private enum class Kind(val index: Int) {
    Ore(0), Clay(1), Obsidian(2), Geode(3);

    companion object {
        const val count: Int = 4
        val allKindsWithNull: List<Kind?> = listOf(null, Ore, Clay, Obsidian, Geode).reversed()

        fun from(index: Int): Kind {
            return when (index) {
                0 -> Ore
                1 -> Clay
                2 -> Obsidian
                3 -> Geode
                else -> error("Invalid index: $index")
            }
        }
    }
}

private class BluePrint(val id: Int, val robots: Array<Map<Kind, Int>>) {

    companion object {
        private val regex =
            """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()

        operator fun invoke(string: String): BluePrint {
            val match = regex.matchEntire(string)?.groupValues ?: error("Invalid string: $string")
            val id = match[1].toInt()
            val robots = mutableMapOf<Kind, Map<Kind, Int>>()
            robots[Ore] = mapOf(Ore to match[2].toInt())
            robots[Clay] = mapOf(Ore to match[3].toInt())
            robots[Obsidian] = mapOf(Ore to match[4].toInt(), Clay to match[5].toInt())
            robots[Geode] = mapOf(Ore to match[6].toInt(), Obsidian to match[7].toInt())
            return BluePrint(id, Array(Kind.values().size) {
                robots[Kind.from(it)] ?: emptyMap()
            })
        }
    }

    fun qualityLevel(maxGeodes: Int): Int {
        return id * maxGeodes
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
    val sum = AtomicInteger(0)
    val initialState = State(Ore to 1)
    bluePrints.parallelStream().forEach { bp ->
        val maxValue = dfs(bp, initialState, 24)
        sum.updateAndGet {
            bp.qualityLevel(maxValue) + it
        }
    }
    println(sum.get())
}

private fun part2(bluePrints: List<BluePrint>) {
    val product = AtomicInteger(1)
    val initialState = State(Ore to 1)
    bluePrints.take(3).parallelStream().forEach { bp ->
        val maxValue = dfs(bp, initialState, 32)
        product.updateAndGet {
            it * maxValue
        }
    }
    println(product.get())
}

private class State(private val values: Array<Int> = Array(Kind.count * 2) { 0 }) {

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
        return values[kind.index + Kind.count]
    }

    private fun incrementRobotAmount(kind: Kind) {
        values[kind.index] += 1
    }

    private fun setRobotAmount(kind: Kind, value: Int) {
        values[kind.index] = value
    }

    private fun addMaterialAmount(kind: Kind, amount: Int): Boolean {
        val index = kind.index + Kind.count
        if (-amount > values[index]) return false
        values[index] += amount
        return true
    }

    override fun equals(other: Any?): Boolean {
        val otherState = other as? State ?: return false
        return this.values.contentEquals(otherState.values)
    }

    override fun hashCode(): Int {
        return this.values.contentHashCode()
    }

    fun nextState(bluePrint: BluePrint, constructRobot: Kind?, remainingTime: Int): State? {
        val copy: State = State(this.values.copyOf())
        if (constructRobot != null) {
            val costs = bluePrint.robots[constructRobot.index]
            for ((kind, cost) in costs) {
                if (!copy.addMaterialAmount(kind, -cost)) return null
            }
            copy.incrementRobotAmount(constructRobot)
        }
        for (kind in Kind.values()) {
            copy.addMaterialAmount(kind, this.getRobotAmount(kind))
        }
        // Max out the robots/materials based on the remaining time to minimize the number of different states
        copy.applyLimits(bluePrint, remainingTime)
        return copy
    }

    fun value(minutesRemaining: Int, bluePrint: BluePrint): StateValue {
        return StateValue(state = this, minutesRemaining = minutesRemaining)
    }

    private fun applyLimits(bluePrint: BluePrint, remainingTime: Int) {
        for (kind in Kind.values()) {
            if (kind == Geode) continue
            val maxSpendPerMinute = bluePrint.robots.maxOf { it[kind] ?: 0 }
            // Subtract 1 since at least the last minute would be necessary to produce Geode otherwise it doesn't add value.
            val maxSpend = maxSpendPerMinute * (remainingTime - 1)
            val materialAmount = getMaterialAmount(kind)
            val diff = materialAmount - maxSpend
            if (diff > 0) {
                // Eliminate all the waste
                addMaterialAmount(kind, -min(diff, materialAmount))
                setRobotAmount(kind, 0)
            }
        }
    }
}

private class StateValue(state: State, minutesRemaining: Int) {

    private val values = IntArray(Kind.values().size) {
        val kind = Kind.from(it)
        state.getMaterialAmount(kind) + state.getRobotAmount(kind) * minutesRemaining
    }
    private val maxRemainingGeodeAmount: Int = max(0, ((minutesRemaining - 1) * minutesRemaining) / 2)

    fun canBeGreaterThan(other: StateValue): Boolean {
        if (Kind.values().none { this.values[it.index] > other.values[it.index] }) return false
        return values[Geode.index] + maxRemainingGeodeAmount > other.values[Geode.index]
    }
}

private fun dfs(
    bluePrint: BluePrint,
    state: State,
    remainingTime: Int,
    maxVisitedValues: Array<StateValue?> = Array(remainingTime) { null },
    cache: MutableMap<Any, Int> = HashMap(1024 * 64)
): Int {
    if (remainingTime == 0) {
        return state.getMaterialAmount(kind = Geode)
    }

    val currentValue = state.value(remainingTime, bluePrint)
    val maxVisitedValue = maxVisitedValues[remainingTime - 1]
    if (maxVisitedValue != null && !currentValue.canBeGreaterThan(maxVisitedValue)) {
        return -1
    }
    maxVisitedValues[remainingTime - 1] = currentValue

    val cacheKey = Pair(remainingTime, state)
    val cachedValue = cache[cacheKey]
    if (cachedValue != null) return cachedValue

    var maxValue = state.getMaterialAmount(kind = Geode)
    for (kind in allKindsWithNull) {
        val nextState = state.nextState(bluePrint, kind, remainingTime - 1) ?: continue
        val value = dfs(bluePrint, nextState, remainingTime - 1, maxVisitedValues, cache)
        maxValue = max(maxValue, value)
    }
    cache[cacheKey] = maxValue
    return maxValue
}
