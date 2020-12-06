package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.read
import org.junit.Test

import org.junit.Assert.*

class Day2Test {

    @Test
    fun part1() {
        val lines = parseLines("/2020/day2.txt") { it }
        val day2 = Day2()
        println(day2.part1(lines))
    }

    @Test
    fun part2() {
        val lines = parseLines("/2020/day2.txt") { it }
        val day2 = Day2()
        println(day2.part2(lines))
    }
}