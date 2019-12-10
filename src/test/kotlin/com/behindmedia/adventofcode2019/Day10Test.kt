package com.behindmedia.adventofcode2019

import org.junit.Test
import java.util.*
import kotlin.Comparator
import kotlin.IllegalStateException
import kotlin.math.PI
import kotlin.math.max
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day10Test {

    @Test
    fun puzzle1() {
        val day10 = Day10()
        val asteroids = day10.decodeInput(read("/day10.txt"))
        val result = day10.findBestLocation(asteroids)
        println(result)
        assertEquals(Pair(Coordinate(28,29), 340), result)
    }

    @Test
    fun puzzle2() {
        val day10 = Day10()
        val asteroids = day10.decodeInput(read("/day10.txt"))
        val bestLocation = day10.findBestLocation(asteroids)
        val result = day10.destroyAsteroids(asteroids, bestLocation.first)!!
        println(result.x * 100 + result.y)
        assertEquals(Coordinate(26, 28), result)
    }

    @Test
    fun angle() {
        val zero = Coordinate(0, -1)
        assertTrue(Coordinate(0, -1).angle(zero).isAlmostEqual(0.0))
        assertTrue(Coordinate(1, -1).angle(zero).isAlmostEqual(PI / 4.0))
        assertTrue(Coordinate(1, 1).angle(zero).isAlmostEqual(PI * 3.0 / 4.0))
        assertTrue(Coordinate(0, 1).angle(zero).isAlmostEqual(PI))
        assertTrue(Coordinate(-1, 1).angle(zero).isAlmostEqual(PI * 5.0 / 4.0))
        assertTrue(Coordinate(-1, -1).angle(zero).isAlmostEqual(PI * 7.0 / 4.0))
    }
}