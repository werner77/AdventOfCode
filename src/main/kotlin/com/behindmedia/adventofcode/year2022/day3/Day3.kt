package com.behindmedia.adventofcode.year2022.day3

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2022/day3.txt") { line ->
        line.map { it.priority }
    }
    part1(data)
    part2(data)
}

private fun part2(data: List<List<Int>>) {
    var k = 0
    var ans = 0
    while (k < data.size) {
        val sets = (0 until 3).fold(mutableListOf<Set<Int>>()) { list, _ ->
            list.apply {
                add(data[k++].toSet())
            }
        }
        val intersection = sets[0].intersect(sets[1]).intersect(sets[2])
        require(intersection.size == 1)
        ans += intersection.first()
    }
    println(ans)
}

private fun part1(data: List<List<Int>>) {
    var ans = 0
    for (d in data) {
        val set1 = (0 until d.size / 2).map { d[it] }.toSet()
        val set2 = (d.size / 2 until d.size).map { d[it] }.toSet()
        val intersection = set1.intersect(set2)
        require(intersection.size == 1)
        ans += intersection.first()
    }
    println(ans)
}

private val Char.priority: Int
    get() = if (this.isLowerCase()) this - 'a' + 1 else this - 'A' + 27