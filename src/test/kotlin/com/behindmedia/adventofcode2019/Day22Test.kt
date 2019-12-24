package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*
import java.math.BigInteger

class Day22Test {

    @Test
    fun puzzle1() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val deck = Day22.Deck(10007)

        day22.shuffle(deck, shuffleTechniques)

        val result = deck.positionOf(2019)
        println(result)
        assertEquals(4775, result)
    }

    @Test
    fun puzzle2() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)
        val result = day22.shuffledCard(119315717514047, 2020, shuffleTechniques, true,
            101741582076661)
        println(result)
        assertEquals(37889219674304, result)
    }

    @Test
    fun shuffleForward() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)
        val result = day22.shuffledCard(10007, 2019, shuffleTechniques)
        println(result)
        assertEquals(4775, result)
    }

    @Test
    fun shuffleInverse() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)
        val result = day22.shuffledCard(10007, 4775, shuffleTechniques, true)
        println(result)
        assertEquals(2019, result)
    }

    @Test
    fun shuffleRepeatedWithInverse() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)
        val result1 = day22.shuffledCard(10007, 2019, shuffleTechniques, false, 1000)
        val result2 = day22.shuffledCard(10007, result1, shuffleTechniques, true, 1000)

        println(result2)
        assertEquals(result2, 2019)
    }

    @Test
    fun shuffleOptimized() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)
        val result1 = day22.shuffledCard(10007, 2019, shuffleTechniques, true, 1013)
        val result2 = day22.shuffledCard(10007, 2019, shuffleTechniques, true, 1013)

        assertEquals(result1, result2)
    }

    @Test
    fun sample1() {
        val input = """
            deal with increment 7
            deal into new stack
            deal into new stack
        """.trimIndent()
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val deck = Day22.Deck(10)
        day22.shuffle(deck, shuffleTechniques)

        println(deck)
        assertEquals("0 3 6 9 2 5 8 1 4 7", deck.toString())
    }

    @Test
    fun sample2() {
        val input = """
            cut 6
            deal with increment 7
            deal into new stack
        """.trimIndent()

        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val deck = Day22.Deck(10)
        day22.shuffle(deck, shuffleTechniques)

        println(deck)
        assertEquals("3 0 7 4 1 8 5 2 9 6", deck.toString())
    }

    @Test
    fun testInverseSimple() {
        val input = """
            cut 6
            deal with increment 7
            deal into new stack
        """.trimIndent()

        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val result = day22.shuffledCard(10, 2, shuffleTechniques, true)

        assertEquals(7 , result)
    }

    @Test
    fun testSimple() {
        val input = """
            cut 6
            deal with increment 7
            deal into new stack
        """.trimIndent()

        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val result = day22.shuffledCard(10, 7, shuffleTechniques, false)

        assertEquals(2, result)
    }
}