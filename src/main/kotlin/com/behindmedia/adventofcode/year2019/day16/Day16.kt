package com.behindmedia.adventofcode.year2019.day16

import com.behindmedia.adventofcode.common.firstDigits
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.times
import com.behindmedia.adventofcode.common.timing
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private fun fft(list: List<Int>, numberOfPhases: Int, messageOffset: Int): Int {
    val n = list.size
    var inputList = list.toMutableList()
    var outputList = list.toMutableList()

    for (phase in 0 until numberOfPhases) {

        // The idea is that we first determine the coreSum, which we can do incrementally,
        // it's the band of 1's in the middle.
        //
        // For elementIndex < n/2 we need to add some other elements as well

        // Start at the end
        var elementIndex = n - 1

        // Sum of elements in the middle, for elementIndex >= N/2 this is all we need
        var coreSum = 0

        // Because all elements with elementIndex < messageOffset are 0 we can stop once the elementIndex == messageOffset
        while (elementIndex >= max(messageOffset, 0)) {

            // Increment the coreSum with the current element
            coreSum += inputList[elementIndex]

            // If elementIndex < n/2 we remove the two elements that came in form the right to the core band
            var i = 2 * elementIndex + 1
            val h = min(n, i + 2)
            while (i < h) {
                coreSum -= inputList[i]
                i++
            }

            var sum = coreSum

            // Now add the rest of the elements
            // First element which is -1
            var j = 3 * elementIndex + 2

            // Initial sign
            var sign = -1

            while (j < n) {
                var k = j

                // Add a number of values equal to elementIndex + 1 (this is the number of elements with the same sign)
                val g = min(n, j + elementIndex + 1)

                while (k < g) {
                    sum += sign * inputList[k++]
                }

                // Move (elementIndex + 1) values to the right (in between are zeros)
                j = g + elementIndex + 1

                // Flip the sign
                sign = -sign
            }

            outputList[elementIndex] = abs(sum) % 10
            elementIndex--
        }
        inputList = outputList.also { outputList = inputList }
    }
    return inputList.firstDigits(8, messageOffset)
}

fun main() {
    val data = read("/2019/day16.txt").trim().map { it.digitToInt() }
    timing {
        // Part 1
        println(fft(data, 100, 0))

        // Part2
        val messageOffset = data.firstDigits(7)
        println(fft(data * 10_000, 100, messageOffset))
    }
}