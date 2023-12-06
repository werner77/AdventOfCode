package com.behindmedia.adventofcode.year2023.day6

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = read("/2023/day6.txt")

    val (time, dist) = data.split("\n")
    val timeValues = time.split("Time:")[1].splitNonEmptySequence(" ").map { it.toLong() }.toList()
    val distValues = dist.split("Distance:")[1].splitNonEmptySequence(" ").map { it.toLong() }.toList()

    // Part 1
    println(timeValues.withIndex().productOf { (i, t) ->
        (0 until t).count { waitTime -> distTravelled(t, waitTime) > distValues[i]  }
    })

    // Part 2
    val totalTime = timeValues.joinToString(separator = "").toLong()
    val totalDistance = distValues.joinToString(separator = "").toLong()
    val lowerBound = binarySearch(0, totalTime, inverted = true) {
        distTravelled(totalTime, it) > totalDistance
    } ?: error("No value found")

    val upperBound = binarySearch(0, totalTime, inverted = false) {
        distTravelled(totalTime, it) > totalDistance
    } ?: error("No value found")

    // Part 2
    println(upperBound - lowerBound + 1)
}

private fun distTravelled(time: Long, waitTime: Long) = waitTime * (time - waitTime)