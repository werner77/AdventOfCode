package com.behindmedia.adventofcode.year2022.day2

import com.behindmedia.adventofcode.common.*

fun main() {
    /*
    Rock defeats Scissors, Scissors defeats Paper, and Paper defeats Rock
    0 = Rock
    1 = Paper
    2 = Scissors

    Winner:
    0 = Player1
    1 = Draw
    2 = Player2
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
        val winner = getWinner(player1, player2)
        val score = 1 + player2 + winner * 3
        totalScore += score
    }
    println(totalScore)
}

private fun part2(data: List<List<Int>>) {
    var totalScore = 0
    for ((player1, winner) in data) {
        val player2 = getPlayer2(player1, winner)
        val score = 1 + player2 + winner * 3
        totalScore += score
    }
    println(totalScore)
}

private fun getWinner(player1: Int, player2: Int): Int {
    if (player1 == player2) return 1
    return when(player1) {
        0 -> if (player2 == 2) 0 else 2
        1 -> if (player2 == 0) 0 else 2
        2 -> if (player2 == 1) 0 else 2
        else -> error("Should not reach this line")
    }
}

private fun getPlayer2(player1: Int, winner: Int): Int {
    if (winner == 1) return player1
    return when (player1) {
        0 -> if (winner == 0) 2 else 1
        1 -> if (winner == 0) 0 else 2
        2 -> if (winner == 0) 1 else 0
        else -> error("Should not reach this line")
    }
}