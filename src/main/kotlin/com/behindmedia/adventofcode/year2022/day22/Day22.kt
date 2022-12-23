package com.behindmedia.adventofcode.year2022.day22

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class State(var coordinate: Coordinate, var direction: Coordinate)
data class Direction(val amount: Int, val rotation: RotationDirection?)

fun main() {
    val data = read("/2022/day22.txt")
    val (mapString, directionString) = data.split("\n\n")
    val map = parseMapFromString(mapString) {
        it
    }
    val directions = parseDirections(directionString)
    part1(map, directions)
    part2(map, directions)
}

private fun parseDirections(directionString: String): List<Direction> {
    val directions = mutableListOf<Direction>()
    var i = 0
    while (i < directionString.length) {
        var amount = 0
        while (i < directionString.length) {
            val k = directionString[i] - '0'
            if (k > 10) break
            amount *= 10
            amount += k
            i++
        }
        require(amount > 0)
        val rotation: RotationDirection? = if (i < directionString.length) {
            val d = directionString[i++]
            when (d) {
                'L' -> RotationDirection.Left
                'R' -> RotationDirection.Right
                else -> error(
                    "Invalid direction: ${directionString[i]}"
                )
            }
        } else {
            null
        }
        directions += Direction(amount, rotation)
    }
    return directions
}

private fun part1(
    map: Map<Coordinate, Char>,
    directions: List<Direction>
) {
    val xBoundaries = mutableMapOf<Int, Pair<Int, Int>>()
    val yBoundaries = mutableMapOf<Int, Pair<Int, Int>>()
    for ((c, v) in map.entries) {
        if (v != ' ') {
            yBoundaries[c.x] =
                (yBoundaries[c.x] ?: Pair(Int.MAX_VALUE, Int.MIN_VALUE)).let {
                    Pair(min(it.first, c.y), max(it.second, c.y))
                }
            xBoundaries[c.y] =
                (xBoundaries[c.y] ?: Pair(Int.MAX_VALUE, Int.MIN_VALUE)).let {
                    Pair(min(it.first, c.x), max(it.second, c.x))
                }
        }
    }
    solve(map, directions) { coordinate, direction ->
        when (direction) {
            Coordinate.up -> Coordinate(coordinate.x, yBoundaries[coordinate.x]!!.second)
            Coordinate.down -> Coordinate(coordinate.x, yBoundaries[coordinate.x]!!.first)
            Coordinate.left -> Coordinate(xBoundaries[coordinate.y]!!.second, coordinate.y)
            Coordinate.right -> Coordinate(xBoundaries[coordinate.y]!!.first, coordinate.y)
            else -> error("Invalid direction")
        }
    }
}

private fun part2(
    map: Map<Coordinate, Char>,
    directions: List<Direction>
) {
    val size = 50

    val tests = listOf<Triple<Coordinate, Coordinate, Coordinate>>(
        Triple(Coordinate(size * 3 - 1, 5), Coordinate.right, Coordinate.right),
        Triple(Coordinate(size * 2 - 1, size + 5), Coordinate.right, Coordinate.down),
        Triple(Coordinate(size * 2 - 1, size * 2 + 5), Coordinate.right, Coordinate.right),
        Triple(Coordinate(size - 1, size * 3 + 5), Coordinate.right, Coordinate.down),
        Triple(Coordinate(size + 5, 0), Coordinate.up, Coordinate.left),
        Triple(Coordinate(size * 2 + 5, 0), Coordinate.up, Coordinate.down),
        Triple(Coordinate(5, size * 2), Coordinate.up, Coordinate.left),
        Triple(Coordinate(size, 5), Coordinate.left, Coordinate.left),
        Triple(Coordinate(size, size + 5), Coordinate.left, Coordinate.up),
        Triple(Coordinate(0, size * 2 + 5), Coordinate.left, Coordinate.left),
        Triple(Coordinate(0, size * 3 + 5), Coordinate.left, Coordinate.up),
        Triple(Coordinate(size * 2 + 5, size - 1), Coordinate.down, Coordinate.right),
        Triple(Coordinate(size + 5, size * 3 - 1), Coordinate.down, Coordinate.right),
        Triple(Coordinate(5, size * 4 - 1), Coordinate.down, Coordinate.up),
    )

    tests.forEach {
        testDirectionWrapping(it, size)
    }

    // Size of the cube == 50
    // Moving left/right off the map
//    solve(map, directions) { coordinate, direction ->
//        getNextWrappedCoordinate(coordinate, size, direction)
//    }
}

private fun getNextWrappedCoordinate(
    coordinate: Coordinate,
    size: Int,
    direction: Coordinate
): Coordinate {
    val quadrant = getQuadrant(coordinate, size)
    return when (direction) {
        Coordinate.up -> when (quadrant) {
            1 -> Coordinate(0, 3 * size + (coordinate.x - size))
            2 -> Coordinate(size - 1 - (coordinate.x - 2 * size), 4 * size - 1)
            5 -> Coordinate(size + (coordinate.y - 2 * size), size + coordinate.x)
            else -> error("Invalid")
        }

        Coordinate.down -> when (quadrant) {
            2 -> Coordinate(2 * size - 1, size + (coordinate.x - 2 * size))
            4 -> Coordinate(size - 1, 3 * size + (coordinate.x - size))
            6 -> Coordinate(size * 3 - 1 - coordinate.x , 0) // y component could be inverted, check
            else -> error("Invalid")
        }

        Coordinate.left -> when (quadrant) {
            1 -> Coordinate(0, size * 3 - 1 - (coordinate.y))
            3 -> Coordinate(coordinate.y - size, 2 * size)
            5 -> Coordinate(size * 2 - 1 - (coordinate.y - 2 * size), 0)
            6 -> Coordinate(size + (coordinate.y - 3 * size), 0)
            else -> error("Invalid")
        }

        Coordinate.right -> when (quadrant) {
            2 -> Coordinate(size * 2 - 1, size * 3 - 1 - (coordinate.y))
            3 -> Coordinate(2 * size + (coordinate.y - size), size - 1)
            4 -> Coordinate(3 * size - 1, size - 1 - (coordinate.y - 2 * size))
            6 -> Coordinate(size + (coordinate.y - 3 * size), 3 * size - 1)
            else -> error("Invalid")
        }

        else -> error("Invalid direction: $direction")
    }
}

private fun getQuadrant(coordinate: Coordinate, size: Int): Int {
    when (coordinate.x) {
        in 0 until size -> {
            when (coordinate.y / size) {
                2 -> return 5
                3 -> return 6
            }
        }

        in size until size * 2 -> {
            when (coordinate.y / size) {
                0 -> return 1
                1 -> return 3
                2 -> return 4
            }
        }

        in 2 * size until 3 * size -> {
            when (coordinate.y / size) {
                0 -> return 2
            }
        }
    }
    error("Invalid coordinate: $coordinate")
}

private fun solve(
    map: Map<Coordinate, Char>,
    directions: List<Direction>,
    wrapAround: (Coordinate, Coordinate) -> Coordinate
) {
    val start = map.entries.first { it.value == '.' }.key
    val state = State(start, Coordinate.right)
    for (direction in directions) {
        for (k in 0 until direction.amount) {
            var c = state.coordinate + state.direction
            var h = map[c]
            if (h == null || h == ' ') {
                c = wrapAround(c, state.direction)
                h = map[c]
            }
            if (h == '#') {
                // Wall, just stop
                break
            } else if (h == '.') {
                // Move
                state.coordinate = c
            }
        }
        direction.rotation?.let {
            state.direction = state.direction.rotate(it)
        }
    }
    val directionValue = when (state.direction) {
        Coordinate.right -> 0
        Coordinate.down -> 1
        Coordinate.left -> 2
        Coordinate.up -> 3
        else -> error("Invalid direction")
    }

    println("Final coordinate: ${state.coordinate}")
    println("Final direction: ${state.direction}")

    val ans = 4 * (state.coordinate.x + 1) + 1000 * (state.coordinate.y + 1) + directionValue
    println(ans)
}

private fun getCoordinateMap(size: Int): Map<Coordinate, Coordinate3D> {
    val map = mutableMapOf<Coordinate, Coordinate3D>()
    var c = Coordinate3D(0, 0, 0)
    var xDirection = Coordinate3D(1, 0, 0)
    var yDirection = Coordinate3D(0, 1, 0)
    for (j in 0 until 4) {
        for (k in 0 until size) {
            val y = j * size + k
            for (i in 0 until 4) {
                for (l in 0 until size) {
                    val x = i * size + l
                    map[Coordinate(x, y)] = c
                    c += xDirection
                }
                xDirection = Coordinate3D(-xDirection.z, xDirection.y, xDirection.x)
            }
            c += yDirection
        }
        yDirection = Coordinate3D(yDirection.x, -yDirection.z, yDirection.y)
    }
    return map
}

private fun testDirectionWrapping(testCase: Triple<Coordinate, Coordinate, Coordinate>, size: Int) {
    val (c, d1, d2) = testCase
    println("----------------------------------------------------")
    println("Testing coordinate: $c with direction $d1")
    val q1 = getQuadrant(c, size)
    println("Quadrant: $q1")
    val next = getNextWrappedCoordinate(c, size, d1)

    val q2 = getQuadrant(next, size)
    println("Got wrapped coordinate: $next")
    println("Quadrant: $q2")

    val c1 = getNextWrappedCoordinate(next, size, d2)

    if (c != c1) {
        throw AssertionError("Failed test case for coordinate: $c and direction $d1 which was mapped to coordinate: $next but back to coordinate: $c1")
    }
    println("----------------------------------------------------")
    println("")
}