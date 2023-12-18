package com.behindmedia.adventofcode.year2017.day19

import com.behindmedia.adventofcode.common.*

fun main() {
    val map = parseLines("/2017/day19.txt") { line ->
        line
    }.foldIndexed(mutableMapOf<Coordinate, Char>()) { y, map, value ->
        for ((x, c) in value.withIndex()) {
            map[Coordinate(x, y)] = c
        }
        map
    }

    val start = map.entries.filter { it.value == '|' }.minByOrNull { it.key.y } ?: error("Start point not found")
    var current = start.key
    var currentDirection = Coordinate.down
    val letters = mutableListOf<Char>()
    var steps = 0
    while (true) {
        val c = map[current] ?: break
        if (c == ' ') break
        when (c) {
            '|' -> {
                // continue direction
            }
            '+' -> {
                val next = current.directNeighbours.filter { map[it] != ' ' && (current - it) != currentDirection }.single()
                currentDirection = next - current
            }
            '-' -> {
                // Continue direction
            }
            else -> {
                letters += c
            }
        }
        current += currentDirection
        steps++
    }
    println(letters.joinToString(""))
    println(steps)
}