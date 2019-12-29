package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.parse
import org.junit.Test

import org.junit.Assert.*

class Day2Test {

    @Test
    fun processOpcodes() {
        val opcodes = listOf<Int>(1,9,10,3,2,3,11,0,99,30,40,50)
        val day2 = Day2()
        assertEquals(listOf(3500,9,10,70, 2, 3, 11, 0, 99, 30,40,50), day2.execute(opcodes))
        assertEquals(listOf(2,0,0,0,99), day2.execute(listOf(1,0,0,0,99)))
        assertEquals(listOf(2,3,0,6,99), day2.execute(listOf(2,3,0,3,99)))
        assertEquals(listOf(2,4,4,5,99,9801), day2.execute(listOf(2,4,4,5,99,0)))
        assertEquals(listOf(30,1,1,4,2,5,6,0,99), day2.execute(listOf(1,1,1,4,99,5,6,0,99)))
    }

    @Test
    fun puzzle1() {
        val opcodes = parse("/2019/day2.txt") {
            it.split(",").map { value -> value.toInt() }
        }
        val day2 = Day2()
        val result = day2.execute(opcodes, Pair(12, 2))[0]
        println(result)
        assertEquals(5866714, result)
    }

    @Test
    fun puzzle2() {
        val opcodes = parse("/2019/day2.txt") {
            it.split(",").map { value -> value.toInt() }
        }
        val day2 = Day2()
        val result = day2.findNounAndVerb(opcodes)
        println(result)
        assertEquals(5208, result)
    }
}