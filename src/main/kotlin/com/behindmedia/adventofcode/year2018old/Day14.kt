package com.behindmedia.adventofcode.year2018old

class Day14 {

    fun addToScores(next: Int, scores: MutableList<Int>, expected: List<Int>, expectedIndex: Int): Int? {
        val newExpectedIndex: Int
        if (next == expected[expectedIndex]) {
            newExpectedIndex = expectedIndex + 1
        } else if (next == expected[0]) {
            newExpectedIndex = 1
        } else {
            newExpectedIndex = 0
        }

        scores.add(next)
        if (newExpectedIndex == expected.size) {
            return null
        }
        return newExpectedIndex
    }

    fun recipeCount(before: String): Int {

        val expected = before.toCharArray().map { it.toString().toInt() }
        val scores = mutableListOf(3, 7)

        var currentIndex1 = 0
        var currentIndex2 = 1
        var expectedIndex = 0

        while (true) {
            val current1 = scores[currentIndex1]
            val current2 = scores[currentIndex2]

            val sum = current1 + current2
            val next1 = sum / 10
            val next2 = sum % 10

            if (next1 != 0) {
                expectedIndex = addToScores(next1, scores, expected, expectedIndex) ?: return (scores.size - expected.size)
            }
            expectedIndex = addToScores(next2, scores, expected, expectedIndex) ?: return (scores.size - expected.size)

            val step1 = (1 + current1)
            val step2 = (1 + current2)

            currentIndex1 = (currentIndex1 + step1) % scores.size
            currentIndex2 = (currentIndex2 + step2) % scores.size
        }
    }


    fun recipeScores(after: Int, count: Int = 10): String {

        val scores = mutableListOf<Int>(3, 7)

        var currentIndex1 = 0
        var currentIndex2 = 1
        val limit = after + count

        while (scores.size < limit) {
            val current1 = scores[currentIndex1]
            val current2 = scores[currentIndex2]

            val sum = current1 + current2
            val next1 = sum / 10
            val next2 = sum % 10

            if (next1 != 0) {
                scores.add(next1)
            }
            scores.add(next2)

            val step1 = (1 + current1)
            val step2 = (1 + current2)

            currentIndex1 = (currentIndex1 + step1) % scores.size
            currentIndex2 = (currentIndex2 + step2) % scores.size
        }

        val ret = StringBuilder()
        for (i in 0 until count) {
            val digit = scores[after + i]
            ret.append(digit.toString())
        }
        return ret.toString()
    }

}