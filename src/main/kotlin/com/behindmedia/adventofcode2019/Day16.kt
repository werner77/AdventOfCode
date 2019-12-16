package com.behindmedia.adventofcode2019

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day16 {

    fun fft(list: List<Int>, numberOfPhases: Int): Int {
        val n = list.size
        var inputList = list.toMutableList()
        var outputList = list.toMutableList()

        for (phase in 0 until numberOfPhases) {
            for (elementIndex in inputList.indices) {
                var i = elementIndex
                var sum = 0
                var sign = 1
                while (i < n) {
                    var j = i
                    while (j < min(n, i + elementIndex + 1)) {
                        sum += sign * inputList[j++]
                    }
                    i = j + elementIndex + 1
                    sign = -sign
                }
                sum = abs(sum) % 10
                outputList[elementIndex] = sum
            }
            inputList = outputList.also { outputList = inputList }
        }
        return inputList.firstDigits(8)
    }

    fun fft2(list: List<Int>, numberOfPhases: Int): Int {
        val messageOffset = list.firstDigits(7)

        // We can simplify the algorithm greatly if the message offset is >= half the list size
        assert(messageOffset >= list.size / 2)

        val n = list.size
        var inputList = list.toMutableList()
        var outputList = list.toMutableList()

        for (phase in 0 until numberOfPhases) {
            var elementIndex = n - 1
            var sum = 0
            while (elementIndex >= messageOffset) {
                sum += inputList[elementIndex]
                outputList[elementIndex] = abs(sum) % 10
                elementIndex--
            }
            inputList = outputList.also { outputList = inputList }
        }
        return inputList.firstDigits(8, messageOffset)
    }

    fun fastFFT(list: List<Int>, numberOfPhases: Int, messageOffset: Int): Int {
        val n = list.size
        var inputList = list.toMutableList()
        var outputList = list.toMutableList()

        for (phase in 0 until numberOfPhases) {
            var elementIndex = n - 1
            var sum = 0
            var sumAbove = 0
            while (elementIndex >= max(messageOffset, 0)) {
                sum += inputList[elementIndex]

                // TODO: optimize
//                var j =
//                while (j < n) {
//
//                }

                sum -= sumAbove
                outputList[elementIndex] = abs(sum) % 10
                elementIndex--
            }
            inputList = outputList.also { outputList = inputList }
        }
        return inputList.firstDigits(8, messageOffset)
    }
}