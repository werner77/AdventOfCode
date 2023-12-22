package com.behindmedia.adventofcode.year2023.day6

import com.behindmedia.adventofcode.common.findQuadraticRoots
import com.behindmedia.adventofcode.common.productOf
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.splitNonEmptySequence
import com.behindmedia.adventofcode.common.timing
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun main() {
    val data = read("/2023/day6.txt")

    timing {
        val (time, dist) = data.split("\n")
        val timeValues = time.split("Time:")[1].splitNonEmptySequence(" ").map { it.toLong() }.toList()
        val distValues = dist.split("Distance:")[1].splitNonEmptySequence(" ").map { it.toLong() }.toList()

        // Part 1
        println(timeValues.withIndex().productOf { (i, t) ->
            (0 until t).count { waitTime -> distTravelled(t, waitTime) > distValues[i] }
        })

        // Part 2
        val totalTime = timeValues.joinToString(separator = "").toLong()
        val totalDistance = distValues.joinToString(separator = "").toLong()

        // x * (totalTime - x) - totalDistance = 0
        // -x^2 - totalTime * x  - totalDistance = 0
        // a = -1, b = -totalTime, c = -totalDistance
        val (root1, root2) = solveEquation(-1, -totalTime, -totalDistance)

        val upperBound = floor(max(root1, root2)).toLong()
        val lowerBound = ceil(min(root1, root2)).toLong()

        // Part 2
        println(upperBound - lowerBound + 1)
    }
}

private fun solveEquation(a: Long, b: Long, c: Long): List<Double> {
    return findQuadraticRoots(a, b, c)
}

private fun distTravelled(time: Long, waitTime: Long) = waitTime * (time - waitTime)