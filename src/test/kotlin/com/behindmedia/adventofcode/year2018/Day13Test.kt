package com.behindmedia.adventofcode.year2018
import org.junit.Test

import org.junit.Assert.*

class Day13Test {

    @Test
    fun parse() {
        val string = parse("/2018/day13-simple.txt") {
            Day13.Track.fromString(it)
        }

        print(string)
    }

    @Test
    fun processSimple() {
        val day13 = Day13()
        val string = parse("/2018/day13-simple.txt") { it }
        val coordinate = day13.process(string)

        assertEquals(Day13.Coordinate(7, 3), coordinate)
    }

    @Test
    fun process() {
        val day13 = Day13()
        val string = parse("/2018/day13.txt") { it }
        val coordinate = day13.process(string)

        assertEquals(Day13.Coordinate(116,10), coordinate)
    }

    @Test
    fun process2() {
        val day13 = Day13()
        val string = parse("/2018/day13.txt") { it }
        val coordinate = day13.process(string, false)

        println(coordinate)
    }
}