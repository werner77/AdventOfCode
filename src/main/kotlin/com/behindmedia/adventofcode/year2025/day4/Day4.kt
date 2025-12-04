package com.behindmedia.adventofcode.year2025.day4

import com.behindmedia.adventofcode.common.*
private fun solve(inputGrid: CharGrid, recursive: Boolean): Int {
    val grid = inputGrid.mutableCopy()
    var ans = 0
    val toRemove = mutableSetOf<Coordinate>()
    while (true) {
        for (c in grid.coordinateRange) {
            var counter = 0
            if (grid[c] != '@') continue
            for (c1 in c.allNeighbours) {
                if (grid.coordinateRange.contains(c1)) {
                    if (grid[c1] == '@') {
                        counter++
                    }
                }
            }
            if (counter < 4) {
                ans++
                toRemove += c
            }
        }
        if (!recursive || toRemove.isEmpty()) {
            break
        }
        for (c in toRemove) {
            grid[c] = '.'
        }
        toRemove.clear()
    }
    return ans
}

fun main() {
    val data = read("/2025/day4.txt")
    val grid = CharGrid.invoke(string = data, '.')
    for (recursive in listOf(false, true)) {
        println(solve(grid, recursive))
    }
}