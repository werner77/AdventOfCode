package com.behindmedia.adventofcode2019

import org.junit.Test
import kotlin.test.assertEquals

class Day11Test {

    @Test
    fun puzzle1() {
        val day11 = Day11()
        val encodedState = read("/day11.txt")
        val map = day11.paintLicensePlate(encodedState)
        println(map.size)
        assertEquals(2415, map.size)
    }

    private fun printMap(map: Map<Coordinate, Day11.Color>) {
        val coordinates = map.keys
        val range = coordinates.range()

        for (coordinate in range) {
            val value = map[coordinate]
            if (value == Day11.Color.White) {
                print("#")
            } else {
                print(" ")
            }
            if (coordinate.x == range.endInclusive.x) {
                println()
            }
        }
    }

    @Test
    fun puzzle2() {
        val day11 = Day11()
        val encodedState = read("/day11.txt")
        val map = day11.paintLicensePlate(encodedState, Day11.Color.White)
        printMap(map)
    }

}