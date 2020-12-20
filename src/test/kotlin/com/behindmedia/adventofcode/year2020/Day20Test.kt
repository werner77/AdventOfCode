package com.behindmedia.adventofcode.year2020

import org.junit.Test

import org.junit.Assert.*

class Day20Test {

    @Test
    fun part1() {
        println(Day20().part1("/2020/day20.txt"))
    }

    @Test
    fun part1_1() {
        println(Day20().part1("/2020/day20-1.txt"))
    }

    @Test
    fun part2() {
        println(Day20().part2("/2020/day20.txt", "/2020/seamonster.txt"))
    }

    @Test
    fun part2_1() {
        println(Day20().part2("/2020/day20-1.txt", "/2020/seamonster.txt"))
    }
}