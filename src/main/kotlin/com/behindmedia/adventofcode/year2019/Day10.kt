package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.popFirst
import java.util.*

class Day10 {

    /**
     * This class describes a tuple of coordinate and its distance to another coordinate,
     * sorted by the distance ascending
     */
    private class CoordinateDistance(val coordinate: Coordinate, val distance: Double): Comparable<CoordinateDistance> {
        override fun compareTo(other: CoordinateDistance): Int {
            return this.distance.compareTo(other.distance)
        }
    }

    /**
     * Function to decode the string input of the puzzle to a list of coordinates (describing all the asteroids)
     */
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

    /**
     * Finds the best location (puzzle1) for positioning the laser.
     * Returns the coordinate and the number of asteroids it can directly shoot from that location.
     */
    fun findBestLocation(asteroids: List<Coordinate>): Pair<Coordinate, Int> {
        var result: Pair<Coordinate, Int>? = null
        for (i in asteroids.indices) {
            val asteroid1 = asteroids[i]

            // Unique normalized vectors to other asteroid using asteroid1 as base
            val uniqueVectors = mutableSetOf<Coordinate>()
            for (j in asteroids.indices) {
                // Skip if its the same asteroid
                if (i == j) continue

                val asteroid2 = asteroids[j]

                // Determine the vector and normalize its distance using the greatest common denominator for x and y
                // This will ensure that two vectors of different magnitude, but with the same direction will result
                // in the same vector
                val vector = asteroid1.vector(asteroid2).normalized()

                // Add the vector to the set
                uniqueVectors.add(vector)
            }

            // Take this asteroid if the number of uniqueVectors is greater than the current result
            if (uniqueVectors.size > (result?.second ?: 0)) {
                result = Pair(asteroid1, uniqueVectors.size)
            }
        }
        return result ?: throw IllegalStateException("No vectors found")
    }

    fun destroyAsteroids(asteroids: List<Coordinate>, laser: Coordinate, eliminationCount: Int = 200): Coordinate? {
        val vectors = mutableMapOf<Coordinate, SortedSet<CoordinateDistance>>()
        var asteroidCount = 0

        for (asteroid in asteroids) {
            if (laser != asteroid) {
                // Normalized vector to the asteroid
                val vector = laser.vector(asteroid).normalized()

                //Distance
                val distance = laser.distance(asteroid)

                val vectorSet = vectors.getOrPut(vector) { sortedSetOf() }

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