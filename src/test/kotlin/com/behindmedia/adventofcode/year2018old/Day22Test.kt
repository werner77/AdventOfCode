package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.Coordinate
import org.junit.Test

import org.junit.Assert.*

class Day22Test {

    @Test
    fun puzzle1() {
        val day22 = Day22()
        val result = day22.determineRiskLevel(7740, Coordinate(12,763))
        println(result)
    }

    @Test
    fun puzzle2() {
        val day22 = Day22()
        val result = day22.shortestPath(7740, Coordinate(12,763))
        println(result)
    }

    @Test
    fun determineRiskLevel() {
        val day22 = Day22()
        val result = day22.determineRiskLevel(510, Coordinate(10,10))
        assertEquals(114, result)
    }

    @Test
    fun shortestPath() {
        val day22 = Day22()
        val result = day22.shortestPath(510, Coordinate(10,10))
        assertEquals(45, result)
    }
}