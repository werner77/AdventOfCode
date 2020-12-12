package com.behindmedia.adventofcode.year2020

import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class Day12Test {

    private lateinit var day12: Day12

    @Before
    fun setUp() {
        day12 = Day12()
    }

    @Test
    fun part1() {
        println(day12.part1("/2020/day12.txt"))
    }

    @Test
    fun part2() {
        println(day12.part2("/2020/day12.txt"))
    }

    @Test
    fun part2_1() {
        println(day12.part2("/2020/day12-1.txt"))
    }
}