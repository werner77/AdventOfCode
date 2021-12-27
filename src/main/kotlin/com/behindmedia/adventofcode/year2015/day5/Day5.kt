package com.behindmedia.adventofcode.year2015.day5

import com.behindmedia.adventofcode.common.*

private val vowels = "aeiou".toSet()

private fun isNice1(string: String): Boolean {
    var repeat = false
    var vowelCount = 0
    for ((i, c) in string.withIndex()) {
        if (c in vowels) vowelCount++
        if (i > 0 && string[i - 1] == c) repeat = true
        if (i > 0) {
            if (c == 'b' && string[i - 1] == 'a') return false
            if (c == 'd' && string[i - 1] == 'c') return false
            if (c == 'q' && string[i - 1] == 'p') return false
            if (c == 'y' && string[i - 1] == 'x') return false
        }
    }
    return vowelCount >= 3 && repeat
}

private fun isNice2(string: String): Boolean {
    var repeat = false
    val pairs = mutableMapOf<Pair<Char, Char>, Int>()
    var foundPair = false
    for ((i, c) in string.withIndex()) {
        if (i > 0) {
            val pair = Pair(string[i - 1], c)
            val existing = pairs[pair]
            if (existing == null) {
                pairs[pair] = i + 1
            } else if (existing < i) {
                foundPair = true
            }
        }
        if (i > 1) {
            val c2 = string[i - 2]
            if (c == c2) repeat = true
        }
    }
    return repeat && foundPair
}

fun main() {
    val data = parseLines("/2015/day5.txt") { line ->
        line
    }
    println(data.count { isNice1(it) })
    println(data.count { isNice2(it) })
}