package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.read
import org.junit.Test

import org.junit.Assert.*

class Day18Test {

    @Test
    fun part1() {
        val input = read("/2020/day18.txt")
        println(Day18().part1(input))
    }

    @Test
    fun part1_1() {
        println(Day18().part1("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"))
    }

    @Test
    fun part2() {
        val input = read("/2020/day18.txt")
        println(Day18().part2(input))
    }

    @Test
    fun part2_1() {
        println(Day18().part2("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"))
    }

    @Test
    fun part2_2() {
        println(Day18().part2("6 + 9 * 8 + 6"))
    }
}