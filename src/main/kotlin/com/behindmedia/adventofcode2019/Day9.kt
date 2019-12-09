package com.behindmedia.adventofcode2019

class Day9 {

    fun selfTest(initialState: List<Long>): Computer {
        val computer = Computer(initialState)
        computer.process(listOf())
        return computer
    }

}