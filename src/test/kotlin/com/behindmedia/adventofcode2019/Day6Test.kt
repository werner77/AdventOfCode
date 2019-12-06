package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day6Test {

    @Test
    fun numberOfTotalOrbits() {
        val input = parseLines("/day6-sample.txt") { it }
        val day6 = Day6()
        assertEquals(42, day6.numberOfTotalOrbits(input))
    }

    @Test
    fun numberOfTransfers() {
        val input = parseLines("/day6-sample2.txt") { it }
        val day6 = Day6()
        assertEquals(4, day6.numberOfTransfers(input, "YOU", "SAN"))
    }

    @Test
    fun puzzle1() {
        val input = parseLines("/day6.txt") { it }
        val day6 = Day6()
        println(day6.numberOfTotalOrbits(input))
    }

    @Test
    fun puzzle2() {
        val input = parseLines("/day6.txt") { it }
        val day6 = Day6()
        println(day6.numberOfTransfers(input, "YOU", "SAN"))
    }
}