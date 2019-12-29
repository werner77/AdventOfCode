package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Assert.assertEquals
import org.junit.Test

class CommonKtTest {

    @Test
    fun parseLines() {
        val list= parseLines("/2019/common.txt") { it }
        val expected = listOf("Hello", "world")
        assertEquals(2, list.size)
        assertEquals(expected, list)
    }
}