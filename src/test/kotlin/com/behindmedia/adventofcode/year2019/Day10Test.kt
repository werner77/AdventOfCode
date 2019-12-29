package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.isAlmostEqual
import com.behindmedia.adventofcode.common.read
import org.junit.Test
import kotlin.math.PI
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day10Test {

    @Test
    fun puzzle1() {
        val day10 = Day10()
        val asteroids = day10.decodeInput(read("/2019/day10.txt"))
        val result = day10.findBestLocation(asteroids)
        println(result)
        assertEquals(Pair(Coordinate(28,29), 340), result)
    }

    @Test
    fun puzzle2() {
        val day10 = Day10()
        val asteroids = day10.decodeInput(read("/2019/day10.txt"))
        val bestLocation = day10.findBestLocation(asteroids)
        val result = day10.destroyAsteroids(asteroids, bestLocation.first)!!
        println(result.x * 100 + result.y)
        assertEquals(Coordinate(26, 28), result)
    }

    @Test
    fun angle() {
        val zero = Coordinate(0, -1)
        assertAlmostEqual(Coordinate(0, -1).angle(zero), 0.0)
        assertAlmostEqual(Coordinate(1, -1).angle(zero),PI / 4.0)
        assertAlmostEqual(Coordinate(1, 0).angle(zero),PI / 2.0)
        assertAlmostEqual(Coordinate(1, 1).angle(zero),PI * 3.0 / 4.0)
        assertAlmostEqual(Coordinate(0, 1).angle(zero), PI)
        assertAlmostEqual(Coordinate(-1, 1).angle(zero),PI * 5.0 / 4.0)
        assertAlmostEqual(Coordinate(-1, 0).angle(zero),PI * 3.0 / 2.0)
        assertAlmostEqual(Coordinate(-1, -1).angle(zero),PI * 7.0 / 4.0)
    }

    private fun assertAlmostEqual(a: Double, b: Double) {
        assertTrue(a.isAlmostEqual(b), "Expected $a to almost equal $b")
    }
}