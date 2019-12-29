package com.behindmedia.adventofcode.year2018

import org.junit.Test

class Day4Test {

    @Test
    fun testStrategy1() {
        val entries = parseList("/2018/day4.txt") {
            Day4.Entry.fromString(it)
        }
        val result =  Day4().strategy1(entries)
        println("Result: ${result}")
    }

    @Test
    fun testStrategy2() {
        val entries = parseList("/2018/day4.txt") {
            Day4.Entry.fromString(it)
        }
        val result =  Day4().strategy2(entries)
        println("Result: ${result}")
    }
}