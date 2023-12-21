package com.behindmedia.adventofcode.year2023.day21

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.read

fun main() {
    val grid = CharGrid(read("/2023/day21.txt"))

    // Part 1
    println("=====================================")
    println("Part 1: ")
    println(process(grid, 64))

    println("=====================================")

    // Part 2
    println("Part 2: ")
    require(grid.sizeX == grid.sizeY)
    val size = grid.sizeX
    val target = 26501365
    val n = target / size
    val m = target % size
    println()
    println("Looking for quadratic formula y = ax^2 + bx + c, where steps = $m + $size * x")
    println()
    val x = listOf(0L, 1L, 2L)
    val y = x.map {
        process(grid, (m + it * size).toInt())
    }
    for (i in 0 until 3) {
        println(getEquation(x[i], y[i]))
    }
    println()
    println("<=>")
    println()
    val c = y[0]
    val a = (y[2] - 2L * y[1] + c) / 2L
    val b = y[1] - a - c
    println("y = ${a}x^2 + ${b}x + $c")
    println("x = ${n}")
    println()
    println("<=>")
    println()
    val ans = a * n * n + b * n + c
    println("y = $ans")
    println("=====================================")
}

private fun getEquation(x: Long, y: Long): String {
    val builder = StringBuilder()
    builder.append((x * x).let {
        if (it == 1L) {
            "a + "
        } else if (it > 1L) {
            "${it}a + "
        } else {
            ""
        }
    })
    builder.append(x.let {
        if (it == 1L) {
            "b + "
        } else if (it > 1L) {
            "${it}b + "
        } else {
            ""
        }
    })
    builder.append("c")
    builder.append(" = $y")
    return builder.toString()
}

private fun process(
    grid: CharGrid,
    target: Int
): Long {
    val start = grid.single { it.value == 'S' }.key
    val pending = ArrayDeque<Pair<Coordinate, Int>>()
    pending.add(start to 0)
    var result = 0L
    val seen = mutableSetOf<Coordinate>()
    while (pending.isNotEmpty()) {
        val state = pending.removeFirst()
        val (coordinate, steps) = state
        if (coordinate in seen) continue
        seen += coordinate
        if (steps % 2 == target % 2) {
            // this is a result
            result++
        }
        for (neighbour in coordinate.directNeighbours) {
            val value = grid[neighbour.modulo(grid)]
            if ((value == '.' || value == 'S') && steps < target) {
                pending += neighbour to steps + 1
            }
        }
    }
    return result
}

private fun Coordinate.modulo(grid: CharGrid): Coordinate {
    return Coordinate(x.mod(grid.sizeX), y.mod(grid.sizeY))
}
