package com.behindmedia.adventofcode.year2016.day6

import com.behindmedia.adventofcode.common.parseLines

private fun part1() {
    val countMap = parse()
    var i = 0
    while (true) {
        val m = countMap[i] ?: break
        val c = m.entries.maxByOrNull { it.value }!!.key
        print(c)
        i++
    }
    println()
}

private fun parse(): MutableMap<Int, MutableMap<Char, Int>> {
    val countMap = mutableMapOf<Int, MutableMap<Char, Int>>()
    parseLines("/2016/day6.txt") { line ->
        for ((i, c) in line.withIndex()) {
            val m = countMap.getOrPut(i) { mutableMapOf() }
            m[c] = (m[c] ?: 0) + 1
        }
    }
    return countMap
}

private fun part2() {
    val countMap = parse()
    var i = 0
    while (true) {
        val m = countMap[i] ?: break
        val c = m.entries.minByOrNull { it.value }!!.key
        print(c)
        i++
    }
    println()
}

fun main() {
    part1()
    part2()
}
