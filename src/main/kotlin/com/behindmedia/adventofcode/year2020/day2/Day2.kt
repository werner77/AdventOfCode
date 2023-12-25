package com.behindmedia.adventofcode.year2020.day2

import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.parseLines

private data class Policy(val min: Int, val max: Int, val letter: Char, val password: String) {
    fun isValid(part1: Boolean): Boolean {
        return if (part1) {
            val counter = defaultMutableMapOf<Char, Int>() { 0 }
            for (c in password) {
                counter[c]++
            }
            counter[letter] in min..max
        } else {
            (password.getOrNull(min - 1) == letter).xor(password.getOrNull(max - 1) == letter)
        }
    }
}

fun main() {
    val data = parseLines("/2020/day2.txt") { line ->
        val (policy, letter, password) = line.split(" ", ":").filter { it.isNotBlank() }.map { it.trim() }
        val (min, max) = policy.split("-").map { it.toInt() }
        require(letter.length == 1)
        Policy(min, max, letter[0], password)
    }
    println(data.count { it.isValid(true) })
    println(data.count { it.isValid(false) })
}