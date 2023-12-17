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
    return shortestWeightedPath(from = Node(Coordinate.origin, null, 0),
        neighbours = { path ->
            val directionCount = path.destination.directionCount
            val currentDirection = path.destination.direction
            val coordinates = if (directionCount == 0 || currentDirection == null) {
                path.destination.position.directNeighbours
            } else {
                directions(currentDirection, directionCount)
            }
            coordinates.mapNotNull { direction ->
                val coordinate = path.destination.position + direction
                val value = data.getOrNull(coordinate)
                if (value != null) {
                    Node(
                        coordinate,
                        direction,
                        if (direction == currentDirection) directionCount + 1 else 1
                    ) to value.digitToInt()
                } else {
                    null
                }
            }
        },
        process = { path ->
            if (path.destination.position == Coordinate(data.maxX, data.maxY)) path.pathLength else null
        }) ?: error("could not find path")
}

data class Node(val position: Coordinate, val direction: Coordinate?, val directionCount: Int)
