package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.read
import org.junit.Test

class Day25Test {

    @Test
    fun execute() {
        val initialInput = """
            take mouse
            north
            take ornament
            south
            east
            north
            take astronaut ice cream
            north
            south
            south
            west
            north
            west
            north
            take easter egg
            east
            take hypercube
            north
            east
            take prime number
            west
            south
            west
            north
            west
            north
            take wreath
            south
            east
            south
            south
            west
            take mug
            west
            east
            east
            east
            take ornament
            south
            take mouse
            east
            north
            north
            south
            south
            west
            north
            west
            west
            west
            drop mug
            drop astronaut ice cream
            drop ornament
            drop easter egg
            north
        """.trimIndent()

        val day25 = Day25()

        val result = day25.adventure(read("/day25.txt"), initialInput, false)

        println(result)
    }
}