package com.behindmedia.adventofcode.year2015.day14

import com.behindmedia.adventofcode.common.*

data class Flyer(val name: String, val speed: Int, val flySeconds: Int, val restSeconds: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Flyer
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

fun simulate(flyers: List<Flyer>, maxTime: Int): Pair<Int, Int> {
    val positions = defaultMutableMapOf<Flyer, Int> { 0 }
    val points = defaultMutableMapOf<Flyer, Int> { 0 }
    val currentFlyers = flyers.fold(mutableMapOf<Flyer, Int>()) { map, flyer ->
        map[flyer] = flyer.flySeconds
        map
    }
    val currentResters = mutableMapOf<Flyer, Int>()
    for (t in 0 until maxTime) {
        val flyerIterator = currentFlyers.iterator()
        while(flyerIterator.hasNext()) {
            val entry = flyerIterator.next()
            if (entry.value == t) {
                flyerIterator.remove()
                currentResters[entry.key] = t + entry.key.restSeconds
            } else {
                positions[entry.key] += entry.key.speed
            }
        }
        val resterIterator = currentResters.iterator()
        while (resterIterator.hasNext()) {
            val entry = resterIterator.next()
            if (entry.value == t) {
                resterIterator.remove()
                currentFlyers[entry.key] = t + entry.key.flySeconds
                positions[entry.key] += entry.key.speed
            }
        }

        var maxPos = Int.MIN_VALUE
        for (entry in positions.entries.sortedByDescending { it.value }) {
            if (entry.value >= maxPos) {
                maxPos = entry.value
                points[entry.key] += 1
            }
        }
    }
    return Pair(positions.maxOf { it.value }, points.maxOf { it.value })
}


// Dancer can fly 27 km/s for 5 seconds, but then must rest for 132 seconds.
fun main() {
    val data = parseLines("/2015/day14.txt") { line ->
        parseInput(line)
    }

    data.forEach { println(it) }
    println(simulate(data, 2503))
}

private fun parseInput(line: String): Flyer {
    val components = line.splitNonEmptySequence(" ", ".", ",").toList()
    val name = components.first()
    val speed = components[3].toInt()
    val flySeconds = components[6].toInt()
    val restSeconds = components[13].toInt()
    return Flyer(name, speed, flySeconds, restSeconds)
}