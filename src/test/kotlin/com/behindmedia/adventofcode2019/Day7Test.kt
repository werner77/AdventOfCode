package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day7Test {

    @Test
    fun puzzle1() {
        val opcodes = parse("/day7.txt") {
            it.split(",").map { value -> value.toInt() }
        }
        val day7 = Day7()
        val result = day7.optimize(opcodes)
        println("Result: $result")
    }

    @Test
    fun puzzle2() {
        val opcodes = parse("/day7.txt") {
            it.split(",").map { value -> value.toInt() }
        }
        val day7 = Day7()
        val result = day7.optimizeWithFeedback(opcodes)
        println("Result: $result")
    }

    @Test
    fun testOptimize() {
        val opcodes = listOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0)
        val day7 = Day7()
        val result = day7.optimize(opcodes)
        assertEquals(43210, result)
    }

    @Test
    fun testExecuteWithFeedback() {
        val opcodes = listOf(3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,
            27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5)
        val day7 = Day7()
        val result = day7.executeWithFeedback(listOf(9,8,7,6,5), opcodes)
        assertEquals(139629729, result)
    }
}