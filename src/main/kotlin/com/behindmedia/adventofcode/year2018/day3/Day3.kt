package com.behindmedia.adventofcode.year2018.day3

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.common.Coordinate.Companion
import kotlin.math.*

private data class Square(val id: Int, val origin: Coordinate, val size: Coordinate) {
    val range: CoordinateRange
        get() = CoordinateRange(origin, size.x, size.y)
}

fun main() {
    val data = parseLines("/2018/day3.txt") { line ->
        val l = line.split(*"# @,:x".toCharArray()).mapNotNull { it.toIntOrNull() }
        Square(id = l[0], origin = Coordinate(l[1], l[2]), size = Coordinate(l[3], l[4]))
    }

    val map = mutableMapOf<Coordinate, Int>()
    for (s in data) {
        for (c in s.range) {
            map[c] = (map[c] ?: 0) + 1
        }
    }

    // part 1
    println(map.count { (_, value) -> value > 1 })

    // part 2
    for (s in data) {
        var valid = true
        for (c in s.range) {
            if (map[c] != 1) {
                valid = false
                break
            }
        }
        if (valid) {
            println(s.id)
            break
        }
    }
}