package com.behindmedia.adventofcode2019

import org.junit.Test
import java.util.*
import kotlin.Comparator
import kotlin.IllegalStateException
import kotlin.math.PI
import kotlin.math.max
import kotlin.test.assertEquals

class Day10Test {

    @Test
    fun test() {

        // .#....#.###.........#..##.###.#.....##...

        val asteroids = parseAsteroids()

        //val vectorCount = mutableMapOf<Coordinate, Int>()

        var bestCount = 0
        var bestCoordinate = Coordinate.origin

        for (i in 0 until asteroids.size) {
            val asteroid1 = asteroids[i]
            val uniqueVectors = mutableSetOf<Coordinate>()

            for (j in 0 until asteroids.size) {
                if (i != j) {
                    val asteroid2 = asteroids[j]
                    val vector = asteroid1.vectorTo(asteroid2).normalized()
                    if (!uniqueVectors.contains(vector)) {
                        uniqueVectors.add(vector)
                    }
                }
            }
            if (uniqueVectors.size > bestCount) {
                bestCoordinate = asteroid1
                bestCount = max(bestCount, uniqueVectors.size)
            }
        }

        println(bestCount)
        println("Best coordinate: (${bestCoordinate.x},${bestCoordinate.y})")
    }

    private fun parseAsteroids(): List<Coordinate> {
        val characters = read("/day10.txt").toCharArray()

        val asteroids = mutableListOf<Coordinate>()
        var x = 0
        var y = 0

        for (c in characters) {

            when (c) {
                '.' -> x++
                '#' -> {
                    asteroids.add(Coordinate(x, y))
                    x++
                }
                '\n' -> {
                    x = 0
                    y++
                }
                else -> throw IllegalStateException("Unknown character")
            }
        }
        return asteroids
    }

    class CoordinateVector(val coordinate: Coordinate, val distance: Double): Comparable<CoordinateVector> {
        override fun compareTo(other: CoordinateVector): Int {
            return this.distance.compareTo(other.distance)
        }
    }

    @Test
    fun puzzle2() {
        val coordinate = findAsteroid()
        println(coordinate)
    }

    @Test
    fun angle() {

        assertEquals(PI/4.0, Coordinate(1, -1).angle())
        assertEquals(PI * 3.0/4.0, Coordinate(1, 1).angle())
        assertEquals(PI * 5.0/4.0, Coordinate(-1, 1).angle())
        assertEquals(PI * 7.0/4.0, Coordinate(-1, -1).angle())

//        println(Coordinate(1, 0).angle())
//        println(Coordinate(-1, 0).angle())
//        println(Coordinate(0, 1).angle())
//        println(Coordinate(0, -1).angle())
    }

    class AngleComparator: Comparator<Coordinate> {
        override fun compare(o1: Coordinate?, o2: Coordinate?): Int {
            if (o1 == null || o2 == null) {
                throw IllegalStateException("Unexpected")
            }
            val angle1: Double = o1.angle()
            val angle2: Double = o2.angle()

            return angle1.compareTo(angle2)
        }
    }

    fun findAsteroid(): Coordinate? {
        val asteroids = parseAsteroids()
        val laserCoordinate = Coordinate(28,29)

        val vectors = mutableMapOf<Coordinate, TreeSet<CoordinateVector>>()
        var asteroidCount = 0

        var maxX = 0
        var maxY = 0

        val possibleVectors = TreeSet<Coordinate>(AngleComparator())

        for (asteroid in asteroids) {
            if (laserCoordinate != asteroid) {
                val vector = laserCoordinate.vectorTo(asteroid).normalized()
                val distance = laserCoordinate.distance(asteroid)
                val vectorSet = vectors.getOrPut(vector) {
                    TreeSet<CoordinateVector>()
                }
                vectorSet.add(CoordinateVector(asteroid, distance))
                asteroidCount++

                maxX = max(maxX, asteroid.x)
                maxY = max(maxY, asteroid.y)
            }
        }

        var eliminatedCount = 0
        while (asteroidCount > 0) {
            for (vector in possibleVectors) {
                // Eliminate first
                val vectorSet = vectors[vector]
                val eliminatedAsteroid = vectorSet?.popFirst()
                if (eliminatedAsteroid != null) {
                    eliminatedCount++
                    asteroidCount--
                    if (eliminatedCount == 200) {
                        return eliminatedAsteroid.coordinate
                    }
                }
            }
        }
        return null
    }
}