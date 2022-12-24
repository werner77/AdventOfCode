package com.behindmedia.adventofcode.year2022.day24

import com.behindmedia.adventofcode.common.*

private data class Blizzard(val initialPosition: Coordinate, val direction: Coordinate) {
    fun position(time: Int, sizeX: Int, sizeY: Int): Coordinate {
        var x = ((initialPosition.x - 1) + time * direction.x) % (sizeX - 2)
        var y = ((initialPosition.y - 1) + time * direction.y) % (sizeY - 2)
        if (x < 0) x += sizeX - 2
        if (y < 0) y += sizeY - 2
        x++
        y++
        return Coordinate(x, y)
    }
}

private operator fun Coordinate.Companion.invoke(char: Char): Coordinate? {
    return when (char) {
        '>' -> Coordinate.right
        '<' -> Coordinate.left
        '^' -> Coordinate.up
        'v' -> Coordinate.down
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
        time = findShortestPath(from = start,
            startTime = time,
            neighbours = { it.destination.directNeighbours },
            reachable = { _, coordinate, time ->
                val value = map[coordinate] ?: '#'
                value != '#' && coordinate !in getBlizzardPositionsAt(
                    range.sizeX, range.sizeY, time, blizzards, cache
                )
            },
            process = { path ->
                if (path.destination == finish) {
                    path.pathLength
                } else null
            }) ?: error("No path found")
    }
    return time
}

private inline fun <reified N, T> findShortestPath(
    from: N,
    startTime: Int,
    neighbours: (Path<N>) -> List<N>,
    reachable: (Path<N>, N, Int) -> Boolean = { _, _, _ -> true },
    process: (Path<N>) -> T?
): T? {
    val pending = ArrayDeque<Path<N>>()
    val visited = mutableSetOf(Pair(from, startTime))
    pending.add(Path(from, startTime, null))
    while (true) {
        val current = pending.removeFirstOrNull() ?: return null
        process(current)?.let {
            return it
        }
        val candidates = neighbours(current) + listOf(current.destination)
        val nextTime = current.pathLength + 1
        for (neighbour in candidates) {
            val next = Pair(neighbour, nextTime)
            if (next in visited) continue
            if (reachable(current, neighbour, nextTime)) {
                visited += next
                pending.add(Path(neighbour, nextTime, current))
            }
        }
    }
}
