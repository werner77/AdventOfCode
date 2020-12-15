package com.behindmedia.adventofcode.year2020

import org.junit.Test

import org.junit.Assert.*

class Day15Test {

    @Test
    fun part1() {
        println(Day15().part1("9,6,0,10,18,2,1"))
    }

    @Test
    fun part1_1() {
        println(Day15().part1("3,1,2"))
    }

    @Test
    fun part2() {
        println(Day15().part1("9,6,0,10,18,2,1", 30000000L))
    }
}