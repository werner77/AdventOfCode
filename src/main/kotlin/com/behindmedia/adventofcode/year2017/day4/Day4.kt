package com.behindmedia.adventofcode.year2017.day4

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val data = parseLines("/2017/day4.txt") { line ->
        line.split(" ")
    }
    part1(data)
    part2(data)
}

private fun String.isAnagram(other: String): Boolean {
    return this.toSet() == other.toSet()
}

private fun part1(data: List<List<String>>) {
    println(data.count { it.toSet().size == it.size })
}

private fun part2(data: List<List<String>>) {
    println(data.count {
        var foundAnagram = false
        for (i in it.indices) {
            for (j in i + 1 until it.size) {
                val a = it[i]
                val b = it[j]
                if (a.isAnagram(b)) foundAnagram = true
            }
        }
        !foundAnagram
    })
}