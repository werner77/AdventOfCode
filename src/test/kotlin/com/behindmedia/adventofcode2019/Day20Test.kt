package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day20Test {

    @Test
    fun puzzle1() {
        val input = read("/day20.txt")
        val day20 = Day20()

        val result = day20.findMinimumPath(input, recursive = false)
        println(result)
        assertEquals(448, result)
    }

    @Test
    fun puzzle2() {
        val input = read("/day20.txt")
        val day20 = Day20()

        val result = day20.findMinimumPath(input, recursive = true)
        println(result)
        assertEquals(5678, result)
    }
}