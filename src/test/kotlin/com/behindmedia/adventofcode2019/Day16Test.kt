package com.behindmedia.adventofcode2019

import org.junit.Test

import java.lang.Math.abs
import kotlin.test.assertEquals

class Day16Test {

    @Test
    fun execute() {

        val input = parse("/day16.txt") {
            it.map { c -> c.toString().toInt() }
        }

        val output = fft(input, 100)

        for (i in 0 until 8) {
            print(output[i])
        }
        println()

    }

    @Test
    fun sample1() {
        val input = listOf(1,2,3,4,5,6,7,8)
        val result = fft(input, 1)

        println(result)
    }

    @Test
    fun testPatternValue() {
        assertEquals(1, patternValue(0, 0))
        assertEquals(0, patternValue(1, 0))
        assertEquals(-1, patternValue(2, 0))
        assertEquals(0, patternValue(3, 0))
        assertEquals(1, patternValue(4, 0))


        assertEquals(0, patternValue(0, 1))
        assertEquals(1, patternValue(1, 1))
        assertEquals(1, patternValue(2, 1))
        assertEquals(0, patternValue(3, 1))
        assertEquals(0, patternValue(4, 1))
    }

    fun patternValue(elementIndex: Int, passIndex: Int): Int {

        val pattern = arrayOf(0, 1, 0, -1)

        //val patternIndex = (elementIndex + 1) % 4

        val patternIndex = ((1 + elementIndex) / (passIndex + 1)) % 4

        return pattern[patternIndex]
    }

    fun fft(list: List<Int>, numberOfPhases: Int): List<Int> {

        val pattern = arrayOf(0, 1, 0, -1)
        var inputList = list

        for (phase in 0 until numberOfPhases) {

            val outputList = MutableList<Int>(inputList.size) { 0 }

            for (i in 0 until inputList.size) {
                var sum = 0L
                for ((index, value) in inputList.withIndex()) {
                    val patternIndex = ((1 + index) / (i + 1)) % 4
                    val patternValue = pattern[patternIndex]
                    sum += patternValue * value
                }

                sum = abs(sum) % 10
                outputList[i] = sum.toInt()
            }

            inputList = outputList
        }


        // 4 different pattern values: 0, 1, 0 , -1
        // repeated (i + 1) times each
        // (i + 1) / 4
        return inputList
    }
}