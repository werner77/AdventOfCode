package com.behindmedia.adventofcode.year2018old
import com.behindmedia.adventofcode.common.read
import org.junit.Assert.assertEquals
import org.junit.Test

class Day17Test {

    @Test
    fun flowSimple() {
        val input = read("/2018/day17-simple.txt")
        val ground = Day17.Ground.fromString(input)

        val result = ground.flow()

        println(ground)

        assertEquals(57, result.first)
    }

    @Test
    fun flow() {
        val input = read("/2018/day17.txt")
        val ground = Day17.Ground.fromString(input)

        val result = ground.flow()

        println(ground)

        println(result)
    }
}