package com.behindmedia.adventofcode.year2021.day4

import com.behindmedia.adventofcode.common.*

private fun <T> parseLines(lineParser: (String) -> T): List<T> {
    return parseLines("/2021/day4.txt") {
        lineParser.invoke(it)
    }
}

private class Board {
    companion object {
        const val SIZE = 5
    }

    private val numberMap = mutableMapOf<Int, Coordinate>()
    private val markedCoordinates = mutableSetOf<Coordinate>()
    private var currentCoordinate = Coordinate.origin

    val unmarkedNumbers: Set<Int>
        get() = numberMap.asSequence().filter { !markedCoordinates.contains(it.value) }.map { it.key }.toSet()

    fun addNumber(n: Int) {
        require(currentCoordinate.x in 0 until SIZE && currentCoordinate.y in 0 until SIZE) {
            "Board is already fully filled"
        }
        numberMap[n] = currentCoordinate
        currentCoordinate = if (currentCoordinate.x == SIZE - 1) {
            Coordinate(x = 0, y = currentCoordinate.y + 1)
        } else {
            currentCoordinate.copy(x = currentCoordinate.x + 1)
        }
    }

    fun scratchNumber(n: Int): Boolean {
        val c = numberMap[n]
        if (c != null) {
            markedCoordinates.add(c)
        }
        return hasWin()
    }

    fun hasWin(): Boolean {
        for (i in 0 until SIZE) {
            if ((0 until SIZE).map { Coordinate(i , it) }.all { markedCoordinates.contains(it) }) return true
            if ((0 until SIZE).map { Coordinate(it, i) }.all { markedCoordinates.contains(it) }) return true
        }
        return false
    }
}

fun main() {
    var first = true
    val randomNumbers = mutableListOf<Int>()
    val boards = mutableListOf<Board>()
    parseLines { line ->
        if (line.isBlank()) {
            // Add new board
            boards.add(Board())
        } else {
            if (first) {
                // parse random numbers
                randomNumbers.addAll(line.splitNonEmptySequence(" ", ",") { it.toInt() })
                first = false
            } else {
                // parse boards
                val board = boards.last()
                line.splitNonEmptySequence(" ") { it.toInt() }.forEach { board.addNumber(it) }
            }
        }
    }
    val wins = mutableListOf<Int>()
    val winningBoards = mutableSetOf<Board>()
    for (n in randomNumbers) {
        for (board in boards) {
            if (winningBoards.contains(board)) continue
            val hasWin = board.scratchNumber(n)
            if (hasWin) {
                wins.add(n * board.unmarkedNumbers.sum())
                winningBoards.add(board)
            }
        }
    }

    // Part 1
    println(wins.first())

    // Part 2
    println(wins.last())
}