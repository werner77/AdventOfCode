package com.behindmedia.adventofcode.year2018
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class Day16Test {

    @Test
    fun process() {
        val input = read("/2018/day16a.txt")
        val samples = Day16.Sample.fromString(input)

        val result = Day16().process(samples)
        println(result)
    }

    @Test
    fun execute() {
        val input = read("/2018/day16a.txt")
        val samples = Day16.Sample.fromString(input)

        val day16 = Day16()

        day16.process(samples)

        val instructions = parseList("/2018/day16b.txt") {
            it.split("[", "]", ",", " ").filter { !it.isEmpty() }.map { it.toInt() }.toIntArray()
        }

        val output = day16.execute(instructions)

        println(Arrays.toString(output))

    }
}