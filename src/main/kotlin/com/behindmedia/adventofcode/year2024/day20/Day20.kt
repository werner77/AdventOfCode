package com.behindmedia.adventofcode.year2024.day20

import com.behindmedia.adventofcode.common.*

private typealias Cheat = Pair<Coordinate, Coordinate>

fun main() = timing {
    val grid = CharGrid(read("/2024/day20.txt"))

    // Part 1
    println(grid.countGoodCheats(2))

    // Part 2
    println(grid.countGoodCheats(20))
}

private fun CharGrid.countGoodCheats(maxTime: Int, minSave: Int = 100): Int {
    val pathStart = this.single { it.value == 'S' }.key
    val pathEnd = this.single { it.value == 'E' }.key
    val path = shortestPath(
        from = pathStart,
        neighbours = { it.destination.directNeighbours },
        reachable = { _, it -> this.getOrNull(it) != '#' },
        process = { if (it.destination == pathEnd) it else null }
    ) ?: error("No path found")
    val steps = path.allNodes.withIndex().associateBy({ it.value }, { it.index })
    val seenCheats = defaultMutableMapOf<Int, MutableSet<Cheat>>(putValueImplicitly = true) { mutableSetOf() }
    for ((start, startLength) in steps) {
        findCheat(start, maxTime) { cheatPath ->
            val destinationLength = steps[cheatPath.destination]
            if (destinationLength != null) {
                val save = destinationLength - startLength - cheatPath.length
                if (save >= minSave) {
                    val nodes = cheatPath.nodes { it != start }
                    seenCheats[save] += nodes.first() to nodes.last()
                }
            }
        }
    }
    val counts = seenCheats.mapValues { it.value.size }
    //println(counts.toSortedMap())
    return counts.values.sum()
}

private fun CharGrid.findCheat(start: Coordinate, maxLength: Int, handle: (Path<Coordinate>) -> Unit) {
    val pending = ArrayDeque<Path<Coordinate>>()
    pending.add(Path(start, 0, null))
    val seen = mutableSetOf<Coordinate>()
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        val position = current.destination
        val length = current.length
        if (!seen.add(position) || length > maxLength) continue
        if (length > 1) {
            handle(current)
        }
        for (next in position.directNeighbours) {
            if (this.containsKey(next)) {
                pending.add(Path(next, length + 1, current))
            }
        }
    }
}