package com.behindmedia.adventofcode2019

class Day5 {

    fun execute(initialState: List<Int>, input: Int = 1): Int {
        val computer = Computer(initialState.toLongList())
        val result = computer.process(listOf(input).toLongList())
        assert(result.status == Computer.Status.Finished)
        return result.lastOutput.toInt()
    }
}