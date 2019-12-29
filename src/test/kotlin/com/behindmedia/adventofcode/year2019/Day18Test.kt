package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Test

import org.junit.Assert.*

class Day18Test {

    @Test
    fun puzzle1() {
        val day18 = Day18()
        val input = read("/day18.txt")
        val result = day18.getMinimumNumberOfMovesToCollectAllKeys(input)
        println(result)
        assertEquals(6316, result)
    }

    @Test
    fun puzzle2() {
        val day18 = Day18()
        val input = read("/day18-2.txt")
        val result = day18.getMinimumNumberOfMovesToCollectAllKeys(input)
        println(result)
        assertEquals(1648, result)
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

        val result = day18.getMinimumNumberOfMovesToCollectAllKeys(input)
        println(result)
        assertEquals(86, result)
    }

    @Test
    fun reachableCoordinates2() {
        val input = """
        #######    
        #a.#Cd#
        ##@#@##
        #######
        ##@#@##
        #cB#Ab#
        #######
        """.trimIndent()

        val day18 = Day18()
        val result = day18.getMinimumNumberOfMovesToCollectAllKeys(input)
        println(result)
        assertEquals(8, result)
    }
}