package com.behindmedia.adventofcode2019

import java.util.*
import kotlin.Comparator

class Day10 {

    private class CoordinateDistance(val coordinate: Coordinate, val distance: Double): Comparable<CoordinateDistance> {
        override fun compareTo(other: CoordinateDistance): Int {
            return this.distance.compareTo(other.distance)
        }
    }

    private class AngleComparator(private val zeroVector: Coordinate): Comparator<Coordinate> {
        override fun compare(o1: Coordinate?, o2: Coordinate?): Int {
            if (o1 == null || o2 == null) {
                throw IllegalStateException("Unexpected")
            }
            return o1.angle(zeroVector).compareTo(o2.angle(zeroVector))
        }
    }

    fun decodeInput(string: String): List<Coordinate> {
        var x = 0
        var y = 0
        return string.toCharArray().fold(mutableListOf()) { list, c ->
            when(c) {
                '.' -> x++
                '#' -> {
                    list.add(Coordinate(x++, y))
                }
                '\n' -> {
                    x = 0
                    y++
                }
                else -> throw IllegalArgumentException("Unexpected character encountered: $c")
            }
            list
        }
    }

    fun findBestLocation(asteroids: List<Coordinate>): Pair<Coordinate, Int> {
        var result: Pair<Coordinate, Int>? = null
        for (i in asteroids.indices) {
            val asteroid1 = asteroids[i]
            val uniqueVectors = mutableSetOf<Coordinate>()
            for (j in asteroids.indices) {
                if (i == j) continue
                val asteroid2 = asteroids[j]
                val vector = asteroid1.vector(asteroid2).normalized()
                uniqueVectors.add(vector)
            }
            if (uniqueVectors.size > (result?.second ?: 0)) {
                result = Pair(asteroid1, uniqueVectors.size)
            }
        }
        return result ?: throw IllegalStateException("No vectors found")
    }

    fun destroyAsteroids(asteroids: List<Coordinate>, laser: Coordinate, eliminationCount: Int = 200): Coordinate? {
        val vectors = mutableMapOf<Coordinate, TreeSet<CoordinateDistance>>()
        var asteroidCount = 0

        for (asteroid in asteroids) {
            if (laser != asteroid) {
                // Normalized vector to the asteroid
                val vector = laser.vector(asteroid).normalized()

                //Distance
                val distance = laser.distance(asteroid)

                val vectorSet = vectors.getOrPut(vector) { TreeSet() }

                // Add the coordinate to the vectorSet, sorted by distance ascending
                vectorSet.add(CoordinateDistance(asteroid, distance))
                asteroidCount++
            }
        }

        // Sort all the possible normalized vectors by their angle to the zeroVector
        val zeroVector = Coordinate(0, -1)
        val sortedVectors = vectors.keys.sortedBy {
            it.angle(zeroVector)
        }

        // Now start eliminating asteroids by looping through the possible vectors sorted by angle ascending
        // until there are no left
        var eliminatedCount = 0
        while (asteroidCount > 0) {
            for (vector in sortedVectors) {
                // Eliminate first
                val vectorSet = vectors[vector]
                val eliminatedAsteroid = vectorSet?.popFirst()
                if (eliminatedAsteroid != null) {
                    eliminatedCount++
                    asteroidCount--
                    if (eliminatedCount == eliminationCount) {
                        return eliminatedAsteroid.coordinate
                    } else if (asteroidCount == 0) {
                        break
                    }
                }
            }
        }
        return null
    }

}