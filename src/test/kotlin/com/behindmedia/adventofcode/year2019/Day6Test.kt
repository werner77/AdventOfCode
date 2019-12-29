package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

import org.junit.Assert.*

class Day6Test {

    @Test
    fun numberOfTotalOrbits() {
        val input = parseLines("/2019/day6-sample.txt") { it }
        val day6 = Day6()
        assertEquals(42, day6.numberOfTotalOrbits(input))
    }

    @Test
    fun numberOfTransfers() {
        val input = parseLines("/2019/day6-sample2.txt") { it }
        val day6 = Day6()
        assertEquals(4, day6.numberOfTransfers(input, "YOU", "SAN"))
    }

    @Test
    fun puzzle1() {
        val input = parseLines("/2019/day6.txt") { it }
        val day6 = Day6()
        val result = day6.numberOfTotalOrbits(input)
        println(result)
        assertEquals(312697, result)
    }

    @Test
    fun puzzle2() {
        val input = parseLines("/2019/day6.txt") { it }
        val day6 = Day6()
        val result = day6.numberOfTransfers(input, "YOU", "SAN")
        println(result)
        assertEquals(466, result)
    }
}