package com.behindmedia.adventofcode.year2021.day21

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private data class Player(val id: Int, var pos: Int, var score: Long = 0) {
    fun increment(inc: Int) {
        pos = (pos - 1 + inc) % 10 + 1
        score += pos
    }
}

private data class State(var player1: Player, var player2: Player, var dieState: Int = 1) {
    val losingPlayerScore: Long
        get() = min(player1.score, player2.score)

    var dieRollCount = 0
    var currentPlayer = 0

    fun play(): Boolean {
        var increment = 0
        for (i in 0 until 3) {
            increment += (dieState++)
            if (dieState > 100) dieState = 1
        }
        dieRollCount += 3
        val player = if (currentPlayer == 0) player1 else player2
        player.increment(increment)
        currentPlayer = (currentPlayer + 1) % 2
        return max(player1.score, player2.score) >= 1000
    }
}

private data class GameState(val player1: PlayerState, val player2: PlayerState) {
    fun winningPlayer(winningScore: Int): Int {
        return if (player1.score >= winningScore) {
            1
        } else if (player2.score >= winningScore) {
            2
        } else {
            0
        }
    }

    inline fun possibleTurns(crossinline perform: (GameState) -> Unit) {
        player1.dirac { state1 ->
            if (state1.score >= 21) {
                perform(GameState(state1, this.player2))
            } else {
                player2.dirac { state2 ->
                    perform(GameState(state1, state2))
                }
            }
        }
    }
}

private data class PlayerState(val pos: Int, val score: Int) {

    companion object {
        inline fun forEachStateBelow(winningScore: Int, perform: (PlayerState) -> Unit) {
            for (score in 0 until winningScore) {
                for (pos in 1..10) {
                    perform(PlayerState(pos, score))
                }
            }
        }
    }

    fun increment(inc: Int): PlayerState {
        val newPos = (pos - 1 + inc) % 10 + 1
        return PlayerState(newPos, score + newPos)
    }

    inline fun dirac(crossinline perform: (PlayerState) -> Unit) {
        permutate(3, 1..3) {
            perform(this.increment(it.sum()))
            null
        }
    }
}

private fun getWins(start1: Int, start2: Int): Pair<Long, Long> {
    val dp = defaultMutableMapOf<GameState, Long> { 0L }
    val startState = GameState(PlayerState(start1, 0), PlayerState(start2, 0))
    dp[startState] = 1L

    val winningScore = 21
    var total = 0L
    var wins1 = 0L
    var wins2 = 0L
    PlayerState.forEachStateBelow(winningScore) { state1 ->
        PlayerState.forEachStateBelow(winningScore) { state2 ->
            val gameState = GameState(state1, state2)
            val currentWays = dp[gameState]
            if (currentWays > 0L) {
                gameState.possibleTurns {
                    dp[it] += currentWays
                    when (it.winningPlayer(winningScore)) {
                        1 -> {
                            wins1 += currentWays
                            total += currentWays
                        }
                        2 -> {
                            wins2 += currentWays
                            total += currentWays
                        }
                    }
                }
            }

        }
    }
    return Pair(wins1, wins2)
}

fun main() {
    val (player1, player2) = parseLines("/2021/day21.txt") { line ->
        line.split(" ").map { it.trim() }.last().toInt()
    }
    val state = State(Player(1, player1), Player(2, player2), 1)
    while (!state.play());

    // Part 1
    println(state.losingPlayerScore * state.dieRollCount)

    timing {
        // Part 2
        val (wins1, wins2) = getWins(player1, player2)
        println(max(wins1, wins2))
    }
}