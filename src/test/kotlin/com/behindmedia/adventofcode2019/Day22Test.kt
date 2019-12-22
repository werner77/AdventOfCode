package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day22Test {

    @Test
    fun parseInput() {
        val input = read("/day22.txt")
        val day22 = Day22()
        day22.parseInput(input)
    }

    @Test
    fun puzzle1() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val deck = Day22.Deck()

        day22.shuffle(deck, shuffleTechniques)

        val result = deck.positionOf(2019)
        println(result)
        assertEquals(4775, result)
    }

    @Test
    fun puzzle1a() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)
        val result = day22.shuffledCard(10007, 2019, shuffleTechniques)
        println(result)
        assertEquals(4775, result)
    }

    @Test
    fun puzzle2() {
        val input = read("/day22.txt")
        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)
        val result = day22.shuffledCard(119315717514047, 2020, shuffleTechniques, 101741582076661)

        println(result)
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
    }

    @Test
    fun sample3() {
        val input = """
            deal into new stack
            deal with increment 9
            deal with increment 9
            deal into new stack
        """.trimIndent()

        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val deck = Day22.Deck(10)
        day22.shuffle(deck, shuffleTechniques)

        println(deck)
    }

    @Test
    fun sample4() {
        val input = """
            deal with increment 55
            deal with increment 55
            deal with increment 55
            deal with increment 55
        """.trimIndent()

        val day22 = Day22()
        val shuffleTechniques = day22.parseInput(input)

        val deck = Day22.Deck()
        day22.shuffle(deck, shuffleTechniques)

        println(deck)
    }
}