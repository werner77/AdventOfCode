package com.behindmedia.adventofcode.year2015.day13

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class Arrangement(val first: String, val second: String, val increment: Int) {
    companion object {
        operator fun invoke(string: String): Arrangement {
            val components = string.splitNonEmptySequence(" ", ".").toList()
            val firstName = components.first()
            val lastName = components.last()
            val gain = components[2] == "gain"
            val amount = components[3].toInt()
            return Arrangement(firstName, lastName, if (gain) amount else -amount)
        }
    }
}

private fun calculateTotalHappiness(arrangements: List<String>, happinessMap: Map<Pair<String, String>, Int>): Int {
    var total = 0
    for (i in arrangements.indices) {
        val pair1 = Pair(arrangements[i], arrangements[(i + 1) % arrangements.size])
        val pair2 = Pair(arrangements[(i + 1) % arrangements.size], arrangements[i])
        val increment1 = happinessMap[pair1] ?: error("Pair not found")
        val increment2 = happinessMap[pair2] ?: error("Pair not found")
        total += increment1 + increment2
    }
    return total
}

private fun findMaxHappiness(persons: Set<String>, happinessMap: Map<Pair<String, String>, Int>): Int {
    var maxValue = Int.MIN_VALUE
    persons.permute {
        maxValue = max(maxValue, calculateTotalHappiness(it, happinessMap))
        null
    }
    return maxValue
}

// Mallory would lose 51 happiness units by sitting next to Carol.
fun main() {
    val data = parseLines("/2015/day13.txt") { line ->
        Arrangement(line)
    }

    val happinessMap = data.fold(mutableMapOf<Pair<String, String>, Int>()) { map, arrangement ->
        map[Pair(arrangement.first, arrangement.second)] = arrangement.increment
        map
    }

    val persons = data.fold(mutableSetOf<String>()) { set, arrangement ->
        set += arrangement.first
        set += arrangement.second
        set
    }

    // Part 1
    println(findMaxHappiness(persons, happinessMap))

    for (person in persons) {
        happinessMap[Pair("Me", person)] = 0
        happinessMap[Pair(person, "Me")] = 0
    }
    persons += "Me"

    // Part 2
    println(findMaxHappiness(persons, happinessMap))
}