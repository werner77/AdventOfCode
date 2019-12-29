package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

class Day10Test {

    @Test
    fun determineMessageSimple() {
        val day10 = Day10()

        val entries = parseLines("/2018/day10-simple.txt") {
            day10.parseLine(it)
        }

        day10.determineMessage(entries)
    }

    @Test
    fun determineMessage() {
        val day10 = Day10()

        val entries = parseLines("/2018/day10.txt") {
            day10.parseLine(it)
        }

        day10.determineMessage(entries)
    }
}