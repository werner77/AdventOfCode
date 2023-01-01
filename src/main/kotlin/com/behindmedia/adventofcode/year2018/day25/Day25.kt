package com.behindmedia.adventofcode.year2018.day25

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun List<Int>.distance(to: List<Int>): Int {
    return this.withIndex().sumOf { (i, v) -> abs(v - to[i]) }
}

private class Constellation(initialItem: List<Int>) {
    private val items = mutableListOf(initialItem)

    fun merge(other: Constellation) {
        items.addAll(other.items)
    }

    fun distance(other: Constellation): Int {
        return this.items.minOf { c1 ->
            other.items.minOf { c2 ->
                c1.distance(c2)
            }
        }
    }
}

private fun merge(constellations: List<Constellation>): List<Constellation> {
    val result = mutableListOf<Constellation>()
    val available = constellations.toMutableSet()
    while (available.isNotEmpty()) {
        val current = available.popFirst() ?: error("No constellation found")
        val others = available.filter { it.distance(current) <= 3 }
        others.forEach {
            current.merge(it)
            available.remove(it)
        }
        result += current
    }
    return result
}

fun main() {
    val constellations = parseLines("/2018/day25.txt") { line ->
        Constellation(line.split(",").map { it.toInt() })
    }
    var current = constellations
    while (true) {
        val next = merge(current)
        if (current.size == next.size) break
        current = next
    }
    println(current.size)
}