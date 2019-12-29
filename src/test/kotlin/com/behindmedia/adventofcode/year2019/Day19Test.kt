package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Assert.assertEquals
import org.junit.Test

class Day19Test {

    @Test
    fun puzzle1() {
        val encoded = read("/2019/day19.txt")
        val day19 = Day19()

        val result = day19.numberOfBeamPoints(encoded, 50)
        println(result)
        assertEquals(166, result)
    }

    @Test
    fun puzzle2() {
        val encoded = read("/2019/day19.txt")

        val day19 = Day19()
        val result = day19.findSquareLocation(encoded, 100)
        val decodedResult = result.x * 10_000 + result.y

        println(decodedResult)
        assertEquals(3790981, decodedResult)
    }
}