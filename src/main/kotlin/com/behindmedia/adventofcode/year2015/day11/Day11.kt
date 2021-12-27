package com.behindmedia.adventofcode.year2015.day11

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun isValid(string: CharArray): Boolean {
    /*
    Passwords must include one increasing straight of at least three letters, like abc, bcd, cde, and so on, up to xyz. They cannot skip letters; abd doesn't count.
Passwords may not contain the letters i, o, or l, as these letters can be mistaken for other characters and are therefore confusing.
Passwords must contain at least two different, non-overlapping pairs of letters, like aa, bb, or zz.
     */
    var foundIncreasingSequence = false
    var pairCount = 0
    var firstPairIndex = -1
    for ((i, c) in string.withIndex()) {
        if (c in setOf('i', 'o', 'l')) return false
        if (i > 1 && c > 'b') {
            foundIncreasingSequence = foundIncreasingSequence || (1..2).all { j ->
                string[i - j] == c - j
            }
        }
        if (i > 0) {
            if (string[i - 1] == c && i - 1 > firstPairIndex) {
                pairCount++
                if (firstPairIndex < 0) firstPairIndex = i
            }
        }
    }
    return foundIncreasingSequence && pairCount >= 2
}

private fun increment(array: CharArray) {
    var i = array.size - 1
    while(i >= 0) {
        if (array[i] == 'z') {
            array[i] = 'a'
            i--
        } else {
            array[i]++
            return
        }
    }
    error("Should not reach this line")
}

fun main() {
    val input = read("/2015/day11.txt").trim()
    val current = input.toCharArray()
    repeat(2) {
        while (true) {
            if (isValid(current)) break
            increment(current)
        }
        println(current.joinToString(separator = ""))
        increment(current)
    }
}