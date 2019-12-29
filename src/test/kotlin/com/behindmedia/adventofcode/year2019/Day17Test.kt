package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Test
import kotlin.test.assertEquals

class Day17Test {

    @Test
    fun puzzle1() {
        val day17 = Day17()
        val program = Computer.parseEncodedState(read("/day17.txt"))
        val result = day17.getNumberOfCrossings(program)
        println(result)
        assertEquals(7328, result)
    }

    @Test
    fun puzzle2() {
        val day17 = Day17()

        val encoded = read("/day17.txt")
        val state = Computer.parseEncodedState(encoded).toMutableList()

        val result = day17.getNumberOfDustParticles(state)

        println(result)
    }
}