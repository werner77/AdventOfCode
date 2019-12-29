package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.parse
import org.junit.Assert.assertEquals
import org.junit.Test

class Day5Test {

    @Test
    fun puzzle1() {

        val initialState = parse("/2019/day5.txt") {
            it.split(",").map { value -> value.toInt() }
        }
        val day5 = Day5()
        val result = day5.execute(initialState)
        println(result)
        assertEquals(7157989, result)
    }

    @Test
    fun puzzle2() {

        val initialState = parse("/2019/day5.txt") {
            it.split(",").map { value -> value.toInt() }
        }
        val day5 = Day5()
        val result = day5.execute(initialState, 5)
        println(result)
        assertEquals(7873292, result)
    }

    @Test
    fun execute() {
        val day5 = Day5()
        assertEquals(1, day5.execute(listOf(3,9,8,9,10,9,4,9,99,-1,8), 8))
        assertEquals(0, day5.execute(listOf(3,9,8,9,10,9,4,9,99,-1,8), 7))
        assertEquals(0, day5.execute(listOf(3,9,7,9,10,9,4,9,99,-1,8), 8))
        assertEquals(1, day5.execute(listOf(3,9,7,9,10,9,4,9,99,-1,8), 7))
        assertEquals(0, day5.execute(listOf(3,9,7,9,10,9,4,9,99,-1,8), 9))
        assertEquals(1, day5.execute(listOf(3,3,1108,-1,8,3,4,3,99), 8))
        assertEquals(0, day5.execute(listOf(3,3,1108,-1,8,3,4,3,99), 7))
        assertEquals(0, day5.execute(listOf(3,3,1108,-1,8,3,4,3,99), 9))
        assertEquals(0, day5.execute(listOf(3,3,1107,-1,8,3,4,3,99), 8))
        assertEquals(1, day5.execute(listOf(3,3,1107,-1,8,3,4,3,99), 7))
        assertEquals(0, day5.execute(listOf(3,3,1107,-1,8,3,4,3,99), 9))
        assertEquals(0, day5.execute(listOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9), 0))
        assertEquals(1, day5.execute(listOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9), 1))
        assertEquals(0, day5.execute(listOf(3,3,1105,-1,9,1101,0,0,12,4,12,99,1), 0))
        assertEquals(1, day5.execute(listOf(3,3,1105,-1,9,1101,0,0,12,4,12,99,1), 100))
        assertEquals(0, day5.execute(listOf(3,3,1107,-1,8,3,4,3,99), 9))
        assertEquals(1, day5.execute(listOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9), 1))
    }
}