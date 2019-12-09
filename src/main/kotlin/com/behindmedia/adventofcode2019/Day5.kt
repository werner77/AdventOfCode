package com.behindmedia.adventofcode2019

class Day5 {

    fun execute(initialState: List<Int>, input: Int = 1): Int {
        val computer = Computer(initialState.toLongList())
        val output = computer.process(listOf(input).toLongList())
        assert(computer.status == Computer.Status.Finished)
        return output.toInt()
    }
}