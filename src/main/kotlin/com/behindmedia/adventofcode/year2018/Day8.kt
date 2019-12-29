package com.behindmedia.adventofcode.year2018

class Day8 {

    class Sum {
        var value: Int = 0
    }

    fun readNode(input: List<Int>, index: Int, sum: Sum): Int {
        val numberOfChildren = input[index]
        val numberOfMetaDataEntries = input[index + 1]
        var currentIndex = index + 2

        for (i in 0 until numberOfChildren) {
            currentIndex = readNode(input, currentIndex, sum)
        }

        val max = currentIndex + numberOfMetaDataEntries
        while (currentIndex < max) {
            sum.value += input[currentIndex++]
        }
        return currentIndex
    }

    fun readNode2(input: List<Int>, index: Int): Pair<Int, Int> {
        val numberOfChildren = input[index]
        val numberOfMetaDataEntries = input[index + 1]
        var currentIndex = index + 2

        val nodeValues = IntArray(numberOfChildren)

        for (i in 0 until numberOfChildren) {
            val result = readNode2(input, currentIndex)
            currentIndex = result.first
            nodeValues[i] = result.second
        }

        var currentNodeValue = 0
        val max = currentIndex + numberOfMetaDataEntries
        while (currentIndex < max) {
            if (numberOfChildren == 0) {
                //Simply add the values
                currentNodeValue += input[currentIndex]
            } else {
                val childNodeIndex = input[currentIndex] - 1
                val childNodeValue = if (childNodeIndex >= 0 && childNodeIndex < numberOfChildren) nodeValues[childNodeIndex] else 0
                currentNodeValue += childNodeValue
            }
            currentIndex++
        }
        return Pair(currentIndex, currentNodeValue)
    }

    fun process(input: List<Int>): Int {
        val sum = Sum()
        readNode(input, 0, sum)
        return sum.value
    }

    fun process2(input: List<Int>): Int {
        val result = readNode2(input, 0)
        return result.second
    }

}