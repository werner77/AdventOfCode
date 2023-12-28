package com.behindmedia.adventofcode.year2020.day16

import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing

private fun parseRange(string: String): IntRange {
    val (min, max) = string.split("-").map { it.toInt() }
    return min..max
}

private fun parseOurTicket(yourTicket: String) = yourTicket.split("\n").drop(1).first().split(",").map { it.toInt() }

private fun parseRequirements(requirements: String): MutableMap<String, Pair<IntRange, IntRange>> {
    return requirements.split("\n").filter { it.isNotEmpty() }.fold(mutableMapOf()) { map, line ->
        map.apply {
            val (name, rest) = line.split(": ")
            val (first, second) = rest.split(" or ").map { parseRange(it) }
            this[name] = first to second
        }
    }
}

private fun parseNearbyTicketNumbers(nearbyTickets: String): List<List<Int>> {
    return nearbyTickets.split("\n").drop(1).filter { it.isNotEmpty() }.fold(mutableListOf()) { list, line ->
        list.apply {
            add(line.split(",").map { it.toInt() })
        }
    }
}

fun main() {
    val (requirementsString, ourTicketString, nearbyTicketString) = read("/2020/day16.txt").split("\n\n").map { it.trim() }

    val requirements = parseRequirements(requirementsString)
    val ourTicket = parseOurTicket(ourTicketString)
    val nearbyTickets = parseNearbyTicketNumbers(nearbyTicketString)

    // Part 1
    val (total, validTickets) = part1(nearbyTickets, requirements)
    println(total)

    // Part 2
    println(part2(ourTicket, requirements, validTickets))
}

private fun part1(
    nearbyTickets: List<List<Int>>,
    requirements: MutableMap<String, Pair<IntRange, IntRange>>
): Pair<Int, MutableList<List<Int>>> {
    var total = 0
    val validTickets = mutableListOf<List<Int>>()
    for (ticketNumbers in nearbyTickets) {
        val invalid = ticketNumbers.filter {
            requirements.none { (_, pair) -> it in pair.first || it in pair.second }
        }
        if (invalid.isNotEmpty()) {
            total += invalid.sum()
        } else {
            validTickets += ticketNumbers
        }
    }
    return total to validTickets
}

private fun part2(
    ourTicket: List<Int>,
    requirements: MutableMap<String, Pair<IntRange, IntRange>>,
    validTickets: MutableList<List<Int>>
): Long {
    val candidates =
        ourTicket.indices.fold(defaultMutableMapOf<Int, MutableSet<String>>(true) { mutableSetOf() }) { map, index ->
            for ((name, ranges) in requirements) {
                // Check whether all tickets at this index correspond
                val valid = (0 until validTickets.size).all { k ->
                    validTickets[k][index] in ranges.first || validTickets[k][index] in ranges.second
                }
                if (valid) {
                    map[index] += name
                }
            }
            map
        }

    val settled = mutableMapOf<Int, String>()
    while (candidates.isNotEmpty()) {
        val min = candidates.entries.minBy { it.value.size }
        val name = min.value.single()
        settled[min.key] = name
        for (value in candidates.values) {
            value.remove(name)
        }
        candidates.remove(min.key)
    }

    var result = 1L
    for ((index, name) in settled.entries) {
        if (name.startsWith("departure")) {
            result *= ourTicket[index]
        }
    }

    val ans = settled.entries.fold(1L) { value, (index, name) ->
        if (name.startsWith("departure")) {
            value * ourTicket[index]
        } else {
            value
        }
    }
    return ans
}