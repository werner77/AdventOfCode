package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Test

class Day21Test {

    @Test
    fun puzzle1() {
        val day21 = Day21()

        val program = read("/day21.txt")
        val result = day21.walk(program)

        println(result)
    }

    @Test
    fun puzzle2() {
        val day21 = Day21()

        val program = read("/day21.txt")
        val result = day21.run(program)

        println(result)
    }
}