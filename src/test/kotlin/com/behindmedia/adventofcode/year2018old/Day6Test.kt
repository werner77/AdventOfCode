package com.behindmedia.adventofcode.year2018old
import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

class Day6Test {

    @Test
    fun largestAreaSimple() {
        val points = parseLines("/2018/day6-simple.txt") {
            Day6.Point.fromString(it)
        }

        val result = Day6().largestArea1(points)
        println("Result: ${result}")
    }

    @Test
    fun largestArea() {
        val points = parseLines("/2018/day6.txt") {
            Day6.Point.fromString(it)
        }

        val result = Day6().largestArea1(points)
        println("Result: ${result}")
    }

    @Test
    fun largestArea2Simple() {
        val points = parseLines("/2018/day6-simple.txt") {
            Day6.Point.fromString(it)
        }

        val result = Day6().largestArea2(points, 32)
        println("Result: ${result}")
    }

    @Test
    fun largestArea2() {
        val points = parseLines("/2018/day6.txt") {
            Day6.Point.fromString(it)
        }

        val result = Day6().largestArea2(points, 10000)
        println("Result: ${result}")
    }


}