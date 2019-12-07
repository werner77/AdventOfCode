package com.behindmedia.adventofcode2019

class Day5 {

    fun execute(initialState: List<Int>, input: Int = 1): Int {
        val computer = Computer(initialState)
        val output = computer.process(listOf(input))
        assert(computer.status == Computer.Status.Done)
        return output
    }
}