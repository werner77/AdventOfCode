package com.behindmedia.adventofcode2019

import org.junit.Test
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
        val program = read("/day13.txt")
        val day13 = Day13()
        val gui: TextWindow? = null
        //uncomment to enable visualization
        //val gui = TextWindow("AdventOfCode Day 13")

        val finalGameState = day13.play(program) { gameState ->
            gui?.let {
                it.setText(gameState.toString())
                Thread.sleep(1000 / 25)
            }
        }
        println(finalGameState.score)
        assertEquals(15706, finalGameState.score)
    }

}