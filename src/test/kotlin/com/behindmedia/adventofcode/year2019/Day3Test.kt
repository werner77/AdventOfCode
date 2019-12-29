package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Assert.assertEquals
import org.junit.Test

class Day3Test {

    @Test
    fun findSmallestDistance() {
        val day3 = Day3()
        var encoding1 = "R8,U5,L5,D3"
        var encoding2 = "U7,R6,D4,L4"
        assertEquals(6, day3.smallestDistance(encoding1, encoding2))

        encoding1 = "R75,D30,R83,U83,L12,D49,R71,U7,L72"
        encoding2 = "U62,R66,U55,R34,D71,R55,D58,R83"

        assertEquals(159, day3.smallestDistance(encoding1, encoding2))

        encoding1 = "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
        encoding2 = "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
        assertEquals(135, day3.smallestDistance(encoding1, encoding2))
    }

    @Test
    fun puzzle1() {
        val lines = parseLines("/2019/day3.txt") {
            it
        }
        val day3 = Day3()
        val result = day3.smallestDistance(lines[0], lines[1])

        println(result)
        assertEquals(806, result)
    }

    @Test
    fun smallestSteps() {
        val day3 = Day3()

        var encoding1 = "R8,U5,L5,D3"
        var encoding2 = "U7,R6,D4,L4"
        assertEquals(30, day3.smallestSteps(encoding1, encoding2))

        encoding1 = "R75,D30,R83,U83,L12,D49,R71,U7,L72"
        encoding2 = "U62,R66,U55,R34,D71,R55,D58,R83"

        assertEquals(610, day3.smallestSteps(encoding1, encoding2))

        encoding1 = "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"
        encoding2 = "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"
        assertEquals(410, day3.smallestSteps(encoding1, encoding2))
    }

    @Test
    fun puzzle2() {
        val lines = parseLines("/2019/day3.txt") {
            it
        }
        val day3 = Day3()
        val result = day3.smallestSteps(lines[0], lines[1])

        println(result)
        assertEquals(66076, result)
    }
}