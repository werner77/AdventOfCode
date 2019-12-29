package com.behindmedia.adventofcode.year2018
import org.junit.Test

import org.junit.Assert.*

class Day10Test {

    @Test
    fun determineMessageSimple() {
        val day10 = Day10()

        val entries = parseList("/2018/day10-simple.txt") {
            day10.parseLine(it)
        }

        day10.determineMessage(entries)
    }

    @Test
    fun determineMessage() {
        val day10 = Day10()

        val entries = parseList("/2018/day10.txt") {
            day10.parseLine(it)
        }

        day10.determineMessage(entries)
    }
}