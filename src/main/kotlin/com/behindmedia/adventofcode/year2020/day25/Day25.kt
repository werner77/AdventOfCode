package com.behindmedia.adventofcode.year2020.day25

import com.behindmedia.adventofcode.common.*

private fun transform(loopSize: Long, subject: Long): Long {
    var value = 1L
    repeat(loopSize.toInt()) {
        value = process(value, subject)
    }
    return value
}

private fun process(value: Long, subject: Long): Long {
    return (value * subject) % 20201227L
}

private fun findLoopSize(publicKey: Long): Long {
    var loopSize = 0L
    var value = 1L
    while(true) {
        if (value == publicKey) {
            return loopSize
        }
        value = process(value, 7)
        loopSize++
    }
}

fun main() {
    val (key1, key2) = read("/2020/day25.txt").split("\n").filter { it.isNotEmpty() }.map { it.toLong() }
    val loopSize1 = findLoopSize(key1)
    println(transform(loopSize1, key2))
}