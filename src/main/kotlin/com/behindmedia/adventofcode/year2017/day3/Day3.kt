package com.behindmedia.adventofcode.year2017.day3

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parseLines("/2017/day3.txt") { line ->
        line.toInt()
    }
    val number = data[0]

    // Find the greatest square less than the number and

    val root = sqrt(number.toDouble()).toInt()

    // Grid = (root + 1, root + 1)

    // Center = 1

    val remainder = number - (root * root)

    var c = Coordinate(root + 1, root)
    var delta = Coordinate.up

    for (i in 0 until remainder) {
        c += delta
        if (c.x == 0 || c.y == 0) {
            // Rotate delta
            delta = delta.rotate(RotationDirection.Left)
        }
    }

    println("Final coordinate: $c")

    val d = c.manhattenDistance(Coordinate((root + 1) / 2, (root + 1) / 2))

    // Part 1
    println("Manhatten distance: $d.")


    // Part 2
}