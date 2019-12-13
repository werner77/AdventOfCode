package com.behindmedia.adventofcode2019

import java.lang.StringBuilder

class Day13 {

    enum class Tile(val rawValue: Long) {
        Empty(0L), Wall(1L), Block(2L), Paddle(3L), Ball(4L);

        companion object {
            fun from(rawValue: Long): Tile {
                return values().first { rawValue == it.rawValue }
            }
        }
    }

    class GameState {
        var ballPosition = Coordinate.origin
        var paddlePosition = Coordinate.origin
        var score = 0L
        val map = mutableMapOf<Coordinate, Tile>()

        fun parseOutputs(outputs: List<Long>) {
            var i = 0
            while (i < outputs.size) {
                val x = outputs[i]
                val y = outputs[i + 1]
                val tileValue = outputs[i + 2]

                if (x == -1L && y == 0L) {
                    //score
                    score = tileValue
                } else {
                    val tile = Tile.from(tileValue)
                    val coordinate = Coordinate(x.toInt(), y.toInt())
                    map[coordinate] = tile

                    when (tile) {
                        Tile.Ball -> ballPosition = coordinate
                        Tile.Paddle -> paddlePosition = coordinate
                    }
                }
                i += 3
            }
        }

        override fun toString(): String {
            val buffer = StringBuilder()
            val coordinates = map.keys
            val range = coordinates.range()

            buffer.append("Score: $score\n\n")

            for (coordinate in range) {
                val value = map[coordinate] ?: throw IllegalStateException("No tile found for $coordinate")

                val s = when (value) {
                    Tile.Empty -> " "
                    Tile.Wall -> "X"
                    Tile.Block -> "\u25A1"
                    Tile.Paddle -> "="
                    Tile.Ball -> "O"
                }
                buffer.append(s)
                if (coordinate.x == range.endInclusive.x) {
                    buffer.append("\n")
                }
            }
            return buffer.toString()
        }
    }

    fun numberOfBlocks(program: String): Int {
        val computer = Computer(program)
        val result = computer.process()
        assert(result.status == Computer.Status.Finished)

        val gameState = GameState()
        gameState.parseOutputs(result.outputs)

        return gameState.map.values.count { it == Tile.Block }
    }

    fun play(program: String, onUpdate: (GameState) -> Unit): GameState {
        val decodedProgram = Computer.parseEncodedState(program).toMutableList()
        decodedProgram[0] = 2
        val computer = Computer(decodedProgram)
        var input: Long? = null

        val gameState = GameState()

        while (computer.status != Computer.Status.Finished) {
            val inputs = if (input == null) listOf() else listOf(input)

            val result = computer.process(inputs)
            val outputs = result.outputs

            gameState.parseOutputs(outputs)

            onUpdate(gameState)

            input = when {
                gameState.paddlePosition.x < gameState.ballPosition.x -> 1
                gameState.paddlePosition.x > gameState.ballPosition.x -> -1
                else -> 0
            }
        }

        return gameState
    }
}