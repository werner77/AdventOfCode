package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

class Day1Test {

    @Test
    fun part1() {
        val input: List<Int> = parseLines("/2020/day1.txt") {
            it.toInt()
        }
        val day1 = Day1()
        val result = day1.part1(input, 2020)
        println(result)
    }

    @Test
    fun part2() {
        val input: List<Int> = parseLines("/2020/day1.txt") {
            it.toInt()
        }
        val day1 = Day1()
        val result = day1.part2(input, 2020)
        println(result)
    }
}