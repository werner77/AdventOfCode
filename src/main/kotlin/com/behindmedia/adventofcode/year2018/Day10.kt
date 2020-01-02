package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.optimumSearch
import kotlin.math.max
import kotlin.math.min

class Day10 {

    data class Entry(val position: Position, val velocity: Position)

    val regex = Regex("position=<(.*)> velocity=<(.*)>")

    fun parseLine(line: String): Entry {
        val matchResult = regex.find(line)
        if (matchResult != null) {
            val first = matchResult.groups[1]?.value
            val second = matchResult.groups[2]?.value

            if (first != null && second != null) {
                return Entry(Position.fromString(first), Position.fromString(second))
            }
        }
        throw IllegalArgumentException("Could not parse string: ${line}")
    }

    data class Position(var x: Int, var y: Int) {
        companion object {
            fun fromString(s: String): Position {
                val values = s.split(" ", ",").filter { !it.isEmpty() }
                if (values.size != 2) {
                    throw IllegalArgumentException("Input string ${s} is not parsable")
                }
                return Position(values[0].toInt(), values[1].toInt())
            }
        }

        fun add(position: Position, multiplier: Int = 1) {
            x += multiplier * position.x
            y += multiplier * position.y
        }

        fun distance(position: Position): Double {
            val diffX = (position.x - x).toDouble()
            val diffY = (position.y - y).toDouble()
            return Math.sqrt(diffX * diffX + diffY * diffY)
        }
    }

    fun List<Entry>.extrapolate(steps: Int) {
        for (entry in this) {
            entry.position.add(entry.velocity, steps)
        }
    }

    fun entropy(entries: List<Entry>): Int {
        val minPoint = Position(Int.MAX_VALUE, Int.MAX_VALUE)
        val maxPoint = Position(Int.MIN_VALUE, Int.MIN_VALUE)
        for (entry in entries) {
            minPoint.x = min(minPoint.x, entry.position.x)
            minPoint.y = min(minPoint.y, entry.position.y)

            maxPoint.x = max(maxPoint.x, entry.position.x)
            maxPoint.y = max(maxPoint.y, entry.position.y)
        }
        return maxPoint.x - minPoint.x + maxPoint.y - minPoint.y
    }

    fun determineMessage(entries: List<Entry>) {
        var lastTime = 0

        val optimumTime = optimumSearch(0, true) {
            val delta = it.toInt() - lastTime
            entries.extrapolate(delta)
            lastTime = it.toInt()
            entropy(entries).toLong()
        }
        print(entries.map { it.position })
        println("Waited for ${optimumTime} seconds")
    }

    fun print(positions: List<Position>) {
        val minPosition = Position(Int.MAX_VALUE, Int.MAX_VALUE)
        val maxPosition = Position(Int.MIN_VALUE, Int.MIN_VALUE)

        val positionSet = mutableSetOf<Position>()

        for (position in positions) {
            minPosition.x = Math.min(minPosition.x, position.x)
            minPosition.y = Math.min(minPosition.y, position.y)
            maxPosition.x = Math.max(maxPosition.x, position.x)
            maxPosition.y = Math.max(maxPosition.y, position.y)
            positionSet.add(position)
        }

        for (y in minPosition.y..maxPosition.y) {
            for (x in minPosition.x..maxPosition.x) {
                if (positionSet.contains(Position(x, y))) {
                    print("#")
                } else {
                    print(" ")
                }
            }
            println()
        }
    }
}