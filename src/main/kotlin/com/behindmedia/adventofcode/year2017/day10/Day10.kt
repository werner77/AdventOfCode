package com.behindmedia.adventofcode.year2017.day10

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun knotHash(input: String) : String {
    val densHash = knotHashBinary(input)
    return hexString(densHash)
}

fun knotHashBinary(input: String): List<Int> {
    val data = input.toIntList()
    val elements = process(data, 64)
    return densHash(elements)
}

fun process(lengths: List<Int>, iterationCount: Int): List<Int> {
    val elements = (0 until 256).toMutableList()
    var currentPosition = 0
    var skipSize = 0
    (0 until iterationCount).forEach { _ ->
        for (length in lengths) {
            for (i in 0 until length / 2) {
                val m = (currentPosition + i) % elements.size
                val n = (currentPosition + length - 1 - i) % elements.size
                elements[m] = elements[n].also { elements[n] = elements[m] }
            }
            currentPosition += length + skipSize
            skipSize++
            currentPosition %= elements.size
        }
    }
    return elements
}

private fun String.toIntList(): List<Int> {
    val result = mutableListOf<Int>()
    for (c in this) {
        result += c.code
    }
    result += listOf(17, 31, 73, 47, 23)
    return result
}

fun part1(input: String) {
    val data = input.split(",").map { it.toInt() }
    val elements = process(data, 1)
    println(elements[0] * elements[1])
}

fun part2(input: String) {
    println(knotHash(input))
}

private fun hexString(densHash: List<Int>): String {
    return densHash.fold(StringBuilder()) { builder, value ->
        builder.apply {
            append(value.toString(16).padStart(2, '0'))
        }
    }.toString()
}

private fun densHash(elements: List<Int>): MutableList<Int> {
    val densHash = mutableListOf<Int>()
    for (i in 0 until 16) {
        var result = 0
        for (j in 0 until 16) {
            result = result xor elements[i * 16 + j]
        }
        densHash += result
    }
    return densHash
}

fun main() {
    val input = read("/2017/day10.txt").trim()
    part1(input)
    part2(input)
}