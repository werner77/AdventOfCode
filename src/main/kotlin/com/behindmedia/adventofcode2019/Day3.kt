package com.behindmedia.adventofcode2019

import kotlin.math.min

class Day3 {

    fun smallestDistance(encoding1: String, encoding2: String): Int {
        return findSmallestDistance(stepsFromEncoding(encoding1), stepsFromEncoding(encoding2))
    }

    fun smallestSteps(encoding1: String, encoding2: String): Int {
        return findSmallestSteps(stepsFromEncoding(encoding1), stepsFromEncoding(encoding2))
    }

    private fun stepsFromEncoding(encoding: String): Map<Coordinate, Int> {
        val result = mutableMapOf<Coordinate, Int>()
        var currentCoordinate = Coordinate(0, 0)
        var stepCount = 0
        val components = encoding.split(",")
        for (component in components) {
            val amount = component.substring(1).toInt()
            val vector = when (val direction = component[0]) {
                'R' ->
                    Coordinate.right
                'D' ->
                    Coordinate.down
                'L' ->
                    Coordinate.left
                'U' ->
                    Coordinate.up
                else ->
                    throw IllegalArgumentException("Found unknown direction: $direction")
            }
            for (i in 1..amount) {
                currentCoordinate = currentCoordinate.offset(vector)
                result.putIfAbsent(currentCoordinate, ++stepCount)
            }
        }
        return result
    }

    private fun findMinimum(firstWire: Map<Coordinate, Int>,
                            secondWire: Map<Coordinate, Int>,
                            selector: (Coordinate, Int, Int) -> Int): Int {
        var minimum = Int.MAX_VALUE
        for ((coordinate, stepCount1) in firstWire) {
            val stepCount2 = secondWire[coordinate]
            if (stepCount2 != null) {
                minimum = min(minimum, selector(coordinate, stepCount1, stepCount2))
            }
        }
        return minimum
    }

    private fun findSmallestDistance(firstWire: Map<Coordinate, Int>, secondWire: Map<Coordinate, Int>): Int {
        return findMinimum(firstWire, secondWire) { coordinate, _, _ ->
            coordinate.manhattenDistance(Coordinate.origin)
        }
    }

    private fun findSmallestSteps(firstWire: Map<Coordinate, Int>, secondWire: Map<Coordinate, Int>): Int {
        return findMinimum(firstWire, secondWire) { _, stepCount1, stepCount2 ->
            stepCount1 + stepCount2
        }
    }
}