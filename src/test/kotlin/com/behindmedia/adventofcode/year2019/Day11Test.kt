package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.printMap
import com.behindmedia.adventofcode.common.range
import com.behindmedia.adventofcode.common.read
import org.junit.Test
import kotlin.test.assertEquals

class Day11Test {

    @Test
    fun puzzle1() {
        val day11 = Day11()
        val encodedState = read("/2019/day11.txt")
        val map = day11.paintLicensePlate(encodedState)
        println(map.size)
        assertEquals(2415, map.size)
    }

    private fun printMap(map: Map<Coordinate, Day11.Color>) {
        map.mapValues { if (it.value == Day11.Color.White) "#" else " " }.printMap(" ")
    }

    @Test
    fun puzzle2() {
        val day11 = Day11()
        val encodedState = read("/2019/day11.txt")
        val map = day11.paintLicensePlate(encodedState, Day11.Color.White)
        printMap(map)
    }

}