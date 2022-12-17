package com.behindmedia.adventofcode.year2022.day16

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.splitNonEmptySequence
import com.behindmedia.adventofcode.common.timing

private typealias IDType = Long

private data class Action(val nextValve: IDType, val openValve: IDType?, val flowIncrement: Int)

private data class Valve(val id: IDType, val flowRate: Int, val connectedValves: List<IDType>) {

    // Yields all the possible actions for this valve given the supplied set of open valves
    fun actions(openValves: ValveSet): List<Action> {
        val result = ArrayList<Action>(connectedValves.size + 1)
        if (flowRate > 0 && id !in openValves) {
            result += Action(id, id, flowRate)
        }
        for (connected in connectedValves) {
            result += Action(connected, null, 0)
        }
        return result
    }
}

@JvmInline
private value class ValveSet private constructor(private val value: Long) {
    companion object {
        operator fun invoke(vararg ids: IDType): ValveSet {
            var value = 0L
            for (id in ids) {
                value = value or id
            }
            return ValveSet(value)
        }
    }
    operator fun plus(valve: IDType): ValveSet {
        return ValveSet(value or valve)
    }
    operator fun minus(valve: IDType): ValveSet {
        return ValveSet(value and valve.inv())
    }
    operator fun contains(valve: IDType): Boolean {
        return value and valve == valve
    }
    fun plusOptional(valve: IDType?): ValveSet {
        return valve?.let { this + it } ?: this
    }
    fun single(): IDType {
        return value
    }
    fun first(): IDType {
        for (i in 0 until valveNameMap.size) {
            val mask = 1L shl i
            if (value and mask == mask) return mask
        }
        error("No element found")
    }
    fun last(): IDType {
        for (i in valveNameMap.size - 1 downTo 0) {
            val mask = 1L shl i
            if (value and mask == mask) return mask
        }
        error("No element found")
    }
    fun isComplete(): Boolean {
        return value == completeValveMask
    }
}

private val valveNameMap = mutableMapOf<String, Long>()
private var completeValveMask = 0L

private fun addOrGetValveId(string: String): Long {
    val existing = valveNameMap[string]
    if (existing != null) {
        return existing
    }
    require(valveNameMap.size < 64)
    val bit = valveNameMap.size
    val mask = 1L shl bit
    valveNameMap[string] = mask
    completeValveMask = completeValveMask or mask
    return mask
}

private fun String.toId(): IDType {
    return valveNameMap[this] ?: error("No ID found for valve with name: $this")
}

fun main() {
    val regex = """Valve ([A-Z]+) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z ,]+)""".toRegex()
    val map = parseLines("/2022/day16.txt") { line ->
        val match = regex.matchEntire(line.trim()) ?: error("No match for line : $line")
        val id = addOrGetValveId(match.groupValues[1])
        val flowRate = match.groupValues[2].toInt()
        val valves = match.groupValues[3].splitNonEmptySequence(" ", ",").map { addOrGetValveId(it) }.toList()
        Valve(id, flowRate, valves)
    }.fold(mutableMapOf<IDType, Valve>()) { map, valve ->
        map.apply {
            put(valve.id, valve)
        }
    }
    timing {
        val state = State(ValveSet(), ValveSet("AA".toId()))
        val value1 = part1(
            data = map,
            cache = mutableMapOf(),
            state = state,
            flow = 0,
            elapsedTime = 0,
            maxTime = 30
        )
        println(value1)
    }
    timing {
        val state = State(ValveSet(), ValveSet("AA".toId()))
        val value2 = part2(
            data = map,
            cache = mutableMapOf(),
            state = state,
            flow = 0,
            elapsedTime = 0,
            maxTime = 26
        )
        println(value2)
    }
}

private data class State(val openValves: ValveSet, val currentValves: ValveSet)

private fun <T>Set<T>.plusOptional(element: T?): Set<T> {
    return element?.let { this + it } ?: this
}

private fun part1(
    data: Map<IDType, Valve>,
    cache: MutableMap<State, Pair<Int, Int>>,
    state: State,
    flow: Int,
    elapsedTime: Int,
    maxTime: Int
): Int {
    // All valves open or no more time, no actions left to perform
    if (elapsedTime == maxTime || state.openValves.isComplete()) {
        return flow
    }
    val cachedValue = cache[state]
    if (cachedValue != null && cachedValue.first >= flow && cachedValue.second <= elapsedTime) {
        return -1
    }
    cache[state] = Pair(flow, elapsedTime)
    val currentValve = data[state.currentValves.single()] ?: error("Valve not found")
    var maxFlow = flow
    for (action in currentValve.actions(state.openValves)) {
        val value = part1(
            data = data,
            cache = cache,
            state = State(state.openValves.plusOptional(action.openValve), ValveSet(action.nextValve)),
            flow = flow + action.flowIncrement * (maxTime - elapsedTime - 1), // Add the cumulative flow to the end
            elapsedTime = elapsedTime + 1,
            maxTime = maxTime
        )
        if (value > maxFlow) {
            maxFlow = value
        }
    }
    return maxFlow
}

private fun part2(
    data: Map<IDType, Valve>,
    cache: MutableMap<State, Pair<Int, Int>>,
    state: State,
    flow: Int,
    elapsedTime: Int,
    maxTime: Int
): Int {
    // All valves open or no more time, no actions left to perform
    if (elapsedTime == maxTime || state.openValves.isComplete()) {
        return flow
    }
    val cachedValue = cache[state]
    if (cachedValue != null && cachedValue.first >= flow && cachedValue.second <= elapsedTime) {
        return -1
    }
    cache[state] = Pair(flow, elapsedTime)

    val valve1 = data[state.currentValves.first()] ?: error("Valve not found")
    val valve2 = data[state.currentValves.last()] ?: error("Valve not found")

    var maxFlow = flow
    for (action1 in valve1.actions(state.openValves)) {
        val nextOpenValves = state.openValves.plusOptional(action1.openValve)
        for (action2 in valve2.actions(nextOpenValves)) {
            val value = part2(
                data = data,
                cache = cache,
                state = State(nextOpenValves.plusOptional(action2.openValve), ValveSet(action1.nextValve, action2.nextValve)),
                flow = flow + (action1.flowIncrement + action2.flowIncrement) * (maxTime - elapsedTime - 1), // Add the cumulative flow to the end
                elapsedTime = elapsedTime + 1,
                maxTime = maxTime
            )
            if (value > maxFlow) {
                maxFlow = value
            }
        }
    }
    return maxFlow
}
