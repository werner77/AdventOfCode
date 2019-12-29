package com.behindmedia.adventofcode.year2018

import org.junit.Test

import org.junit.Assert.*

class Day5Test {

    @Test
    fun react() {
        val polymer = parse("/2018/day5.txt") { it }

        val result = Day5().react(polymer)

        println("Result: ${result.length}")
    }

    @Test
    fun reactSimple() {
        val polymer = "dabAcCaCBAcCcaDA"

        val result = Day5().react(polymer)

        println("Result: ${result.length}")
    }

    @Test
    fun reactOptimal() {
        val polymer = parse("/2018/day5.txt") { it }

        val result = Day5().reactOptimal(polymer)

        println("Result: ${result.length}")
    }

    @Test
    fun reactOptimalSimple() {
        val polymer = "dabAcCaCBAcCcaDA"
        val result = Day5().reactOptimal(polymer)

        println("Result: ${result.length}")
    }
}