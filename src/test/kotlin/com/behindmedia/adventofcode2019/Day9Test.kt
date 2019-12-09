package com.behindmedia.adventofcode2019

import org.junit.Assert.*
import org.junit.Test

class Day9Test {

    fun mapOfState(state: List<Long>): Map<Long, Long> {
        return state.foldIndexed(mutableMapOf()) { address, map, value ->
            map[address.toLong()] = value
            map
        }
    }

    @Test
    fun selfTest1() {

        val input = listOf(109L,1L,204L,-1L,1001L,100L,1L,100L,1008L,100L,16L,101L,1006L,101L,0L,99L)
        val day9 = Day9()
        val computer = day9.selfTest(input)

        val expectedState = mapOfState(input)
        val state = computer.currentState

        assertEquals(expectedState, state)
    }

    @Test
    fun selfTest2() {
        val day9 = Day9()
        val input = listOf(1102L,34915192L,34915192L,7L,4L,7L,99L,0L)
        val computer = day9.selfTest(input)
        val output = computer.lastOutput

        println(output)
        println(computer.currentState)

    }

    @Test
    fun puzzle1() {
        val input = parse("/day9.txt") {
            it.split(",").map { value -> value.toLong() }
        }

        val computer = Computer(input)
        val output = computer.process(listOf(1L))

        println(output)
    }

    @Test
    fun puzzle2() {
        val input = parse("/day9.txt") {
            it.split(",").map { value -> value.toLong() }
        }

        val computer = Computer(input)
        val output = computer.process(listOf(2L))

        println(output)
    }

}