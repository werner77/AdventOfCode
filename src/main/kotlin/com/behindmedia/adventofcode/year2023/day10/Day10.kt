package com.behindmedia.adventofcode.year2023.day10

import com.behindmedia.adventofcode.common.CharGrid
import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.Path
import com.behindmedia.adventofcode.common.insidePointCount
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing
import kotlin.math.roundToInt
import kotlin.math.sign

fun main() {
    val grid = CharGrid(read("/2023/day10.txt"))
    val startCoordinate = grid.single { it.value == 'S' }.key
    val path = findEnclosingPath(startCoordinate, grid)

    timing {
        // Part 1
        // The path does not contain the start, so we need to add 1
        println((1 + path.length) / 2)

        // Part 2
        println(countEnclosed(map = grid, enclosingPath = path))

        // Part 2, simple :-), using the Shoelace formula and Picks theorem
        println(path.allNodes.map { it.position }.insidePointCount())
    }
}

/**
 * The idea I came up with is the following:
 *
 * - First ensure that the 'S' node is replaced with the proper pipe connection
 * - Determine the rotation sign of the enclosing path. The full circle makes a total rotation of either + or - 2 * PI.
 * - All regions that are inside this enclosing path at some point have a bordering coordinate which makes a perpendicular angle with the enclosing path (+/- PI / 2)
 * - The sign of this angle should be the opposite to the sign of the full revolution angle (see above) that the enclosing path makes. If this is true, then the region is inside, otherwise the region is outside.
 * - We count the sizes of the visited locations within valid enclosed sections and add them all up together to get to the result.
 */
private fun countEnclosed(map: CharGrid, enclosingPath: Path<PositionDirectionTuple>): Int {
    // Keeps track of all seen coordinates
    val seen = mutableSetOf<Coordinate>()

    val pathNodes = enclosingPath.allNodes

    val firstNode = pathNodes.first()
    val lastNode = pathNodes.last()

    val actualStartValue = "|-LJ7F".single { it.nextDirection(lastNode.direction) == firstNode.direction }
    val replacedMap: CharGrid = map.mutableCopy().apply { set(firstNode.position, actualStartValue) }

    // Calculate the complete rotation angle: this determines what the vector angle should be for internal regions
    var lastDirection = lastNode.direction
    val rotation = pathNodes.fold(0.0) { value, (_, direction) ->
        value + direction.normalizedAngle(lastDirection).also { lastDirection = direction }
    }
    val enclosingPathMap = pathNodes.associateBy { it.position }

    // All points that are not on the enclosingPath
    val candidates = map.keys.filter { it !in enclosingPathMap }

    // The count of valid points
    var totalCount = 0

    val requiredSign = -rotation.sign.roundToInt()

    for (from in candidates) {
        // If already in one of the processed regions, just continue
        if (from in seen) continue
        val (visited, valid) = checkValidRegion(from, replacedMap, enclosingPathMap, requiredSign)
        if (valid) {
            // If valid add the total size of this region to the inside tiles
            totalCount += visited.size
        }
        // Ensure we don't handle any nodes twice
        seen += visited
    }
    return totalCount
}

private fun checkValidRegion(
    from: Coordinate,
    grid: CharGrid,
    enclosingPath: Map<Coordinate, PositionDirectionTuple>,
    requiredSign: Int
): Pair<Set<Coordinate>, Boolean> {
    val visited = mutableSetOf<Coordinate>()
    val pending = ArrayDeque<Path<Coordinate>>()
    pending += Path(from, 0, null)

    // If one invalid coordinate is found, the whole region is invalid
    var valid = true
    while (true) {
        val path = pending.removeFirstOrNull() ?: break

        if (path.destination in visited) continue

        visited += path.destination

        for (neighbour in path.destination.directNeighbours) {
            if (grid.getOrNull(neighbour) == null) {
                // Coordinate is next to border, mark region as invalid
                valid = false
                continue
            }

            // Already visited: ignore
            if (neighbour in visited) continue

            val enclosing = enclosingPath[neighbour]
            if (enclosing != null) {
                // The neighbour is on the border: determine the sign of the perpendicular angle
                val direction1 = neighbour - path.destination
                var direction2 = enclosing.direction

                if (direction1 == direction2) {
                    // For cornering connections we have to take the previous direction if the directions are aligned
                    val value = grid[enclosing.position]
                    direction2 = value.previousDirection(direction2)
                        ?: error("Could not get previous direction for direction: $direction2")
                }

                // Calculate the angle between the two directions
                val angle = direction1.normalizedAngle(direction2)

                // Sign of the angle should be the same for all directions to be a valid region
                if (requiredSign != angle.sign.roundToInt()) {
                    valid = false
                }
            } else {
                pending += Path(neighbour, path.length + 1, path)
            }
        }
    }
    return Pair(visited, valid)
}

private fun findEnclosingPath(
    from: Coordinate,
    grid: CharGrid
): Path<PositionDirectionTuple> {
    val pending = ArrayDeque<Path<PositionDirectionTuple>>()
    val visited = mutableSetOf<PositionDirectionTuple>()
    // Start exploring in all 4 directions from the start coordinate
    for (direction in Coordinate.directNeighbourDirections) {
        pending += Path(PositionDirectionTuple(from, direction), 0, null)
    }
    while (true) {
        // No paths to explore anymore: break
        val path = pending.removeFirstOrNull() ?: break
        val (position, direction) = path.destination
        visited += path.destination
        val neighbour = position + direction
        if (neighbour == from) {
            return path
        }
        val nextValue = grid.getOrNull(neighbour) ?: continue
        val nextDirection = nextValue.nextDirection(direction = direction) ?: continue
        val nextPositionDirection = PositionDirectionTuple(neighbour, nextDirection)
        if (nextPositionDirection in visited) continue
        val nextPath = Path(nextPositionDirection, path.length + 1, path)
        pending += nextPath
    }
    error("Could not find path")
}

// Pair of position and direction
data class PositionDirectionTuple(val position: Coordinate, val direction: Coordinate)

// Returns the direction which is valid after entering with the specified direction
private fun Char.nextDirection(direction: Coordinate): Coordinate? {
    return when (this) {
        '|' -> if (direction.isVertical) direction else null
        '-' -> if (direction.isHorizontal) direction else null
        'L' -> if (direction == Coordinate.down) Coordinate.right else if (direction == Coordinate.left) Coordinate.up else null
        'J' -> if (direction == Coordinate.down) Coordinate.left else if (direction == Coordinate.right) Coordinate.up else null
        '7' -> if (direction == Coordinate.right) Coordinate.down else if (direction == Coordinate.up) Coordinate.left else null
        'F' -> if (direction == Coordinate.up) Coordinate.right else if (direction == Coordinate.left) Coordinate.down else null
        '.', 'S' -> null
        else -> error("Unrecognized value: $this")
    }
}

// Returns the direction which was valid before the specified direction
private fun Char.previousDirection(direction: Coordinate): Coordinate? {
    return when (this) {
        '|' -> if (direction.isVertical) direction else null
        '-' -> if (direction.isHorizontal) direction else null
        'L' -> if (direction == Coordinate.right) Coordinate.down else if (direction == Coordinate.up) Coordinate.left else null
        'J' -> if (direction == Coordinate.up) Coordinate.right else if (direction == Coordinate.left) Coordinate.down else null
        '7' -> if (direction == Coordinate.left) Coordinate.up else if (direction == Coordinate.down) Coordinate.right else null
        'F' -> if (direction == Coordinate.right) Coordinate.up else if (direction == Coordinate.down) Coordinate.left else null
        '.', 'S' -> null
        else -> error("Unrecognized value: $this")
    }
}
