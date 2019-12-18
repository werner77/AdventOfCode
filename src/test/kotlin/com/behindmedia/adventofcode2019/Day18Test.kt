package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day18Test {

    @Test
    fun puzzle1() {
        val day18 = Day18()
        val input = read("/day18.txt")
        val result = day18.getMinimumNumberOfMoves(input)
        println(result)
        assertEquals(6316, result)
    }

    @Test
    fun reachableCoordinates() {
        val day18 = Day18()

        val input = """
        ########################
        #f.D.E.e.C.b.A.@.a.B.c.#
        ######################.#
        #d.....................#
        ########################
        """.trimIndent()

        val result = day18.getMinimumNumberOfMoves(input)
        println(result)
        assertEquals(86, result)
    }
}