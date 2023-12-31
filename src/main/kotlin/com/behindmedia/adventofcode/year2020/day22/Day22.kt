package com.behindmedia.adventofcode.year2020.day22

import com.behindmedia.adventofcode.common.read

private typealias Card = Int
private fun List<Card>.score(): Int {
    val size = this.size
    return this.withIndex().fold(0) { current, (index, value) ->
        current + (size - index) * value
    }
}

private fun parseDeck(string: String): List<Card> {
    return ArrayDeque(string.split("\n").filter { it.isNotEmpty() }.drop(1).map { it.toInt() })
}

fun main() {
    val text = read("/2020/day22.txt")
    val (deck1, deck2) = text.split("\n\n").map { parseDeck(it) }
    println(play(deck1 to deck2))
    println(playRecursive(deck1 to deck2))
}

private fun play(decks: Pair<List<Card>, List<Card>>): Pair<Int, Int> {
    val deck1 = ArrayDeque(decks.first)
    val deck2 = ArrayDeque(decks.second)
    while(true) {
        val card1 = deck1.removeFirst()
        val card2 = deck2.removeFirst()
        if (card1 > card2) {
            deck1.addLast(card1)
            deck1.addLast(card2)
        } else if (card2 > card1) {
            deck2.addLast(card2)
            deck2.addLast(card1)
        } else {
            error("Draw!")
        }
        if (deck1.isEmpty()) {
            return 2 to deck2.score()
        } else if (deck2.isEmpty()) {
            return 1 to deck1.score()
        }
    }
}

private fun playRecursive(decks: Pair<List<Card>, List<Card>>): Pair<Int, Int> {
    val seen = mutableSetOf<Pair<List<Card>, List<Card>>>()
    val deck1 = ArrayDeque(decks.first)
    val deck2 = ArrayDeque(decks.second)
    while (true) {
        val pair = deck1.toList() to deck2.toList()
        if (pair in seen) {
            return 1 to deck1.score()
        }
        seen += pair
        val card1 = deck1.removeFirst()
        val card2 = deck2.removeFirst()
        val winner = if (deck1.size >= card1 && deck2.size >= card2) {
            // Play recursive
            playRecursive(deck1.subList(0, card1) to deck2.subList(0, card2)).first
        } else {
            if (card1 > card2) {
                1
            } else if (card2 > card1) {
                2
            } else {
                error("Draw!")
            }
        }
        if (winner == 1) {
            deck1.addLast(card1)
            deck1.addLast(card2)
        } else if (winner == 2) {
            deck2.addLast(card2)
            deck2.addLast(card1)
        } else {
            error("Invalid winner: $winner")
        }
        if (deck1.isEmpty()) {
            return 2 to deck2.score()
        } else if (deck2.isEmpty()) {
            return 1 to deck1.score()
        }
    }
}