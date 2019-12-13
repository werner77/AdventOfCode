package com.behindmedia.adventofcode2019

import org.junit.Test
import java.lang.StringBuilder
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextArea


class Day13Test {

    class GameState {
        var ballPosition = Coordinate.origin
        var paddlePosition = Coordinate.origin
        var ballDirection = Coordinate(0, 1)
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

        fun printMap(): String {

            val buffer = StringBuilder()
            val coordinates = map.keys
            val range = coordinates.range()

            for (coordinate in range) {
                val value = map[coordinate] ?: throw IllegalStateException("No tile found for $coordinate")

                val s = when (value) {
                    Tile.Empty -> " "
                    Tile.Wall -> "X"
                    Tile.Block -> "B"
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

    @Test
    fun execute() {

        val input = read("/day13.txt")
        val computer = Computer(input)

        var numberOfTiles = 0

        while (true) {
            val result = computer.process()

            for (i in 2 until result.outputs.size step 3) {
                if (result.outputs[i] == 2L) {
                    numberOfTiles++
                }
            }

            if (result.status == Computer.Status.Finished) {
                break
            }
        }

        println(numberOfTiles)
    }

    enum class Tile(val rawValue: Long) {
        Empty(0L), Wall(1L), Block(2L), Paddle(3L), Ball(4L);

        companion object {
            fun from(rawValue: Long): Tile {
                return values().first { rawValue == it.rawValue }
            }
        }
    }

    @Test
    fun puzzle2() {

        val program = Computer.parseEncodedState(read("/day13.txt")).toMutableList()

        program[0] = 2L

        val computer = Computer(program)
        val inputs = mutableListOf<Long>()

        val gameState = GameState()

        val textWindow = TextWindow()

        while (true) {
            val result = computer.process(inputs)
            val outputs = result.outputs

            gameState.parseOutputs(outputs)

            textWindow.setText(gameState.printMap())

            Thread.sleep(1000)

            // Predict the impact position of the ball and move the paddle there as fast as possible
            inputs.clear()

            if (gameState.paddlePosition.x < gameState.ballPosition.x) {
                inputs.add(1)
            } else if (gameState.paddlePosition.x > gameState.ballPosition.x) {
                inputs.add(-1)
            } else {
                inputs.add(0)
            }

            if (result.status == Computer.Status.Finished) {
                break
            }
        }

        gameState.printMap()
        println("Score = ${gameState.score}")

        println("Done")
    }


}

class TextWindow() {

    private val theText: JTextArea

    init  //constructor of the DisplayGuiHelp object that has the list passed to it on creation
    {
        val theFrame = JFrame()
        theFrame.title = "AdventOfCode Day 13"
        theFrame.setSize(500, 500)
        theFrame.setLocation(550, 400)
        val mainPanel = JPanel()
        theText = JTextArea(5, 25) //create the text area
        mainPanel.add(theText) //add the text area to the panel
        theFrame.contentPane.add(mainPanel) //add the panel to the frame
        theFrame.pack()
        theFrame.isVisible = true
    }

    fun setText(string: String) {
        theText.insert(string, 0)
    }
}