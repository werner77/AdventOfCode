package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Test

import org.junit.Assert.*

class Day15Test {

    @Test
    fun puzzle1() {
        val input = read("/2019/day15.txt")
        val day15 = Day15()
        val result = day15.fewestNumberOfMoves(input)
        println(result)
        assertEquals(248, result)
    }

    @Test
    fun puzzle2() {
        val input = read("/2019/day15.txt")
        val day15 = Day15()
        val result = day15.spreadOxygen(input)
        println(result)
        assertEquals(382, result)
    }
}