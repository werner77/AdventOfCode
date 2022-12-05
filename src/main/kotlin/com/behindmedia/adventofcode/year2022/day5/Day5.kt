package com.behindmedia.adventofcode.year2022.day5

import com.behindmedia.adventofcode.common.*

private data class Command(val amount: Int, val from: Int, val to: Int)

fun main() {
    val commandRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()
    val commands = mutableListOf<Command>()
    var parsedArrangement = false
    val arrangement = mutableMapOf<Int, MutableList<Char>>()
    parseLines("/2022/day5.txt") { line ->
        if (line.isBlank()) {
            parsedArrangement = true
        } else if (!parsedArrangement) {
            for (i in 0 until 9) {
                val itemIndex = 1 + 4 * i
                val item = line[itemIndex]
                if (item in 'A'..'Z') {
                    val list = arrangement.getOrPut(i + 1) { mutableListOf() }
                    list.add(0, item)
                }
            }
        } else {
            val match = commandRegex.matchEntire(line) ?: error("Could not match line")
            commands.add(Command(match.groupValues[1].toInt(), match.groupValues[2].toInt(), match.groupValues[3].toInt()))
        }
    }
    part1(arrangement.deepCopy(), commands)
    part2(arrangement.deepCopy(), commands)
}

private fun Map<Int, MutableList<Char>>.deepCopy(): MutableMap<Int, MutableList<Char>> {
    return this.entries.fold(mutableMapOf<Int, MutableList<Char>>()) { map, entry ->
        map.apply {
            map[entry.key] = entry.value.toMutableList()
        }
    }
}

private fun part1(
    arrangement: MutableMap<Int, MutableList<Char>>,
    commands: MutableList<Command>
) {
    solve(arrangement, commands, false)
}

private fun part2(
    arrangement: MutableMap<Int, MutableList<Char>>,
    commands: MutableList<Command>
) {
    solve(arrangement, commands, true)
}

private fun solve(
    arrangement: MutableMap<Int, MutableList<Char>>,
    commands: MutableList<Command>,
    reverseOrder: Boolean
) {
    val destination = arrangement.toMutableMap()
    for (command in commands) {
        val fromItems = destination[command.from] ?: error("No from items found")
        val toItems = destination[command.to] ?: error("No to items found")
        val itemsToMove = mutableListOf<Char>()
        for (i in 0 until command.amount) {
            itemsToMove += fromItems.removeLast()
        }
        if (reverseOrder) {
            itemsToMove.reverse()
        }
        toItems += itemsToMove
    }
    var ans = ""
    for (i in 1..9) {
        ans += destination[i]?.lastOrNull() ?: ""
    }
    println(ans)
}
