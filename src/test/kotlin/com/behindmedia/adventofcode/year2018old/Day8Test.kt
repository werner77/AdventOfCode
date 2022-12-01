package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.parse
import org.junit.Assert.assertEquals
import org.junit.Test

class Day8Test {

    @Test
    fun processSimple() {
        val entries = parse("/2018/day8-simple.txt") {
            it.split(" ").map { it.toInt() }
        }

        val sum = Day8().process(entries)
        assertEquals(138, sum)
    }

    @Test
    fun process() {
        val entries = parse("/2018/day8.txt") {
            it.split(" ").map { it.toInt() }
        }

        val sum = Day8().process(entries)

        println(sum)
    }

    @Test
    fun process2Simple() {
        val entries = parse("/2018/day8-simple.txt") {
            it.split(" ").map { it.toInt() }
        }

        val sum = Day8().process2(entries)
        assertEquals(66, sum)
    }

    @Test
    fun process2() {
        val entries = parse("/2018/day8.txt") {
            it.split(" ").map { it.toInt() }
        }

        val sum = Day8().process2(entries)
        println(sum)
    }
}