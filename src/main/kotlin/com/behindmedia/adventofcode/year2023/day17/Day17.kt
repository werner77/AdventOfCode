package com.behindmedia.adventofcode.year2023.day17

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.RotationDirection.Left
import com.behindmedia.adventofcode.common.RotationDirection.Right
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.shortestWeightedPath
import com.behindmedia.adventofcode.common.timing

fun main() {
    val grid = CharGrid(read("/2023/day17.txt"))
    timing {
        // Part 1
        println(process(grid) { direction, count ->
            if (count < 3) {
                // Continue in same direction
                listOf(direction.rotate(Left), direction.rotate(Right), direction)
            } else {
                listOf(direction.rotate(Left), direction.rotate(Right))
            }
        })
    }

    timing {
        // Part 2
        println(process(grid) { direction, count ->
            if (count < 4) {
                // Continue in same direction
                listOf(direction)
            } else if (count >= 10) {
                listOf(direction.rotate(Left), direction.rotate(Right))
            } else {
                // Turn left or right
                listOf(direction.rotate(Left), direction.rotate(Right), direction)
            }
        })
    }
}

private fun process(data: CharGrid, directions: (Coordinate, Int) -> List<Coordinate>): Int {
    val source = Coordinate.origin
    val target = Coordinate(data.maxX, data.maxY)
    return shortestWeightedPath(from = Block(source, null, 0),
        neighbours = { current ->
            val directionCount = current.directionCount
            val currentDirection = current.direction
            val coordinates = if (directionCount == 0 || currentDirection == null) {
                current.position.directNeighbours
            } else {
                directions(currentDirection, directionCount)
            }
            coordinates.mapNotNull { direction ->
                val coordinate = current.position + direction
                val value = data.getOrNull(coordinate)
                if (value != null) {
                    Block(
                        coordinate,
                        direction,
                        if (direction == currentDirection) directionCount + 1 else 1
                    ) to value.digitToInt()
                } else {
                    null
                }
            }
        },
        minLengthToTarget = {
            // A* heuristic
            it.position.manhattenDistance(target)
        },
        process = { path ->
            if (path.destination.position == target) path.length else null
        }) ?: error("could not find path")
}

private data class Block(val position: Coordinate, val direction: Coordinate?, val directionCount: Int)
