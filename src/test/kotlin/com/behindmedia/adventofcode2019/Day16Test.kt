package com.behindmedia.adventofcode2019

import org.junit.Test

import java.lang.Math.abs
import kotlin.test.assertEquals

class Day16Test {

    @Test
    fun puzzle1() {
        val day16 = Day16()
        val input = parse("/day16.txt") {
            it.map { c -> c.toString().toInt() }
        }

        val result = day16.fft(input, 100)
        println(result)
        assertEquals(59281788, result)
    }

    @Test
    fun puzzle2() {
        val day16 = Day16()
        val input = parse("/day16.txt") {
            it.map { c -> c.toString().toInt() }
        }.repeated(10_000)

        val output = day16.fft2(input, 100)
        println(output)
        assertEquals(96062868, output)
    }
}