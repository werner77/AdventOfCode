package com.behindmedia.adventofcode.year2024.day25

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class Schema(val size: Int, val items: List<Int>)

fun main() {
    val data = read("/2024/day25.txt").splitTrimmed("\n\n")
    val locks = mutableListOf<Schema>()
    val keys = mutableListOf<Schema>()
    for (d in data) {
        val grid = CharGrid(d)
        val isLock = (0 until grid.sizeX).map { grid[Coordinate(it, 0)] }.all { it == '#' }
        if (!isLock) {
            require((0 until grid.sizeX).map { grid[Coordinate(it, grid.maxY)] }.all { it == '#' })
        }
        if (isLock) {
            val countMap = mutableMapOf<Int, Int>()
            for (x in 0 until grid.sizeX) {
                if (countMap[x] == null) countMap[x] = 0
                for (y in 1 until grid.sizeY) {
                    if (grid[Coordinate(x, y)] == '#') {
                        countMap[x] = y
                    } else {
                        break
                    }
                }
            }
            locks.add(Schema(grid.sizeY - 2, countMap.entries.sortedBy { it.key }.map { it.value }.toList()))
        } else {
            val countMap = mutableMapOf<Int, Int>()
            for (x in 0 until grid.sizeX) {
                if (countMap[x] == null) countMap[x] = 0
                for (y in grid.maxY downTo 1) {
                    if (grid[Coordinate(x, y)] == '#') {
                        countMap[x] = grid.maxY - y
                    } else {
                        break
                    }
                }
            }
            keys.add(Schema(grid.sizeY - 2, countMap.entries.sortedBy { it.key }.map { it.value }.toList()))
        }
    }
    var count = 0
    for (lock in locks) {
        for (key in keys) {
            if (fit(key, lock)) {
                count++
            }
        }
    }
    println(count)
}

private fun fit(key: Schema, lock: Schema): Boolean {
    val count = max(key.items.size, lock.items.size)
    val size = min(key.size, lock.size)
    require(key.size == lock.size)
    require(key.items.size == lock.items.size)
    return (0 until count).all { key.items[it]  + lock.items[it] <= size }
}