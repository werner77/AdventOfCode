package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.firstDigits
import com.behindmedia.adventofcode.common.parse
import com.behindmedia.adventofcode.common.repeated
import org.junit.Test

import kotlin.test.assertEquals

class Day16Test {

    @Test
    fun puzzle1() {
        val day16 = Day16()
        val input = parse("/day16.txt") {
            it.map { c -> c.toString().toInt() }
        }
        val result = day16.fastFFT(input, 100, 0)
        println(result)
        assertEquals(59281788, result)
    }

    @Test
    fun sample1() {
        val day16 = Day16()
        val input = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        val result = day16.fastFFT(input, 4, 0)
        println(result)
        assertEquals(1029498, result)
    }

    @Test
    fun puzzle2() {
        val day16 = Day16()
        val input = parse("/day16.txt") {
            it.map { c -> c.toString().toInt() }
        }.repeated(10_000)

        val messageOffset = input.firstDigits(7)
        val output = day16.fastFFT(input, 100, messageOffset)
        println(output)
        assertEquals(96062868, output)
    }
}