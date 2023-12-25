package com.behindmedia.adventofcode.year2021.day21

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private data class Player(val pos: Int, val score: Int) {
    companion object {
        inline fun forEachStateBelow(winningScore: Int, perform: (Player) -> Unit) {
            for (score in 0 until winningScore) {
                for (pos in 1..10) {
                    perform(Player(pos, score))
                }
            }
        }
    }

    fun increment(inc: Int): Player {
        val newPos = (pos - 1 + inc) % 10 + 1
        return Player(newPos, score + newPos)
    }

    inline fun playDirac(crossinline perform: (Player) -> Unit) {
        permute(3, 1..3) {
            perform(increment(it.sum()))
            null
        }
    }

    fun playDeterministic(dieState: Int): Pair<Player, Int> {
        var inc = 0
        var current = dieState
        for (i in 0 until 3) {
            inc += (current++)
            if (current > 100) current = 1
        }
        return Pair(increment(inc), current)
    }
}

private data class Game(val player1: Player, val player2: Player, val currentPlayerIndex: Int) {
    fun winning(winningScore: Int): Boolean {
        return otherPlayer.score >= winningScore
    }

    val otherPlayerIndex: Int
        get() = (currentPlayerIndex + 1) % 2

    val currentPlayer: Player
        get() = if (currentPlayerIndex == 0) player1 else player2

    val otherPlayer: Player
        get() = if (currentPlayerIndex == 0) player2 else player1

    inline fun dirac(crossinline perform: (Game) -> Unit) {
        if (currentPlayerIndex == 0) {
            player1.playDirac {
                perform(Game(it, player2, 1))
            }
        } else {
            player2.playDirac {
                perform(Game(player1, it, 0))
            }
        }
    }

    fun deterministic(dieState: Int): Pair<Game, Int> {
        val (player, newDieState) = currentPlayer.playDeterministic(dieState)
        return if (currentPlayerIndex == 0) {
            Pair(Game(player, player2, 1), newDieState)
        } else {
            Pair(Game(player1, player, 0), newDieState)
        }
    }

    fun playDirac(winningScore: Int): Pair<Long, Long> {
        val wins = LongArray(2) { 0L }
        val dp = defaultMutableMapOf<Game, Long> { 0L }
        dp[this] = 1L
        Player.forEachStateBelow(winningScore) { state1 ->
            Player.forEachStateBelow(winningScore) { state2 ->
                for (currentPlayer in listOf(0, 1)) {
                    val game = Game(state1, state2, currentPlayer)
                    val ways = dp[game]
                    if (ways > 0L) {
                        game.dirac { nextState ->
                            if (nextState.winning(winningScore)) {
                                wins[nextState.otherPlayerIndex] += ways
                            }
                            dp[nextState] += ways
                        }
                    }
                }
            }
        }
        return Pair(wins[0], wins[1])
    }

    fun playDeterministic(winningScore: Int): Long {
        var current = this
        var diceRollCount = 0L
        var dieState = 1
        while (!current.winning(winningScore)) {
            val result = current.deterministic(dieState)
            current = result.first
            dieState = result.second
            diceRollCount += 3L
        }
        return current.currentPlayer.score * diceRollCount
    }
}

fun main() {
    val (player1, player2) = parseLines("/2021/day21.txt") { line ->
        line.split(" ").map { it.trim() }.last().toInt()
    }
    val startState = Game(Player(player1, 0), Player(player2, 0), 0)
    println(startState.playDeterministic(1000))
    // Part 2
    timing {
        val (wins1, wins2) = startState.playDirac(21)
        println(max(wins1, wins2))
    }
}