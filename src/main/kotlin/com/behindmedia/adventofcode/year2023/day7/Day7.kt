package com.behindmedia.adventofcode.year2023.day7

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.withDefaultValue
import com.behindmedia.adventofcode.year2023.day7.Card.Companion.JOKER
import kotlin.math.min


private data class Card(val symbol: Char) : Comparable<Card> {
    companion object {
        private val VALUES: Map<Char, Int> =
            listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2', '*')
                .reversed()
                .withIndex()
                .fold(mutableMapOf()) { map, (index, value) ->
                    map.apply {
                        put(value, index)
                    }
                }
        val JOKER = Card('*')
    }
    val isJoker: Boolean
        get() = this == JOKER
    fun withJoker(): Card = if (symbol == 'J') JOKER else this
    override fun compareTo(other: Card): Int = VALUES[this.symbol]!!.compareTo(VALUES[other.symbol]!!)
}

private data class Hand(val cards: List<Card>, val bid: Long) : Comparable<Hand> {
    fun withJokers(): Hand {
        return copy(cards = cards.map { it.withJoker() })
    }

    private val sameCounts: List<Int> by lazy {
        val groupedCards = cards.groupBy { it }.withDefaultValue { emptyList() }
        var jokerCount = groupedCards[JOKER].size
        val otherCounts =
            groupedCards.filter { !it.key.isJoker }.map { it.value.size }.sortedDescending().toMutableList()
        if (otherCounts.isEmpty()) {
            listOf(jokerCount)
        } else {
            // Distribute the jokers across the other count
            var i = 0
            while (jokerCount > 0) {
                require(i < otherCounts.size)
                val increment = min(5 - otherCounts[i], jokerCount)
                otherCounts[i] += increment
                jokerCount -= increment
                i++
            }
            otherCounts
        }
    }

    val combinationValue: Long by lazy {
        when {
            sameCounts[0] == 5 -> {
                // Five of a kind
                7
            }

            sameCounts[0] == 4 -> {
                // Four of a kind
                6
            }

            sameCounts[0] == 3 && sameCounts[1] == 2 -> {
                // Full house
                5
            }

            sameCounts[0] == 3 -> {
                // Three of a kind
                4
            }

            sameCounts[0] == 2 && sameCounts[1] == 2 -> {
                // Two pair
                3
            }

            sameCounts[0] == 2 -> {
                // One pair
                2
            }

            else -> {
                // High card
                1
            }
        }
    }

    override fun compareTo(other: Hand): Int {
        return combinationValue.compareTo(other.combinationValue).takeIf { it != 0 } ?: run {
            for (i in 0 until 5) {
                return cards[i].compareTo(other.cards[i]).takeIf { it != 0 } ?: continue
            }
            0
        }
    }
}

fun main() {
    val data = parseLines("/2023/day7.txt") { line ->
        val (hand, bid) = line.split(" ")
        Hand(hand.map { Card(it) }, bid.toLong())
    }

    // Part 1
    println(data.sorted().withIndex().sumOf { (i, hand) -> (i + 1) * hand.bid })

    // Part 2
    println(data.map { it.withJokers() }.sorted().withIndex().sumOf { (i, hand) -> (i + 1) * hand.bid })
}