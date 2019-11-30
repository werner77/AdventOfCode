package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class CommonKtTest {

    @Test
    fun parseLines() {
        val list= parseLines("/common.txt") { it }
        val expected = listOf("Hello", "world")
        assertEquals(2, list.size)
        assertEquals(expected, list)
    }
}