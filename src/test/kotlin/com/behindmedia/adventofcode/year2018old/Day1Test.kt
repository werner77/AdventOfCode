package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

class Day1Test {

    @Test
    fun calculateFrequency() {
        val numbers = parseLines("/2018/day1.txt") {
            it.toInt()
        }
        val result = Day1().calculateFrequency(0, numbers)
        println(result)
    }

    @Test
    fun findFirstDuplicateFrequency() {
        val numbers = parseLines("/2018/day1.txt") {
            it.toInt()
        }
        val result = Day1().findFirstDuplicateFrequency(0, numbers)
        println(result)
    }
}