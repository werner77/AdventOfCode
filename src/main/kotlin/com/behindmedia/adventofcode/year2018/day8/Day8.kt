package com.behindmedia.adventofcode.year2018.day8

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2018/day8.txt") { line ->
        line.split(" ").map { it.toInt() }
    }.first()
    println(parse1(data, 0).second)
    println(parse2(data, 0).second)
}

private fun parse1(data: List<Int>, index: Int): Pair<Int, Int> {
    val childNodeCount = data[index]
    val metaDataCount = data[index + 1]
    var sum = 0
    var i = index + 2
    repeat(childNodeCount) {
        val (k, s) = parse1(data, i)
        i = k
        sum += s
    }
    repeat(metaDataCount) {
        sum += data[i++]
    }
    return Pair(i, sum)
}

private fun parse2(data: List<Int>, index: Int): Pair<Int, Int> {
    val childNodeCount = data[index]
    val metaDataCount = data[index + 1]
    var i = index + 2
    val childValues = IntArray(childNodeCount)
    repeat(childNodeCount) {
        val (k, s) = parse2(data, i)
        i = k
        childValues[it] = s
    }
    var sum = 0
    repeat(metaDataCount) {
        val value = data[i++]
        sum += if (childNodeCount == 0) {
            value
        } else {
            childValues.getOrNull(value - 1) ?: 0
        }
    }
    return Pair(i, sum)
}