package com.behindmedia.adventofcode.year2024.day22

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.timing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

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

@JvmInline
private value class PriceSequence(val value: Int) {
    operator fun plus(price: Int): PriceSequence {
        val newValue = (value shl 8) or (price and 0xFF)
        return PriceSequence(newValue)
    }
}

private fun part2(inputs: List<Long>, times: Int = 2000): Int {
    val totalPrices = ConcurrentHashMap<PriceSequence, Int>()
    runBlocking {
        for (input in inputs) {
            launch(Dispatchers.Default) {
                var sequence = PriceSequence(0)
                val prices = mutableMapOf<PriceSequence, Int>()
                var current = input
                var lastPrice = (current % 10).toInt()
                repeat(times) { i ->
                    current = evolve(current)
                    val price = (current % 10).toInt()
                    val change = price - lastPrice
                    sequence += change
                    if (i >= 3) {
                        if (sequence !in prices) {
                            prices[sequence] = price
                            totalPrices.compute(sequence) { _, value -> (value ?: 0) + price }
                        }
                    }
                    lastPrice = price
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