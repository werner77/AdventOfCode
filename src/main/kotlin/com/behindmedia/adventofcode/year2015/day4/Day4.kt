package com.behindmedia.adventofcode.year2015.day4

import com.behindmedia.adventofcode.common.*

fun main() {
    val input = read("/2015/day4.txt").trim()

    println(solve(input, 5))
    println(solve(input, 6))
}

private fun solve(input: String, zeroCount: Int): Int {
    var i = 0
    while (true) {
        val hash = md5(input + i)
        if ((0 until zeroCount).all { hash[it] == '0' }) {
            break
        }
        i++
    }
    return i
}