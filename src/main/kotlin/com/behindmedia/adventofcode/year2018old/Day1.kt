package com.behindmedia.adventofcode.year2018old

class Day1 {

    fun calculateFrequency(start: Int, numbers: List<Int>): Int {
        var result = start
        for (number in numbers) {
            result += number
        }
        return result
    }

    fun findFirstDuplicateFrequency(start: Int, numbers: List<Int>): Int {

        var currentFrequency = start
        val encounteredFrequencies = mutableSetOf<Int>()
        var index = 0

        while(true) {
            val number = numbers[index]

            currentFrequency += number

            if (encounteredFrequencies.contains(currentFrequency)) {
                return currentFrequency
            }

            encounteredFrequencies.add(currentFrequency)

            index++
            if (index == numbers.count()) {
                index = 0
            }
        }

    }

}