package com.behindmedia.adventofcode.year2018
import org.junit.Test

import org.junit.Assert.*

class Day9Test {

    @Test
    fun winningScore1() {
        val result = Day9().winningScore(9, 25)
        assertEquals(32, result)
    }

    @Test
    fun winningScore2() {
        val result = Day9().winningScore(10, 1618)
        assertEquals(8317, result)
    }

    @Test
    fun winningScore3() {
        val result = Day9().winningScore(13, 7999)
        assertEquals(146373, result)
    }

    @Test
    fun winningScore4() {
        val result = Day9().winningScore(17, 1104)
        assertEquals(2764, result)
    }

    @Test
    fun winningScore5() {
        val result = Day9().winningScore(21, 6111)
        assertEquals(54718, result)
    }

    @Test
    fun winningScore6() {
        val result = Day9().winningScore(30, 5807)
        assertEquals(37305, result)
    }

    @Test
    fun winningScore() {
        val result = Day9().winningScore(486, 70833)
        println(result)
    }

    @Test
    fun winningScorePart2() {
        val result = Day9().winningScore(486, 70833 * 100)
        println(result)
    }

}