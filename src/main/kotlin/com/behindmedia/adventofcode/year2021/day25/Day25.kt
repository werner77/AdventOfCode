package com.behindmedia.adventofcode.year2021.day25

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseMap("/2021/day25.txt") { line ->
        line
    }
    var map = data
    var i = 0
    while (true) {
        val (m, moveCount) = simulate(map)
        map = m
        i++
        if (moveCount == 0) break
    }
    println(i)
}

fun Coordinate.addModulo(delta: Coordinate, minCoordinate: Coordinate, maxCoordinate: Coordinate): Coordinate {
    var newX = this.x + delta.x
    var newY = this.y + delta.y
    val deltaX = newX - maxCoordinate.x
    val deltaY = newY - maxCoordinate.y
    if (deltaX > 0) {
        newX = minCoordinate.x + deltaX - 1
    }
    if (deltaY > 0) {
        newY = minCoordinate.y + deltaY - 1
    }
    return Coordinate(newX, newY)
}

private fun simulate(map: Map<Coordinate, Char>): Pair<Map<Coordinate, Char>, Int> {
    val minCoordinate = map.minCoordinate
    val maxCoordinate = map.maxCoordinate
    var data = map
    var moveCount = 0
    for (right in listOf(true, false)) {
        val newData = mutableMapOf<Coordinate, Char>()
        for (coordinate in minCoordinate..maxCoordinate) {
            val v = data[coordinate] ?: error("No data found for coordinate: $coordinate")
            if (newData[coordinate] == null) {
                val rightCoordinate = coordinate.addModulo(Coordinate.right, minCoordinate, maxCoordinate)
                val downCoordinate = coordinate.addModulo(Coordinate.down, minCoordinate, maxCoordinate)
                if (v == '>' && right && data[rightCoordinate] == '.') {
                    newData[coordinate] = '.'
                    newData[rightCoordinate] = v
                    moveCount++
                } else if (v == 'v' && !right && data[downCoordinate] == '.') {
                    newData[coordinate] = '.'
                    newData[downCoordinate] = v
                    moveCount++
                } else {
                    newData[coordinate] = v
                }
            }
        }
        data = newData
    }
    return Pair(data, moveCount)
}
