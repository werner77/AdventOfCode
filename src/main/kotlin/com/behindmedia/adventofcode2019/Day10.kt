package com.behindmedia.adventofcode2019

import java.util.*
import kotlin.Comparator

class Day10 {

    private class CoordinateDistance(val coordinate: Coordinate, val distance: Double): Comparable<CoordinateDistance> {
        override fun compareTo(other: CoordinateDistance): Int {
            return this.distance.compareTo(other.distance)
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
                if (i != j) {
                    val asteroid2 = asteroids[j]
                    val vector = asteroid1.vector(asteroid2).normalized()
                    uniqueVectors.add(vector)
                }
            }
            if (uniqueVectors.size > (result?.second ?: 0)) {
                result = Pair(asteroid1, uniqueVectors.size)
            }
        }
        return result ?: throw IllegalStateException("No vectors found")
    }

    class AngleComparator(private val zeroVector: Coordinate): Comparator<Coordinate> {

        override fun compare(o1: Coordinate?, o2: Coordinate?): Int {
            if (o1 == null || o2 == null) {
                throw IllegalStateException("Unexpected")
            }
            val zero = Coordinate(0, -1)
            val angle1: Double = o1.angle(zeroVector)
            val angle2: Double = o2.angle(zeroVector)

            return angle1.compareTo(angle2)
        }
    }

    fun destroyAsteroids(asteroids: List<Coordinate>, laser: Coordinate, eliminationCount: Int = 200): Coordinate? {
        val vectors = mutableMapOf<Coordinate, TreeSet<CoordinateDistance>>()
        var asteroidCount = 0
        val possibleVectors = TreeSet<Coordinate>(AngleComparator(Coordinate(0, -1)))

        for (asteroid in asteroids) {
            if (laser != asteroid) {
                // Normalized vector to the asteroid
                val vector = laser.vector(asteroid).normalized()

                //Distance
                val distance = laser.distance(asteroid)

                val vectorSet = vectors.getOrPut(vector) {
                    TreeSet()
                }

                // Add the vector to the set of possible vectors, these are sorted by angle to the zero vector
                possibleVectors.add(vector)

                // Add the coordinate to the vectorSet, sorted by distance ascending
                vectorSet.add(CoordinateDistance(asteroid, distance))
                asteroidCount++
            }
        }

        // Now start eliminating asteroids by looping through the possible vectors sorted by angle ascending
        // until there are no left
        var eliminatedCount = 0
        while (asteroidCount > 0) {
            for (vector in possibleVectors) {
                // Eliminate first
                val vectorSet = vectors[vector]
                val eliminatedAsteroid = vectorSet?.popFirst()
                if (eliminatedAsteroid != null) {
                    eliminatedCount++
                    asteroidCount--
                    if (eliminatedCount == eliminationCount) {
                        return eliminatedAsteroid.coordinate
                    }
                }
            }
        }
        return null
    }

}