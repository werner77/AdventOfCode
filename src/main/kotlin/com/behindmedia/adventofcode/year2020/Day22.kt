package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.popFirst
import java.util.*

class Day22 {

    data class Deck(val player: Int, val cards: LinkedList<Int>) {
        val size: Int
            get() = cards.size

        fun isEmpty(): Boolean = cards.isEmpty()

        val score: Long
            get() = cards.foldIndexed(0L) { index, product, value ->
                product + (size - index) * value
            }

        fun pop(): Int {
            return cards.popFirst() ?: error("Pop should only be called when cards are present")
        }

        fun add(winningCard: Int, losingCard: Int) {
            cards.add(winningCard)
            cards.add(losingCard)
        }

        fun copyWithCardCount(count: Int): Deck {
            val newList = LinkedList<Int>()
            for ((i, c) in cards.withIndex()) {
                if (i >= count) break
                newList.add(c)
            }
            return Deck(player, newList)
        }
    }

    fun part1(input: String): Long {
        val decks = parseDecks(input)
        val winningDeck = play(decks, false)
        return winningDeck.score
    }

    fun part2(input: String): Long {
        val decks = parseDecks(input)
        val winningDeck = play(decks, true)
        return winningDeck.score
    }

    private fun parseDecks(input: String) : Pair<Deck, Deck> {
        var currentCards: MutableList<Int>? = null
        val map = LinkedHashMap<Int, LinkedList<Int>>()
        parseLines(input) { line ->
            when {
                line.isBlank() -> {
                    // ignore
                }
                line.startsWith("Player ") -> {
                    val playerId = line.split(' ', ':')[1].trim().toInt()
                    currentCards = LinkedList<Int>().also {
                        map[playerId] = it
                    }
                }
                else -> {
                    currentCards!!.add(line.trim().toInt())
                }
            }
        }
        return map.entries.map { Deck(it.key, it.value) }.toPair()
    }

    private fun play(decks: Pair<Deck, Deck>, recursiveEnabled: Boolean): Deck {
        val playedDecks = mutableSetOf<Deck>()
        while (true) {
            if (decks.first.isEmpty()) {
                return decks.second
            } else if (decks.second.isEmpty()) {
                return decks.first
            }

            if (playedDecks.contains(decks.first) || playedDecks.contains(decks.second)) {
                // Player 1 always wins if a repetition occurs
                return decks.first
            }

            decks.toList().forEach {
                playedDecks.add(it.copy())
            }

            val topCards = Pair(decks.first.pop(), decks.second.pop())

            val winningPlayer = if (recursiveEnabled && decks.first.size >= topCards.first && decks.second.size >= topCards.second) {
                // recursive round
                play(Pair(decks.first.copyWithCardCount(topCards.first), decks.second.copyWithCardCount(topCards.second)), recursiveEnabled).player
            } else if (topCards.first > topCards.second) {
                decks.first.player
            } else {
                decks.second.player
            }

            if (winningPlayer == decks.first.player) {
                decks.first.add(topCards.first, topCards.second)
            } else {
                decks.second.add(topCards.second, topCards.first)
            }
        }
    }

    private fun <T>List<T>.toPair(): Pair<T, T> {
        require (size == 2) { "List should be of size equal to 2" }
        val (a, b) = this
        return Pair(a, b)
    }
}