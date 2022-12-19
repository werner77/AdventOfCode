package com.behindmedia.adventofcode.year2022.day16

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2022.day16.Valve.Companion.allValveIds
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private data class Valve(val name: String, val flowRate: Int, val connectedValves: List<String>) {

    val id: Long by lazy(mode = NONE) {
        getAndIncrementValveId()
    }

    companion object {
        private var nextValveId: Long = 1L
        private fun getAndIncrementValveId(): Long {
            return nextValveId.also {
                nextValveId = nextValveId shl 1
            }
        }

        val allValveIds: Long
            get() = nextValveId - 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherValve = other as? Valve ?: return false
        return this.name == otherValve.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

fun main() {
    val regex = """Valve ([A-Z]+) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z ,]+)""".toRegex()
    val valveMap = parseLines("/2022/day16.txt") { line ->
        val match = regex.matchEntire(line.trim()) ?: error("No match for line : $line")
        val name = match.groupValues[1]
        val flowRate = match.groupValues[2].toInt()
        val valves = match.groupValues[3].splitNonEmptySequence(" ", ",").toList()
        Valve(name, flowRate, valves)
    }.fold(mutableMapOf<String, Valve>()) { map, valve ->
        map.apply {
            put(valve.name, valve)
        }
    }

    // Calculate the graph
    val connections = calculateConnections(data = valveMap)
    val start = valveMap["AA"] ?: error("Start node not found")
    timing {
        part1(start, connections)
    }
    timing {
        part2(start, connections)
    }
}

private fun part1(
    start: Valve,
    connections: Map<Long, List<Pair<Valve, Int>>>
) {
    val ans = dfs(
        valveId = start.id,
        openValves = 0,
        time = 30,
        connections = connections,
        cache = mutableMapOf()
    )
    println(ans)
}

private fun part2(
    start: Valve,
    connections: Map<Long, List<Pair<Valve, Int>>>
) {
    val ans = AtomicInteger(0)
    val cache = ConcurrentHashMap<Any, Int>()
    runBlocking {
        withContext(Dispatchers.Default) {
            for (i in 1..allValveIds / 2) {
                val first = i and allValveIds
                val second = first xor allValveIds
                launch {
                    val value = dfs(
                        valveId = start.id,
                        openValves = first,
                        time = 26,
                        connections = connections,
                        cache = cache
                    ) + dfs(
                        valveId = start.id,
                        time = 26,
                        openValves = second,
                        connections = connections,
                        cache = cache
                    )
                    ans.updateAndGet {
                        max(it, value)
                    }
                }
            }
        }
    }
    println(ans.get())
}

private fun calculateConnections(data: Map<String, Valve>): Map<Long, List<Pair<Valve, Int>>> {
    val connections = mutableMapOf<Long, List<Pair<Valve, Int>>>()
    val filteredNodes = data.values.filter { it.name == "AA" || it.flowRate > 0 }.toSet()
    for (valve in filteredNodes) {
        // For each starting valve find the shortest path to any of
        connections[valve.id] = findShortestPaths(from = valve, map = data, includeNodes = filteredNodes)
    }
    return connections
}

private fun findShortestPaths(from: Valve, map: Map<String, Valve>, includeNodes: Set<Valve>): List<Pair<Valve, Int>> {
    // destination map
    val result = mutableListOf<Pair<Valve, Int>>()
    return shortestPath(
        from = from,
        neighbours = { path ->
            path.destination.connectedValves.map { map[it]!! }.asSequence()
        },
        process = { path ->
            if (includeNodes.contains(path.destination)) {
                result += Pair(path.destination, path.pathLength.toInt())
            }
            if (result.size == includeNodes.size) {
                result
            } else {
                null
            }
        }
    ) ?: error("Expected path to all valves to be found")
}

private fun dfs(
    valveId: Long,
    time: Int,
    openValves: Long,
    connections: Map<Long, List<Pair<Valve, Int>>>,
    cache: MutableMap<Any, Int>
): Int {
    var maxValue = 0
    val cacheKey = Triple(openValves, valveId, time)
    val cachedValue = cache[cacheKey]
    if (cachedValue != null) {
        return cachedValue
    }
    for ((next, duration) in connections[valveId]!!) {
        val remainingTime = time - duration - 1
        if (remainingTime <= 0) continue
        val nextId = next.id
        if (nextId and openValves != 0L) continue
        maxValue = max(
            maxValue,
            remainingTime * next.flowRate + dfs(
                nextId,
                remainingTime,
                openValves or nextId,
                connections,
                cache
            )
        )
    }
    cache[cacheKey] = maxValue
    return maxValue
}