package com.behindmedia.adventofcode.year2025.day10

import com.behindmedia.adventofcode.common.*
import com.microsoft.z3.Context
import com.microsoft.z3.IntExpr
import com.microsoft.z3.IntNum
import com.microsoft.z3.Status
import kotlin.Int

private val pattern1 = """\[[.#]+]""".toRegex()
private val pattern2 = """\([\d,]+\)""".toRegex()
private val pattern3 = """\{[\d,]+}""".toRegex()

private data class Configuration(val pattern: Int, val buttons: List<List<Int>>, val joltages: List<Int>) {
    val buttonMasks: List<Int> by lazy { buttons.map { it.toBitMask() } }

    val sortedButtons: List<List<Int>> by lazy {
        buttons.sortedByDescending { it.size }
    }
}

private fun String.chopped() = substring(1, length - 1)

private fun String.toBitMask(): Int {
    var mask = 0
    for (i in 0 until length) {
        if (this[i] == '#') {
            mask = mask or (1 shl i)
        }
    }
    return mask
}

private fun List<Int>.toBitMask(): Int {
    var mask = 0
    for (i in this) {
        mask = mask or (1 shl i)
    }
    return mask
}

private typealias State = Int

private fun solve1(configuration: Configuration): Int {
    // Find the min number of button presses needed
    val pending = ArrayDeque<Path<State>>()
    val seen = mutableSetOf<State>()
    pending += Path(0, 0, null)
    seen += 0
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        val s = current.destination
        val l = current.length
        if (s == configuration.pattern) {
            return l
        }
        for (b in configuration.buttonMasks) {
            val newState = s xor b
            if (seen.add(newState)) {
                pending += Path(newState, l + 1, current)
            }
        }
    }
    error("No path found")
}

private fun solve2(config: Configuration): Int = Context().use { ctx ->
    val solver = ctx.mkOptimize()
    val zero = ctx.mkInt(0)

    // Counts number of presses for each button, and ensures it is positive.
    val buttons = config.buttons.indices
        .map { ctx.mkIntConst("button#$it") }
        .onEach { button -> solver.Add(ctx.mkGe(button, zero)) }
        .toTypedArray()

    // For each joltage counter, require that the sum of presses of all buttons that increment it is equal to the
    // target value specified in the config.
    config.joltages.forEachIndexed { counter, targetValue ->
        val buttonsThatIncrement = config.buttons
            .withIndex()
            .filter { (_, counters) -> counter in counters }
            .map { buttons[it.index] }
            .toTypedArray()
        val target = ctx.mkInt(targetValue)
        val sumOfPresses = ctx.mkAdd(*buttonsThatIncrement) as IntExpr
        solver.Add(ctx.mkEq(sumOfPresses, target))
    }

    val presses = ctx.mkIntConst("presses")
    solver.Add(ctx.mkEq(presses, ctx.mkAdd(*buttons)))
    solver.MkMinimize(presses)

    if (solver.Check() != Status.SATISFIABLE) error("No solution found for machine: $config.")
    solver.model.evaluate(presses, false).let { it as IntNum }.int
}

private fun solve(fileName: String, part: Int): Int {
    val data = parseLines("/2025/$fileName") { line ->
        val m1 = pattern1.findAll(line)
        require(m1.count() == 1)
        val m2 = pattern2.findAll(line)
        require(m2.count() > 1)
        val m3 = pattern3.findAll(line)
        require(m3.count() == 1)
        val pattern = m1.first().value.chopped()
        val buttons = m2.map { it.value.chopped().split(",").map { i -> i.toInt() } }.toList()
        val joltages = m3.first().value.chopped().split(",").map { it.toInt() }
        Configuration(pattern.toBitMask(), buttons, joltages)
    }
    return if (part == 1) data.sumOf { solve1(it) } else data.sumOf { solve2(it) }
}


fun main() {
    for (part in 1..2) {
        println(solve("day10-sample1.txt", part))
        println(solve("day10.txt", part))
    }
}
