package com.behindmedia.adventofcode.year2020

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class Day11Test {

    private lateinit var day11: Day11

    @Before
    fun setup() {
        day11 = Day11()
    }

    @Test
    fun part1() {
        println(day11.part1("/2020/day11.txt"))
    }

    @Test
    fun part2() {
        println(day11.part2("/2020/day11.txt"))
    }
}