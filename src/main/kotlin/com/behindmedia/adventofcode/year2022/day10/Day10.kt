package com.behindmedia.adventofcode.year2022.day10

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2022.day10.Operation.addx
import com.behindmedia.adventofcode.year2022.day10.Operation.noop

private enum class Operation {
    noop, addx;
}

private data class Command(val operation: Operation, val amount: Int) {
    fun invoke(currentValue: Int): Int = currentValue + amount
    val cycleCount: Int
        get() = when (operation) {
            noop -> 1
            addx -> 2
        }
}

fun main() {
    var cycle = 0
    // Map of cycle at the end of which the execution occurs to the command which is executed
    val commands = parseLines("/2022/day10.txt") { line ->
        val components = line.splitNonEmptySequence(" ").toList()
        Command(Operation.valueOf(components[0]), components.getOrNull(1)?.toInt() ?: 0)
    }.fold(mutableMapOf<Int, Command>()) { map, command ->
        map.apply {
            cycle += command.cycleCount
            put(cycle, command)
        }
    }
    part1(commands)
    part2(commands)
}

private fun part1(
    commands: Map<Int, Command>
) {
    val maxCycle = commands.keys.max()
    var ans = 0
    var currentValue = 1
    var nextSignal = 20
    for (cycle in 1..maxCycle) {
        if (cycle == nextSignal) {
            val signalStrength = nextSignal * currentValue
            ans += signalStrength
            nextSignal += 40
        }
        currentValue = commands[cycle]?.invoke(currentValue) ?: currentValue
    }
    println(ans)
}

private fun part2(
    commands: Map<Int, Command>
) {
    val maxCycle = commands.keys.max()
    val map = mutableMapOf<Coordinate, Char>()
    var spriteCoordinate = 0
    val gridWith = 40
    for (cycle in 1..maxCycle) {
        val currentPixel = Coordinate((cycle - 1) % gridWith, (cycle - 1) / gridWith)
        if ((0 until 3).map { spriteCoordinate + it }.contains(currentPixel.x)) {
            map[currentPixel] = '#'
        }
        spriteCoordinate = commands[cycle]?.invoke(spriteCoordinate) ?: spriteCoordinate
    }
    map.printMap(' ')
}