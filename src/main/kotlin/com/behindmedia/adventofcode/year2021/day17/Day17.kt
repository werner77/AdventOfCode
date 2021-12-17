package com.behindmedia.adventofcode.year2021.day17

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private val regex = """target area: x=([\-0-9]+)\.\.([\-0-9]+), y=([\-0-9]+)\.\.([\-0-9]+)""".toRegex()

private fun reachesTargetArea(min: Coordinate, max: Coordinate, startVelocity: Coordinate): Boolean {
    var x = 0
    var y = 0
    var vx = startVelocity.x
    var vy = startVelocity.y
    while (true) {
        x += vx
        y += vy
        if (vx > 0) vx-- else if (vx < 0) vx++
        vy--
        if (x in min.x..max.x && y in min.y..max.y) {
            return true
        }
        if (x > max.x || y < min.y || (vx == 0 && x < min.x)) {
            return false
        }
    }
}

fun sqrt(a: Int, roundDown: Boolean): Int {
    val s = sqrt(a.toDouble())
    return if (roundDown) floor(s).toInt() else ceil(s).toInt()
}

fun main() {
    val (min, max) = parse("/2021/day17.txt") { string ->
        val components = regex.matchEntire(string.trim())?.groupValues ?: error("Could not match input: $string")
        val x1 = components[1].toInt()
        val x2 = components[2].toInt()
        val y1 = components[3].toInt()
        val y2 = components[4].toInt()
        Pair(Coordinate(min(x1, x2), min(y1, y2)), Coordinate(max(x1, x2), max(y1, y2)))
    }

    // If vx, vy is start velocity then the following is true:
    // - max(y) = vy * (vy + 1) / 2
    // - max(x) = vx * (vx + 1) / 2

    // So to reach target area:
    // - vx >= sqrt(2 * min.x) - 1 && vx <= max.x (if in 1 second the target area would be reached)
    // - vy >= min.y
    // vy needs to be also less than max.x because otherwise it is not back down in time before x exceeds max x

    val minV = Coordinate(
        sqrt(min.x * 2, true) - 1,
        -abs(min.y)
    )
    val maxV = Coordinate(
        max.x,
        max.x
    )
    var maxVy = 0
    var validCount = 0
    for (vx in minV.x..maxV.x) {
        for (vy in minV.y..maxV.y) {
            if (reachesTargetArea(min, max, Coordinate(vx, vy))) {
                maxVy = max(vy, maxVy)
                validCount++
            }
        }
    }
    val maxHeight = (maxVy * (maxVy + 1)) / 2

    // Part 1
    println(maxHeight)

    // Part 2
    println(validCount)
}