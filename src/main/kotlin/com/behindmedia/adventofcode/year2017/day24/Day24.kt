package com.behindmedia.adventofcode.year2017.day24

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

class Component(val ports: List<Int>) {
    val value: Int
        get() = ports.sum()

    fun otherPort(value: Int): Int {
        return if (ports[0] == value) {
            ports[1]
        } else {
            ports[0]
        }
    }
}

private fun solve1(port: Int, connections: Map<Int, List<Component>>, available: MutableSet<Component>): Int {
    var maxValue = 0
    connections[port]?.forEach { component ->
        if (component in available) {
            available -= component
            maxValue = max(maxValue, solve1(component.otherPort(port), connections, available) + component.value)
            available += component
        }
    }
    return maxValue
}

private fun solve2(
    port: Int,
    connections: Map<Int, List<Component>>,
    available: MutableSet<Component>
): Pair<Int, Int> {
    var maxValue = Pair(0, 0)
    connections[port]?.forEach { component ->
        if (component in available) {
            available -= component
            maxValue = max(solve2(component.otherPort(port), connections, available) + Pair(1, component.value), maxValue)
            available += component
        }
    }
    return maxValue
}

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> = Pair(this.first + other.first, this.second + other.second)

fun max(first: Pair<Int, Int>, second: Pair<Int, Int>): Pair<Int, Int> {
    return if (first.first > second.first) {
        first
    } else if (second.first > first.first) {
        second
    } else if (first.second > second.second) {
        first
    } else {
        second
    }
}

private fun solve1(connections: Map<Int, List<Component>>): Int {
    return solve1(0, connections, connections.values.flatten().toMutableSet())
}

private fun solve2(connections: Map<Int, List<Component>>): Int {
    return solve2(0, connections, connections.values.flatten().toMutableSet()).second
}

fun main() {
    val data = parseLines("/2017/day24.txt") { line ->
        val c = line.split("/")
        require(c.size == 2)
        Component(listOf(c[0].toInt(), c[1].toInt()))
    }
    val map = defaultMutableMapOf<Int, MutableList<Component>> { mutableListOf() }
    for (c in data) {
        for (p in c.ports) {
            map.getOrPutDefault(p) += c
        }
    }
    println(solve1(map))
    println(solve2(map))
}