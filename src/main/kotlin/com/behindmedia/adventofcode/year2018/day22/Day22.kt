package com.behindmedia.adventofcode.year2018.day22

import com.behindmedia.adventofcode.common.*
import java.util.PriorityQueue

private val Long.riskLevel: Long
    get() = this % 3L

fun main() {
    val data = parseLines("/2018/day22.txt") { line ->
        line
    }
    val depth = data[0].substringAfter("depth: ").toLong()
    val (x, y) = data[1].substringAfter("target: ").split(",").map { it.toInt() }
    val target = Coordinate(x, y)

    timing {
        part1(target, depth)
    }
    timing {
        part2(target, depth)
    }
}

private fun MutableMap<Coordinate, Long>.getErosionLevel(coordinate: Coordinate, target: Coordinate, depth: Long) : Long {
    return this.getOrPut(coordinate) {
        val geoIndex = when {
            coordinate == Coordinate.origin -> 0L
            coordinate == target -> 0L
            coordinate.y == 0 -> coordinate.x * 16807L
            coordinate.x == 0 -> coordinate.y * 48271L
            else -> {
                getErosionLevel(coordinate + Coordinate.left, target, depth) *
                        getErosionLevel(coordinate + Coordinate.up, target, depth)
            }
        }
        (geoIndex + depth) % 20183L
    }
}

private fun part1(target: Coordinate, depth: Long) {
    val map = mutableMapOf<Coordinate, Long>()
    var ans = 0L
    for (y in 0..target.y) {
        for (x in 0..target.x) {
            val c = Coordinate(x, y)
            ans += map.getErosionLevel(c, target, depth).riskLevel
        }
    }
    println(ans)
}

private enum class Tool {
    Torch,
    ClimbingGear,
    Neither
}

private fun isAllowedTool(tool: Tool, erosionLevel: Long) = tool in getAllowedTools(erosionLevel)

private fun getAllowedTools(erosionLevel: Long): List<Tool> {
    return when (erosionLevel % 3L) {
        0L -> listOf(Tool.Torch, Tool.ClimbingGear)
        1L -> listOf(Tool.ClimbingGear, Tool.Neither)
        2L -> listOf(Tool.Torch, Tool.Neither)
        else -> error("Unknown erosion level $erosionLevel")
    }
}

private fun part2(target: Coordinate, depth: Long) {
    val map = mutableMapOf<Coordinate, Long>()
    val pending = PriorityQueue<Path<Pair<Coordinate, Tool>>>()
    val seen = mutableSetOf<Pair<Coordinate, Tool>>()
    pending.add(Path(Pair(Coordinate.origin, Tool.Torch), 0, null))
    while (pending.isNotEmpty()) {
        val currentPath = pending.poll()

        if (currentPath.destination in seen) {
            continue
        }
        seen += currentPath.destination

        val (currentCoordinate, currentTool) = currentPath.destination
        val currentLength = currentPath.pathLength
        val currentErosionLevel = map.getErosionLevel(currentCoordinate, target, depth)

        if (currentCoordinate == target) {
            if (currentTool == Tool.Torch) {
                println(currentLength)
                return
            } else {
                // Switch to torch
                pending.add(Path(Pair(currentCoordinate, Tool.Torch), currentLength + 7, currentPath))
                continue
            }
        }

        for (neighbour in currentCoordinate.directNeighbours) {
            // Check whether it is accessible
            if (neighbour.x < 0 || neighbour.y < 0) {
                continue
            }
            val erosionLevel = map.getErosionLevel(neighbour, target, depth)
            for (tool in getAllowedTools(erosionLevel)) {
                if (tool == currentTool) {
                    // No need to change tool
                    pending.add(Path(Pair(neighbour, tool), currentLength + 1, currentPath))
                } else if (isAllowedTool(tool, currentErosionLevel)) {
                    pending.add(Path(Pair(neighbour, tool), currentLength + 8, currentPath))
                }
            }
        }
    }
    error("No path found")
}