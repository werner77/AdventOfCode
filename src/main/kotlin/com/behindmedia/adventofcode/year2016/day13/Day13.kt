package com.behindmedia.adventofcode.year2016.day13

import com.behindmedia.adventofcode.common.*

private const val INPUT = 1350

private fun bitCount(n: Int): Int {
    var sum = 0
    n.toLong().forBits(0 until 32) {
        if (it) sum++
    }
    return sum
}

/*
Find x*x + 3*x + 2*x*y + y + y*y.
Add the office designer's favorite number (your puzzle input).
Find the binary representation of that sum; count the number of bits that are 1.
If the number of bits that are 1 is even, it's an open space.
If the number of bits that are 1 is odd, it's a wall.
 */
private fun isAccessible(coordinate: Coordinate): Boolean {
    val (x, y) = coordinate
    if (x < 0 || y < 0) return false
    val z = x * x + 3 * x + 2 * x * y + y + y * y + INPUT
    return bitCount(z) % 2 == 0
}

private fun shortestPath(start: Coordinate, dest: Coordinate): Int {
    return reachableNodes(from = start,
        neighbours = { it.destination.directNeighbours },
        reachable = { isAccessible(it) },
        process = {
            if (it.destination == dest) it.pathLength else null
        }) ?: error("Could not find path")
}

private fun countLocations(start: Coordinate, maxLength: Int): Int {
    var count = 0
    return reachableNodes(from = start,
        neighbours = { it.destination.directNeighbours },
        reachable = { isAccessible(it) },
        process = {
            if (it.pathLength > maxLength) {
                count
            } else {
                count++
                null
            }
        }) ?: error("Could not find path")
}

fun main() {
    // Part 1
    println(shortestPath(Coordinate(1, 1), Coordinate(31, 39)))

    // Part 2
    println(countLocations(Coordinate(1, 1), 50))
}