package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.numberOfDigits
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.retainingAll
import com.behindmedia.adventofcode.common.toMap
import org.junit.Assert.*
import org.junit.Test

class Day9Test {

    @Test
    fun selfTest1() {

        val initialState = listOf<Long>(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)
        val computer = Computer(initialState)
        val expectedState = initialState.toMap()

        computer.process()

        val state = computer.currentState.toMutableMap().retainingAll { entry ->
            entry.key in initialState.indices
        }
        assertEquals(expectedState, state)
    }

    @Test
    fun selfTest2() {
        val initialState = listOf<Long>(1102,34915192,34915192,7,4,7,99,0)
        val computer = Computer(initialState)
        val result = computer.process()
        assertEquals(16, result.lastOutput.numberOfDigits)
    }

    @Test
    fun selfTest3() {
        val initialState = listOf<Long>(104,1125899906842624,99)
        val computer = Computer(initialState)
        val result = computer.process()
        assertEquals(initialState[1], result.lastOutput)
    }

    @Test
    fun puzzle1() {
        val encodedState = read("/day9.txt")
        val computer = Computer(encodedState)
        val result = computer.process(listOf(1L))
        println(result.lastOutput)
        assertEquals(3598076521, result.lastOutput)
    }

    @Test
    fun puzzle2() {
        val encodedState = read("/day9.txt")
        val computer = Computer(encodedState)
        val result = computer.process(listOf(2L))
        println(result.lastOutput)
        assertEquals(90722, result.lastOutput)
    }

}