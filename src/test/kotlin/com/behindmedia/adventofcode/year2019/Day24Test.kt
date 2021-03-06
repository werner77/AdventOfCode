package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Test
import kotlin.test.assertEquals

class Day24Test {

    @Test
    fun parseInput() {
        val day24 = Day24()
        val state  = day24.parseInput(read("/2019/day24.txt"))
        day24.print(state)
    }

    @Test
    fun puzzle1() {
        val day24 = Day24()
        val result = day24.findFirstRepeatedState(read("/2019/day24.txt"))
        println(result)
        assertEquals(23846449, result)
    }

    @Test
    fun puzzle2() {
        val day24 = Day24()
        val result = day24.numberOfBugs(read("/2019/day24.txt"))
        println(result)
        assertEquals(1934, result)
    }

    @Test
    fun numberOfBugsSample() {
        val input = """
            ....#
            #..#.
            #..##
            ..#..
            #....
        """.trimIndent()
        val day24 = Day24()
        val result = day24.numberOfBugs(input, 10)
        println(result)
        assertEquals(99, result)
    }
}