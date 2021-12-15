package com.behindmedia.adventofcode.year2017.day14

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2017.day10.knotHashBinary

fun main() {
    val input = read("/2017/day14.txt").trim()
    var bitCount = 0
    val map = mutableSetOf<Coordinate>()
    for (y in 0 until 128) {
        val hashBytes = knotHashBinary("${input}-$y")
        for ((j, b) in hashBytes.withIndex()) {
            for (i in 0 until 8) {
                val mask = 1 shl 7 - i
                if (b and mask == mask) {
                    map += Coordinate(j * 8 + i, y)
                    bitCount++
                }
            }
        }
    }

    // Part 1
    println(bitCount)

    // Part 2
    println(countRegions(map))
}

private fun countRegions(map: MutableSet<Coordinate>): Int {
    val seen = mutableSetOf<Coordinate>()
    val pending = mutableListOf<Coordinate>()
    var regions = 0
    for (c in map) {
        if (c in seen) continue
        seen += c
        pending += c
        while (pending.isNotEmpty()) {
            val current = pending.removeLast()
            for (n in current.directNeighbours) {
                if (!map.contains(n) || seen.contains(n)) continue
                pending.add(n)
                seen.add(n)
            }
        }
        regions++
    }
    return regions
}