package com.behindmedia.adventofcode.year2024.day14

import com.behindmedia.adventofcode.common.*

private val pattern = """p=([+\-]?\d+),([+\-]?\d+) v=([+\-]?\d+),([+\-]?\d+)""".toRegex()

private typealias Point = Coordinate

fun main() = timing {
    val data = parseLines("/2024/day14.txt") { line ->
        val (x, y, vx, vy) = pattern.matchEntire(line)?.destructured ?: error("Invalid input line: $line")
        Point(x.toInt(), y.toInt()) to Point(vx.toInt(), vy.toInt())
    }
    val sizeX = 101
    val sizeY = 103

    // Part1
    println(part1(data, sizeX, sizeY))


    // Part2
    println(part2(data, sizeX, sizeY))
}

private fun part1(data: List<Pair<Point, Point>>, sizeX: Int, sizeY: Int): Long {
    val current = data.toMutableList()
    repeat(100) {
        current.move(sizeX, sizeY)
    }
    return current.safetyFactor(sizeX, sizeY)
}

private fun part2(data: List<Pair<Point, Point>>, sizeX: Int, sizeY: Int): Int {
    var t = 0
    var christmasTree: Pair<Int, List<Pair<Point, Point>>>? = null
    val current = data.toMutableList()
    var minEntropy = current.entropy()
    val seen = mutableSetOf<Long>()
    while (true) {
        current.move(sizeX, sizeY)
        t++
        val entropy = current.entropy()
        if (entropy < minEntropy) {
            minEntropy = entropy
            christmasTree = t to current.toList()
        }
        if (!seen.add(entropy) && t > 10_000) {
            break
        }
    }
    val (time, tree) = christmasTree ?: error("Tree not found")
    tree.print(sizeX, sizeY)
    return time
}

private fun List<Pair<Point, Point>>.entropy(): Long {
    var result = 0L
    for (i in this.indices) {
        for (j in i + 1 until this.size) {
            val distance = this[i].first.manhattenDistance(this[j].first)
            result += distance
        }
    }
    return result
}

private fun List<Pair<Point, Point>>.safetyFactor(sizeX: Int, sizeY: Int): Long {
    val quadrantCounts = MutableList(4) { 0L }
    for ((c, _) in this) {
        if (c.x < sizeX / 2 && c.y < sizeY / 2) {
            quadrantCounts[0]++
        } else if (c.x > sizeX / 2 && c.y < sizeY / 2) {
            quadrantCounts[1]++
        } else if (c.x < sizeX / 2 && c.y > sizeY / 2) {
            quadrantCounts[2]++
        } else if (c.x > sizeX / 2 && c.y > sizeY / 2) {
            quadrantCounts[3]++
        }
    }
    return quadrantCounts.product()
}

private fun MutableList<Pair<Point, Point>>.move(sizeX: Int, sizeY: Int) {
    for (i in this.indices) {
        val (c, v) = this[i]
        var c1 = c + v
        if (c1.x >= sizeX ) {
            // Wrap around
            c1 -= Point(sizeX, 0)
        } else if (c1.x < 0) {
            c1 += Point(sizeX, 0)
        }
        if (c1.y >= sizeY) {
            c1 -= Point(0, sizeY)
        } else if (c1.y < 0) {
            c1 += Point(0, sizeY)
        }
        this[i] = c1 to v
    }
}

private fun List<Pair<Point, Point>>.print(sizeX: Int, sizeY: Int) {
    val grid = MutableCharGrid(sizeX, sizeY) {_, _ -> '.' }
    for ((c, _) in this) {
        grid[c] = '#'
    }
    println(grid)
}