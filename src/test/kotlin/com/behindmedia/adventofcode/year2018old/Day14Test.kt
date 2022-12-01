package com.behindmedia.adventofcode.year2018old
import org.junit.Assert.assertEquals
import org.junit.Test

class Day14Test {

    @Test
    fun recipeScores1() {
        val result = Day14().recipeScores(5)
        val expected = "0124515891"
        assertEquals(expected, result)
    }

    @Test
    fun recipeScores2() {
        val result = Day14().recipeScores(9)
        val expected = "5158916779"
        assertEquals(expected, result)
    }

    @Test
    fun recipeScores3() {
        val result = Day14().recipeScores(18)
        val expected = "9251071085"
        assertEquals(expected, result)
    }

    @Test
    fun recipeScores4() {
        val result = Day14().recipeScores(2018)
        val expected = "5941429882"
        assertEquals(expected, result)
    }

    @Test
    fun recipeScores() {
        val result = Day14().recipeScores(556061)
        println(result)
    }

    @Test
    fun recipeCount1() {
        val result = Day14().recipeCount("51589")
        val expected = 9
        assertEquals(expected, result)
    }

    @Test
    fun recipeCount2() {
        val result = Day14().recipeCount("01245")
        val expected = 5
        assertEquals(expected, result)
    }

    @Test
    fun recipeCount3() {
        val result = Day14().recipeCount("92510")
        val expected = 18
        assertEquals(expected, result)
    }

    @Test
    fun recipeCount4() {
        val result = Day14().recipeCount("59414")
        val expected = 2018
        assertEquals(expected, result)
    }

    @Test
    fun recipeCount() {
        val result = Day14().recipeCount("556061")
        println(result)
    }

}