package com.behindmedia.adventofcode.year2020

import org.junit.Test

import org.junit.Assert.*

class Day5Test {

    @Test
    fun part1() {
        val day5 = Day5()
        println(day5.part1("/2020/day5.txt"))
    }

    @Test
    fun part2() {
        val day5 = Day5()
        println(day5.part2("/2020/day5.txt"))
    }

    @Test
    fun parseCoordinate() {
        val day5 = Day5()
        println(day5.parseCoordinate("FBFBBFFRLR"))
    }
}