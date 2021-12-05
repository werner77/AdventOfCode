package com.behindmedia.adventofcode.year2016.day10

import com.behindmedia.adventofcode.common.*
import java.util.*

/**
 bot 162 gives low to bot 67 and high to bot 205
 value 7 goes to bot 32
 */
private val regex1 = """bot ([0-9]+) gives low to (bot|output) ([0-9]+) and high to (bot|output) ([0-9]+)""".toRegex()
private val regex2 = """value ([0-9]+) goes to bot ([0-9]+)""".toRegex()

private data class Node(val id: Int, val isBot: Boolean = true)
private data class Action(val source: Node, val lowDest: Node, val highDest: Node)

fun main() {
    // Key = bot, value is chip numbers
    val state = mutableMapOf<Node, TreeSet<Int>>().withDefault { TreeSet() }
    val actions = mutableListOf<Action>()
    parseLines("/2016/day10.txt") { line ->
        val match1 = regex1.matchEntire(line)
        val match2 = regex2.matchEntire(line)
        if (match1 != null) {
            val action = Action(
                Node(match1.groupValues[1].toInt(), true),
                Node(id = match1.groupValues[3].toInt(), isBot = match1.groupValues[2] == "bot"),
                Node(match1.groupValues[5].toInt(), match1.groupValues[4] == "bot")
            )
            actions.add(action)
        } else if (match2 != null) {
            val value = match2.groupValues[1].toInt()
            val bot = match2.groupValues[2].toInt()
            state.getValue(Node(bot)).add(value)
        } else {
            error("Invalid input: $line")
        }
    }
    while (true) {
        actions.sortBy {
            state[it.source]?.size ?: 0
        }
        val current = actions.removeLastOrNull() ?: break
        val currentValues = state[current.source] ?: break
        if (currentValues.size != 2) error("Expected 2 values")
        val lowValue = currentValues.first()
        val highValue = currentValues.last()
        if (lowValue == 17 && highValue == 61) {
            println(current.source.id)
        }
        state.getValue(current.lowDest).add(lowValue)
        state.getValue(current.highDest).add(highValue)
    }

    val product = state.getValue(Node(0, false)).only() *
            state.getValue(Node(1, false)).only() *
            state.getValue(Node(2, false)).only()

    println(product)
}