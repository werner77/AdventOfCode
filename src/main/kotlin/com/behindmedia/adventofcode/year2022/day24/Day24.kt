package com.behindmedia.adventofcode.year2022.day24

import com.behindmedia.adventofcode.common.*

private data class Blizzard(val initialPosition: Coordinate, val direction: Coordinate) {
    fun position(time: Int, sizeX: Int, sizeY: Int): Coordinate {
        val x = ((initialPosition.x - 1) + time * direction.x).mod(sizeX - 2) + 1
        val y = ((initialPosition.y - 1) + time * direction.y).mod(sizeY - 2) + 1
        return Coordinate(x, y)
    }
}

private operator fun Coordinate.Companion.invoke(char: Char): Coordinate? {
    return when (char) {
        '>' -> right
        '<' -> left
        '^' -> up
        'v' -> down
        else -> null
    }
}

private fun getBlizzardPositionsAt(
    sizeX: Int, sizeY: Int, time: Int, blizzards: Collection<Blizzard>, cache: MutableMap<Int, Set<Coordinate>>
): Set<Coordinate> {
    return cache.getOrPut(time) {
        blizzards.fold(hashSetOf()) { s, b ->
            s.apply { add(b.position(time, sizeX, sizeY)) }
        }
    }
}

fun main() {
    val data = parseMap("/2022/day24.txt") { line ->
        line
    }
    timing {
        val range: CoordinateRange = data.keys.range()
        val blizzards = data.entries.mapNotNull { entry -> Coordinate(entry.value)?.let { Blizzard(entry.key, it) } }
        val start = data.entries.filter { it.value == '.' }.minOf { it.key }
        val finish = data.entries.filter { it.value == '.' }.maxOf { it.key }
        require(blizzards.filter { it.direction in setOf(Coordinate.up, Coordinate.down) }
            .none { it.initialPosition.x in setOf(start.x, finish.x) })
        val cache = mutableMapOf<Int, Set<Coordinate>>()
        part1(start, finish, data, range, blizzards, cache)
        part2(start, finish, data, range, blizzards, cache)
    }
}

private fun part1(
    start: Coordinate,
    finish: Coordinate,
    data: Map<Coordinate, Char>,
    range: CoordinateRange,
    blizzards: List<Blizzard>,
    cache: MutableMap<Int, Set<Coordinate>>,
) {
    println(findShortestTime(listOf(start, finish), data, range, blizzards, cache))
}

private fun part2(
    start: Coordinate,
    finish: Coordinate,
    data: Map<Coordinate, Char>,
    range: CoordinateRange,
    blizzards: List<Blizzard>,
    cache: MutableMap<Int, Set<Coordinate>>,
) {
    println(findShortestTime(listOf(start, finish, start, finish), data, range, blizzards, cache))
}

private fun findShortestTime(
    coordinates: List<Coordinate>,
    map: Map<Coordinate, Char>,
    range: CoordinateRange,
    blizzards: List<Blizzard>,
    cache: MutableMap<Int, Set<Coordinate>>
): Int {
    var time = 0
    for (i in 0 until coordinates.size - 1) {
        val start = coordinates[i]
        val finish = coordinates[i + 1]
        time = shortestPath(
            from = Pair(start, time),
            neighbours = { path ->
                val (c, t) = path.destination
                list(5) {
                    for (n in c.directNeighbours)
                        add(Pair(n, t + 1))
                    add(Pair(c, t + 1))
                }
            },
            reachable = { _, (c, t) ->
                val value = map[c] ?: '#'
                value != '#' && c !in getBlizzardPositionsAt(
                    range.sizeX, range.sizeY, t, blizzards, cache
                )
            },
            process = { path ->
                if (path.destination.first == finish) {
                    path.destination.second
                } else {
                    null
                }
            }
        ) ?: error("No path found")
    }
    return time
}