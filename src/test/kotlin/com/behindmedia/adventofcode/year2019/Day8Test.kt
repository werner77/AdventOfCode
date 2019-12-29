package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.parse
import org.junit.Test
import kotlin.test.assertEquals

class Day8Test {

    @Test
    fun execute() {
        val input = getInput()
        val day8 = Day8()
        val result = day8.checksum(25,6, input)
        println(result)
        assertEquals(1703, result)
    }

    @Test
    fun decodeMessage() {
        val input = getInput()
        val day8 = Day8()
        val result = day8.decodeMessage(25,6, input)
        println(result)
    }

    private fun getInput(): List<Int> {
        return parse("/day8.txt") {
            it.fold(mutableListOf()) { list, c ->
                val number: Int = c - '0'
                assert(number in 0..9)
                list.add(number)
                list
            }
        }
    }
}