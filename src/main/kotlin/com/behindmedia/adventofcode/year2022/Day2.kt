package com.behindmedia.adventofcode.year2022

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    /*
    Rock defeats Scissors, Scissors defeats Paper, and Paper defeats Rock

    A = Rock
    B = Paper
    C = Scissors

     */
    val data = parseLines("/2022/day2.txt") { line ->
        line.split(' ').map { if (it[0] <= 'C') it[0] - 'A' else it[0] - 'X' }
    }
    part1(data)
    part2(data)
}

private fun part1(data: List<List<Int>>) {
    var totalScore = 0
    for ((player1, player2) in data) {
        val winner = winner(player1, player2)
        val score = 1 + player2 + (winner + 1) * 3
        totalScore += score
    }
    println(totalScore)
}

private fun part2(data: List<List<Int>>) {
    var totalScore = 0
    for ((player1, expectedOutcome) in data) {
        val winner = expectedOutcome - 1
        val player2 = getPlayer2(player1, winner)
        val score = 1 + player2 + (winner + 1) * 3
        totalScore += score
    }
    println(totalScore)
}

private fun winner(player1: Int, player2: Int): Int {
    if (player1 == player2) return 0
    return when(player1) {
        0 -> if (player2 == 2) -1 else 1
        1 -> if (player2 == 0) -1 else 1
        2 -> if (player2 == 1) -1 else 1
        else -> error("Should not reach this line")
    }
}

private fun getPlayer2(player1: Int, winner: Int): Int {
    if (winner == 0) return player1
    return when (player1) {
        0 -> if (winner == -1) 2 else 1
        1 -> if (winner == -1) 0 else 2
        2 -> if (winner == -1) 1 else 0
        else -> error("Should not reach this line")
    }
}