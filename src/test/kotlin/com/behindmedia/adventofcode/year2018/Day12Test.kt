package com.behindmedia.adventofcode.year2018
import org.junit.Test

import org.junit.Assert.*

class Day12Test {

    @Test
    fun processSimpleSingleGeneration() {
        val expected1 = "#...#....#.....#..#..#..#"
        val expected2 = "##..##...##....#..#..#..##"
        val expected3 = "#.#...#..#.#....#..#..#...#"
        val day12 = Day12()

        val parseResult = day12.parseInput(read("/2018/day12-simple.txt"))

        val initialState = parseResult.first
        val patterns = parseResult.second

        var state = initialState

        assertEquals(initialState, state)

        state = day12.process(state, patterns, 1).first

        assertEquals(expected1, state)

        state = day12.process(state, patterns, 1).first

        assertEquals(expected2, state)

        state = day12.process(state, patterns, 1).first

        assertEquals(expected3, state)
    }

    @Test
    fun processSimple() {
        val day12 = Day12()
        val result = day12.process(read("/2018/day12-simple.txt"), 20)
        assertEquals(325, result)
    }

    @Test
    fun puzzle1() {
        val day12 = Day12()
        val result = day12.process(read("/2018/day12.txt"), 20)
        println(result)
    }

    @Test
    fun puzzle2() {
        val day12 = Day12()
        val result = day12.process(read("/2018/day12.txt"), 50000000000L)
        println(result)
    }
}