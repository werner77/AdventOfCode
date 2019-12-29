package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.read
import org.junit.Assert.assertEquals
import org.junit.Test

class Day15Test {

    @Test
    fun move() {
        val day15 = Day15()
        val input = read("/2018/day15-2a.txt")
        val cave = day15.parse(input)
        println(cave)
        assertEquals(input, cave.toString())

        cave.performTurn()
        println(cave)
        assertEquals(read("/2018/day15-2b.txt"), cave.toString())

        cave.performTurn()
        println(cave)
        assertEquals(read("/2018/day15-2c.txt"), cave.toString())

        cave.performTurn()
        println(cave)
        assertEquals(read("/2018/day15-2d.txt"), cave.toString())

    }

    @Test
    fun simpleCombat1() {
        val day15 = Day15()
        val input = read("/2018/day15-simple1.txt")
        val cave = day15.parse(input)

        val result = cave.processCombat()

        println(cave)

        assertEquals(27730, result)
    }

    @Test
    fun simpleCombat2() {
        val day15 = Day15()
        val input = read("/2018/day15-simple2.txt")
        val cave = day15.parse(input)

        val result = cave.processCombat()

        println(cave)

        assertEquals(36334, result)
    }

    @Test
    fun simpleCombat3() {
        val day15 = Day15()
        val input = read("/2018/day15-simple3.txt")
        val cave = day15.parse(input)

        val result = cave.processCombat()

        println(cave)

        assertEquals(39514, result)
    }

    @Test
    fun simpleCombat4() {
        val day15 = Day15()
        val input = read("/2018/day15-simple4.txt")
        val cave = day15.parse(input)

        val result = cave.processCombat()

        println(cave)

        assertEquals(18740, result)
    }

    @Test
    fun processCombat() {
        val day15 = Day15()
        val input = read("/2018/day15.txt")
        val cave = day15.parse(input)

        val result = cave.processCombat()

        println(cave)

        println(result)
    }
}