package com.behindmedia.adventofcode.year2018
import org.junit.Assert.*
import org.junit.Test

class Day18Test {

    @Test
    fun equals() {
        val area1 = Day18.Area.fromString(read("/2018/day18-simple.txt"))
        val area2 = Day18.Area.fromString(read("/2018/day18-simple.txt"))

        assertTrue(area1.state == area2.state)
        area2.process(1)
        assertFalse(area1.state == area2.state)
        assertTrue(area1.initialState == area2.initialState)
    }

    @Test
    fun processSimple() {
        val area = Day18.Area.fromString(read("/2018/day18-simple.txt"))
        val result = area.process(10)

        assertEquals(1147, result)
    }

    @Test
    fun process() {
        val area = Day18.Area.fromString(read("/2018/day18.txt"))
        val result = area.process(10)

        println(result)
    }

    @Test
    fun processOptimize() {
        val area = Day18.Area.fromString(read("/2018/day18.txt"))
        val result1 = area.process(500)
        area.reset()
        val result2 = area.process(500, true)
        assertEquals(result1, result2)
    }

    @Test
    fun processLong() {
        val area = Day18.Area.fromString(read("/2018/day18.txt"))
        val result = area.process(1_000_000_000, true)
        println(result)
    }
}