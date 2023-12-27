package com.behindmedia.adventofcode.year2020.day12

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.common.RotationDirection.Left
import com.behindmedia.adventofcode.common.RotationDirection.Right

fun main() {
    val data = parseLines("/2020/day12.txt") { line ->
        line
    }
    println(Coordinate.origin.manhattenDistance(process1(data)))
    println(Coordinate.origin.manhattenDistance(process2(data)))
}

private fun process1(data: List<String>): Coordinate {
    var c = Coordinate.origin
    var d = Coordinate.right
    for (instruction in data) {
        val amount = instruction.substring(1).toInt()
        when (instruction[0]) {
            'N' -> c += Coordinate.up * amount
            'S' -> c += Coordinate.down * amount
            'E' -> c += Coordinate.right * amount
            'W' -> c += Coordinate.left * amount
            'L' -> repeat(amount / 90) { d = d.rotate(Left) }
            'R' -> repeat(amount / 90) { d = d.rotate(Right) }
            'F' -> c += d * amount
        }
    }
    return c
}

private fun process2(data: List<String>): Coordinate {
    var c = Coordinate.origin
    var w = Coordinate(10, -1)
    for (instruction in data) {
        val amount = instruction.substring(1).toInt()
        when (instruction[0]) {
            'N' -> w += Coordinate.up * amount
            'S' -> w += Coordinate.down * amount
            'E' -> w += Coordinate.right * amount
            'W' -> w += Coordinate.left * amount
            'L' -> {
                repeat(amount / 90) { w = w.rotate(Left) }
            }
            'R' -> {
                repeat(amount / 90) { w = w.rotate(Right) }
            }
            'F' -> c += w * amount
        }
    }
    return c
}