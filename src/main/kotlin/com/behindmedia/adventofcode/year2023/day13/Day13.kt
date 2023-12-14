package com.behindmedia.adventofcode.year2023.day13

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = read("/2023/day13.txt")
    val sections = data.split("\n\n")
    val maps = sections.map {
        CharGrid(it)
    }

    // Part 1
    println(maps.sumOf { findReflections(it, true, 0).sum() + findReflections(it, false, 0).sum() * 100 })

    // Part 2
    println(maps.sumOf { findReflections(it, true, 1).sum() + findReflections(it, false, 1).sum() * 100 })
}

private fun findReflections(map: CharGrid, transposed: Boolean, wantedDiffCount: Int): List<Int> {
    val sizeA = if (transposed) map.sizeX else map.sizeY
    val sizeB = if (transposed) map.sizeY else map.sizeX
    val result = mutableListOf<Int>()
    for (a in 1 ..< sizeA) {
        var delta = 0
        var diffCount = 0
        while (true) {
            val a1 = a - delta - 1
            val a2 = a + delta
            if (a1 < 0 || a2 >= sizeA) break
            for (b in 0..< sizeB) {
                val c1 = if (transposed) Coordinate(a1, b) else Coordinate(b, a1)
                val c2 = if (transposed) Coordinate(a2, b) else Coordinate(b, a2)
                if (map[c1] != map[c2]) {
                    diffCount++
                }
            }
            delta++
        }
        if (diffCount == wantedDiffCount) {
            result += a
        }
    }
    return result
}
