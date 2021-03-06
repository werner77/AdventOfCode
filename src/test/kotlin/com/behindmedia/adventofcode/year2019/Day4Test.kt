package com.behindmedia.adventofcode.year2019

import org.junit.Assert.*
import org.junit.Test

class Day4Test {

    @Test
    fun numberOfValidPasswords() {
        val day4 = Day4()
        assertTrue(day4.isValidPassword(111111,false))
        assertFalse(day4.isValidPassword(223450,false))
        assertFalse(day4.isValidPassword(123789,false))
    }

    @Test
    fun puzzle1() {
        val day4 = Day4()
        val result = day4.numberOfValidPasswords(168630, 718098, false)
        println(result)
        assertEquals(1686, result)
    }

    @Test
    fun numberOfValidPasswordsStrict() {
        val day4 = Day4()
        assertTrue(day4.isValidPassword(112233,true))
        assertFalse(day4.isValidPassword(123444,true))
        assertTrue(day4.isValidPassword(111122,true))
    }

    @Test
    fun puzzle2() {
        val day4 = Day4()
        val result = day4.numberOfValidPasswords(168630, 718098, true)
        println(result)
        assertEquals(1145, result)
    }
}