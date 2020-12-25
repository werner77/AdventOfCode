package com.behindmedia.adventofcode.year2020

class Day25 {

    fun part1(key1: Long, key2: Long): Long {
        // Find loop size
        val loopSize1 = findLoopSize(expectedValue = key1)
        return findKey(key2, loopSize1)
    }

    private fun findKey(subjectNumber: Long, loopSize: Long) : Long {
        var value = 1L
        var i = 0L
        while(i < loopSize) {
            value = transform(value, subjectNumber)
            i++
        }
        return value
    }

    private fun findLoopSize(subjectNumber: Long = 7, expectedValue: Long) : Long {
        var value = 1L
        var loopSize = 0L
        while (value != expectedValue) {
            value = transform(value, subjectNumber)
            loopSize++
        }
        return loopSize
    }

    private fun transform(value: Long, subjectNumber: Long): Long {
        var value1 = value
        value1 *= subjectNumber
        value1 %= 20201227
        return value1
    }
}