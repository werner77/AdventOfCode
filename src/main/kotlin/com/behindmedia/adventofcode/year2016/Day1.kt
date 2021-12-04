package com.behindmedia.adventofcode.year2016

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.RotationDirection
import com.behindmedia.adventofcode.common.parse
import com.behindmedia.adventofcode.common.splitNonEmptySequence
import kotlin.math.abs

fun main() {
    val instructions = parse("/2016/day1.txt") { input ->
        input.splitNonEmptySequence(",", " ")
    }

    val visited = mutableSetOf<Coordinate>()
    var position = Coordinate(0,0)
    visited.add(position)
    var direction = Coordinate.up

    for (instr in instructions) {
        val count = instr.substring(1).toInt()
        direction = if (instr.startsWith("R")) {
            // Turn right
            direction.rotate(RotationDirection.Right)
        } else if (instr.startsWith("L")) {
            // Turn left
            direction.rotate(RotationDirection.Left)
        } else {
            error("Unexpected instruction: $instr")
        }
        for (i in 0 until count) {
            position += direction
            if (visited.contains(position)) {
                println("Found position: $position")
                println(abs(position.x) + abs(position.y))
            }
            visited.add(position)
        }
    }
    println("Found position: $position")
    println(abs(position.x) + abs(position.y))
}