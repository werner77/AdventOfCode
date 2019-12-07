package com.behindmedia.adventofcode2019

class Day2 {

    fun execute(initialState: List<Int>, nounAndVerb: Pair<Int, Int>? = null): List<Int> {
        val modifiedState = initialState.toMutableList()

        if (nounAndVerb != null) {
            modifiedState[1] = nounAndVerb.first
            modifiedState[2] = nounAndVerb.second
        }

        val computer = Computer(modifiedState)
        computer.process(listOf())
        assert(computer.status == Computer.Status.Exit)
        return computer.currentState
    }

    fun findNounAndVerb(opcodes: List<Int>, expectedResult: Int = 19690720): Int? {
        return permutate(2, 0..99) {
            val noun = it[0]
            val verb = it[1]
            val result = execute(opcodes, Pair(noun, verb))
            if (result[0] == expectedResult) 100 * noun + verb else null
        }
    }

}