package com.behindmedia.adventofcode.year2023.day4

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class ScratchCard(val id: Int, val haveNumbers: Set<Long>, val winNumbers: Set<Long>) {

    val numberOfWins: Int by lazy {
        haveNumbers.intersect(winNumbers).size
    }

    val score: Long
        get() {
            val n = numberOfWins
            if (n == 0) return 0L
            var res = 1L
            repeat(n - 1) {
                res *= 2
            }
            return res
        }
}

fun main() {
    val data = parseLines("/2023/day4.txt") { line ->
        val (card, contents) = line.split(":")
        val (win, have) = contents.split("|")
        val cardId = card.splitNonEmptySequence(" ").mapNotNull { it.toIntOrNull() }.single()
        val haveNumbers = have.splitNonEmptySequence(" ").map { it.toLong() }.toSet()
        val winningNumbers = win.splitNonEmptySequence(" ").map { it.toLong() }.toSet()
        ScratchCard(cardId, haveNumbers, winningNumbers)
    }

    // Part 1
    println(data.sumOf { it.score })

    // Part 2
    println(process(data, 0, data.size))
}

private fun process(cards: List<ScratchCard>, start: Int, end: Int): Int {
    var ans = end - start
    for (i in start until end) {
        val card = cards[i]
        val wins = card.numberOfWins
        ans += process(cards, i + 1, i + 1 + wins)
    }
    return ans
}

