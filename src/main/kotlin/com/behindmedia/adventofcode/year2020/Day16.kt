package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.popFirst

class Day16 {

    private class TicketRulePossibility(val ticketRule: TicketRule, val possibleIndexes: MutableSet<Int>) :
        Comparable<TicketRulePossibility> {
        override fun compareTo(other: TicketRulePossibility): Int {
            return this.possibleIndexes.size.compareTo(other.possibleIndexes.size)
        }
    }

    private data class TicketRule(val name: String, val min1: Int, val max1: Int, val min2: Int, val max2: Int) {
        constructor(name: String, range1: Pair<Int, Int>, range2: Pair<Int, Int>) : this(
            name,
            range1.first,
            range1.second,
            range2.first,
            range2.second
        )

        companion object {
            operator fun invoke(name: String, values: String): TicketRule {
                val components = values.split(" or ")
                assert(components.size == 2)
                return TicketRule(name, parseMinMax(components[0]), parseMinMax(components[1]))
            }

            private fun parseMinMax(string: String): Pair<Int, Int> {
                val values = string.split("-")
                assert(values.size == 2)
                val min = values[0].toInt()
                val max = values[1].toInt()
                return Pair(min, max)
            }
        }

        fun isValid(value: Int): Boolean {
            return value in min1..max1 || value in min2..max2
        }
    }

    private class State(val ticketRules: List<TicketRule>, val ownTicket: List<Int>, val nearbyTickets: List<List<Int>>)

    fun part1(input: String): Long {
        val state = parseInput(input)
        return state.nearbyTickets.mapNotNull { ticket ->
            ticket.findInvalidTicketNumber(state.ticketRules)
        }.reduce { acc, i -> acc + i }.toLong()
    }

    fun part2(input: String): Long {
        val state = parseInput(input)
        val validTickets = state.nearbyTickets.filter { it.isValidTicket(state.ticketRules) }

        // Step 1: Find possible positions for all rules: gives a set of possible indexes for each rule
        // Step 2: Find the only permutation which satisfies all these possibilities
        val ticketPositionCount = state.ticketRules.size

        val ticketRulePossibilities: List<TicketRulePossibility> = state.ticketRules.map { ticketRule ->
            val possibleIndexes = (0 until ticketPositionCount)
                .filter { i -> !validTickets.any { !ticketRule.isValid(it[i]) } }
            TicketRulePossibility(ticketRule, possibleIndexes.toMutableSet())
        }

        val permutation = findPermutation(ticketRulePossibilities)

        return permutation.filter { it.ticketRule.name.startsWith("departure") }
            .map { state.ownTicket[it.possibleIndexes.first()].toLong() }
            .reduce { acc, i -> acc * i }
    }

    private fun findPermutation(ticketRulePossibilities: List<TicketRulePossibility>): List<TicketRulePossibility> {
        val pinned = mutableListOf<TicketRulePossibility>()
        val currentPossibilities = ticketRulePossibilities.toMutableList()
        while (currentPossibilities.isNotEmpty()) {
            currentPossibilities.sort()
            val first = currentPossibilities.popFirst() ?: error("Expected element to be present")
            if (first.possibleIndexes.size != 1) {
                error("More than one possibility present or none at all, which should not happen")
            }
            pinned.add(first)
            currentPossibilities.forEach {
                it.possibleIndexes.remove(first.possibleIndexes.first())
            }
        }
        return pinned
    }

    private fun parseInput(
        input: String
    ): State {
        var section = 0
        val ticketRules = mutableListOf<TicketRule>()
        val ownTicket = mutableListOf<Int>()
        val nearbyTickets = mutableListOf<List<Int>>()
        parseLines(input) {
            val line = it.trim()
            when {
                line.isBlank() -> {
                    section += 1
                }
                line == "your ticket:" -> {
                    //ignore
                }
                line == "nearby tickets:" -> {
                    //ignore
                }
                section == 0 -> {
                    val components = line.split(":")
                    assert(components.size == 2)
                    ticketRules.add(TicketRule(name = components[0].trim(), values = components[1].trim()))
                }
                section == 1 -> {
                    ownTicket.addAll(line.split(",").map { n -> n.toInt() })
                }
                section == 2 -> {
                    nearbyTickets.add(line.split(",").map { n -> n.toInt() })
                }
                else -> error("Unexpected section: $section")
            }
        }
        return State(ticketRules, ownTicket, nearbyTickets)
    }

    private fun List<Int>.isValidTicket(ticketRules: List<TicketRule>): Boolean {
        return findInvalidTicketNumber(ticketRules) == null
    }

    private fun List<Int>.findInvalidTicketNumber(ticketRules: List<TicketRule>): Int? {
        return find { ticket ->
            !ticketRules.any { range -> range.isValid(ticket) }
        }
    }
}