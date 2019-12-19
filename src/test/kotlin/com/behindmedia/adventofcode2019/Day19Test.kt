package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day19Test {

    @Test
    fun puzzle1() {
        val encoded = read("/day19.txt")
        val day19 = Day19()

        val result = day19.numberOfBeamPoints(encoded, 50)
        println(result)
        assertEquals(166, result)
    }

    @Test
    fun puzzle2() {
        val encoded = read("/day19.txt")

        val day19 = Day19()
        val result = day19.findSquareLocation(encoded, 100)
        val decodedResult = result.x * 10_000 + result.y

        println(decodedResult)
        assertEquals(3790981, decodedResult)
    }
}