package com.behindmedia.adventofcode2019

import java.lang.Math.abs
import kotlin.math.min

class Day3 {

    fun smallestDistance(encoding1: String, encoding2: String): Int {
        return findSmallestDistance(pathFromEncoding(encoding1), pathFromEncoding(encoding2))
    }

    fun smallestSteps(encoding1: String, encoding2: String): Int {
        return findSmallestSteps(stepsFromEncoding(encoding1), stepsFromEncoding(encoding2))
    }

    fun pathFromEncoding(encoding: String): Set<Coordinate> {
        val result = mutableSetOf<Coordinate>()
        var currentCoordinate = Coordinate(0, 0)
        val components = encoding.split(",")
        for (component in components) {
            val amount = component.substring(1).toInt()
            val direction =  component[0]
            when (direction) {
                'R' ->
                    for (i in 1..amount) {
                        currentCoordinate = Coordinate(currentCoordinate.x + 1, currentCoordinate.y)
                        result.add(currentCoordinate)
                    }
                'D' ->
                    for (i in 1..amount) {
                        currentCoordinate = Coordinate(currentCoordinate.x, currentCoordinate.y - 1)
                        result.add(currentCoordinate)
                    }
                'L' ->
                    for (i in 1..amount) {
                        currentCoordinate = Coordinate(currentCoordinate.x - 1, currentCoordinate.y)
                        result.add(currentCoordinate)
                    }
                'U' ->
                    for (i in 1..amount) {
                        currentCoordinate = Coordinate(currentCoordinate.x, currentCoordinate.y + 1)
                        result.add(currentCoordinate)
                    }
                else ->
                    throw IllegalArgumentException("Found unknown direction: $direction")
            }
        }
        return result
    }

    fun stepsFromEncoding(encoding: String): Map<Coordinate, Int> {
        val result = mutableMapOf<Coordinate, Int>()
        var currentCoordinate = Coordinate(0, 0)
        var stepCount = 0
        val components = encoding.split(",")
        for (component in components) {
            val amount = component.substring(1).toInt()
            val direction =  component[0]
            when (direction) {
                'R' ->
                    for (i in 1..amount) {
                        stepCount++
                        currentCoordinate = Coordinate(currentCoordinate.x + 1, currentCoordinate.y)
                        result.putIfAbsent(currentCoordinate, stepCount)
                    }
                'D' ->
                    for (i in 1..amount) {
                        stepCount++
                        currentCoordinate = Coordinate(currentCoordinate.x, currentCoordinate.y - 1)
                        result.putIfAbsent(currentCoordinate, stepCount)
                    }
                'L' ->
                    for (i in 1..amount) {
                        stepCount++
                        currentCoordinate = Coordinate(currentCoordinate.x - 1, currentCoordinate.y)
                        result.putIfAbsent(currentCoordinate, stepCount)
                    }
                'U' ->
                    for (i in 1..amount) {
                        stepCount++
                        currentCoordinate = Coordinate(currentCoordinate.x, currentCoordinate.y + 1)
                        result.putIfAbsent(currentCoordinate, stepCount)
                    }
                else ->
                    throw IllegalArgumentException("Found unknown direction: $direction")
            }
        }
        return result
    }

    fun findSmallestDistance(firstWire: Set<Coordinate>, secondWire: Set<Coordinate>): Int {
        var minDistance = Int.MAX_VALUE
        for (coordinate1 in firstWire) {
            if (secondWire.contains(coordinate1)) {
                minDistance = min(minDistance, abs(coordinate1.x) + abs(coordinate1.y))
            }
        }
        return minDistance
    }

    fun findSmallestSteps(firstWire: Map<Coordinate, Int>, secondWire: Map<Coordinate, Int>): Int {
        var minSteps = Int.MAX_VALUE
        for ((coordinate1,stepCount1) in firstWire) {
            val stepCount2 = secondWire[coordinate1]
            if (stepCount2 != null) {
                minSteps = min(minSteps, stepCount1 + stepCount2)
            }
        }
        return minSteps
    }
}