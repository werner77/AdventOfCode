package com.behindmedia.adventofcode.year2024.day22

import com.behindmedia.adventofcode.common.*

fun main() = timing {
    val data = parseLines("/2024/day22.txt") { line ->
        line.toLong()
    }
    println(part1(data))
    println(part2(data))
}

private fun part1(inputs: List<Long>, times: Int = 2000): Long {
    var result = 0L
    for (input in inputs) {
        var current = input
        repeat(times) {
            current = evolve(current)
        }
        result += current
    }
    return result
}

private fun part2(inputs: List<Long>, times: Int = 2000): Long {
    val totalPrices = defaultMutableMapOf<List<Long>, Long> { 0L }
    for (input in inputs) {
        val sequence = ArrayDeque<Long>()
        val prices = mutableMapOf<List<Long>, Long>()
        var current = input
        repeat(times) {
            val lastPrice = current % 10
            current = evolve(current)
            val price = current % 10
            val change = price - lastPrice
            sequence.addLast(change)
            if (sequence.size > 4) {
                sequence.removeFirst()
            }
            if (sequence.size == 4) {
                if (sequence !in prices) {
                    val sequenceCopy = sequence.toList()
                    prices[sequenceCopy] = price
                    totalPrices[sequenceCopy] += price
                }
            }
        }
    }
    return totalPrices.maxOf { it.value }
}

/**
 * Calculate the result of multiplying the secret number by 64. Then, mix this result into the secret number. Finally, prune the secret number.
 * Calculate the result of dividing the secret number by 32. Round the result down to the nearest integer. Then, mix this result into the secret number. Finally, prune the secret number.
 * Calculate the result of multiplying the secret number by 2048. Then, mix this result into the secret number. Finally, prune the secret number.
 */
private fun evolve(secret: Long): Long {
    var result = secret xor (secret * 64L)
    result %= 16777216L
    result = (result / 32) xor result
    result %= 16777216L
    result = (result * 2048L) xor result
    result %= 16777216L
    return result
}