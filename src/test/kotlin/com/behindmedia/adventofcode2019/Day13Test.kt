package com.behindmedia.adventofcode2019

import org.junit.Test
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextArea
import kotlin.test.assertEquals


class Day13Test {

    @Test
    fun puzzle1() {
        val day13 = Day13()
        val program = read("/day13.txt")
        val result = day13.numberOfBlocks(program)

        println(result)
        assertEquals(335, result)
    }



    @Test
    fun puzzle2() {
        val visualize = false
        val program = read("/day13.txt")
        val day13 = Day13()
        var gui: TextWindow? = null

        if (visualize) {
            gui = TextWindow()
        }

        val finalGameState = day13.play(program) { gameState ->
            if (visualize) {
                gui?.setText(gameState.toString())
                Thread.sleep(1000 / 25)
            }
        }
        println(finalGameState.score)
        assertEquals(15706, finalGameState.score)
    }

    private class TextWindow() {

        private val theText: JTextArea

        init {
            val theFrame = JFrame()
            theFrame.title = "AdventOfCode Day 13"
            theFrame.setSize(300, 300)
            theFrame.setLocation(550, 400)
            val mainPanel = JPanel()
            theText = JTextArea(40, 40) //create the text area
            theText.font = Font("monospaced", Font.PLAIN, 12)
            mainPanel.add(theText) //add the text area to the panel
            theFrame.contentPane.add(mainPanel) //add the panel to the frame
            theFrame.pack()
            theFrame.isVisible = true
        }

        fun setText(string: String) {
            theText.text = string
        }
    }

}