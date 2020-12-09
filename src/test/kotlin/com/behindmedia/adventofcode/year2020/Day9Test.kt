package com.behindmedia.adventofcode.year2020

import org.junit.Test

import org.junit.Assert.*

class Day9Test {

    @Test
    fun part1() {
        val day9 = Day9()
        println(day9.part1("/2020/day9.txt"))
    }

    @Test
    fun part1_1() {
        val day9 = Day9()
        println(day9.part1("/2020/day9-1.txt", 5))
    }

    @Test
    fun part2() {
        val day9 = Day9()
        println(day9.part2("/2020/day9.txt"))
    }

    @Test
    fun part2_1() {
        val day9 = Day9()
        println(day9.part2("/2020/day9-1.txt", 5))
    }
}