package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

import org.junit.Assert.*

class Day1Test {

    @Test
    fun getFuelExamples() {
        val day1 = Day1()
        assertEquals(2, day1.getFuel(12))
        assertEquals(2, day1.getFuel(14))
        assertEquals(654, day1.getFuel(1969))
        assertEquals(33583, day1.getFuel(100756))
    }

    @Test
    fun getTotalFuel() {
        val masses = listOf(12, 14, 1969, 100756)
        val expected = 2 + 2 + 654 + 33583

        val ret = Day1().getTotalFuel(masses)

        assertEquals(expected, ret)
    }

    @Test
    fun puzzle1() {
        val day1 = Day1()

        val masses = parseLines("/2019/day1.txt") {
            it.toInt()
        }
        val result = day1.getTotalFuel(masses)
        println(result)
        assertEquals(3367126, result)
    }

    @Test
    fun cumulativeFuel() {
        val day1 = Day1()

        assertEquals(2, day1.cumulativeFuel(14))
        assertEquals(966, day1.cumulativeFuel(1969))
        assertEquals(50346, day1.cumulativeFuel(100756))
    }

    @Test
    fun puzzle2() {
        val day1 = Day1()

        val masses = parseLines("/2019/day1.txt") {
            it.toInt()
        }
        val result = day1.cumulativeTotalFuel(masses)
        println(result)
        assertEquals(5047796, result)
    }
}