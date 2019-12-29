package com.behindmedia.adventofcode.year2018

import org.junit.Test

import org.junit.Assert.*

class Day1Test {

    @Test
    fun calculateFrequency() {
        val numbers = parseList("/2018/day1.txt") {
            it.toInt()
        }
        val result = Day1().calculateFrequency(0, numbers)
        println(result)
    }

    @Test
    fun findFirstDuplicateFrequency() {
        val numbers = parseList("/2018/day1.txt") {
            it.toInt()
        }
        val result = Day1().findFirstDuplicateFrequency(0, numbers)
        println(result)
    }
}