package com.behindmedia.adventofcode.year2016.day22

import com.behindmedia.adventofcode.common.*

private data class Disk(val location: Coordinate, val total: Long, val used: Long, val available: Long) {
}

private fun parseSize(string: String): Long = string.substring(0, string.length - 1).toLong()

private fun isViablePair(disk1: Disk, disk2: Disk): Boolean {
    return disk1.used > 0 && disk1 != disk2 && disk1.used <= disk2.available
}

/*
root@ebhq-gridcenter# df -h
Filesystem              Size  Used  Avail  Use%
/dev/grid/node-x0-y0     90T   69T    21T   76%
 */
fun main() {
    var i = 0
    val data = parseLines("/2016/day22.txt") { line ->
        line
    }.mapNotNull {
        if (i++ < 2) null
        else {
            val components = it.splitNonEmptySequence(" ", "\t").toList()
            val nodeNameSplit = components[0].splitNonEmptySequence("-").toList()
            val nodeCoordinate =
                Coordinate(nodeNameSplit[1].substring(1).toInt(), nodeNameSplit[2].substring(1).toInt())
            Disk(nodeCoordinate, parseSize(components[1]), parseSize(components[2]), parseSize(components[3]))
        }
    }
    part1(data)
    part2(data)
}

private fun part2(data: List<Disk>) {

    val maxX = data.maxOf { it.location.x }
    val maxY = data.maxOf { it.location.y }
    val sourceDisk = data.find { it.location.y == 0 && it.location.x == maxX } ?: error("Could not find source disk")
    val freeNode = data.filter { it.available >= sourceDisk.used }.only()

    // Create a map
    val map = mutableMapOf<Coordinate, Char>()
    for (disk in data) {
        val diskType = if (disk.available > sourceDisk.used) {
            'F'
        } else if (disk.used > freeNode.available) {
            '#'
        } else if (disk.location.y == 0 && disk.location.x == maxX) {
            'S'
        } else if (disk.location.y == 0 && disk.location.x == 0) {
            'T'
        } else {
            '.'
        }
        map[disk.location] = diskType
    }

    // Step 1: move F to S

    var steps = 0

    // First bring free node to S

    steps += shortestPath(from = freeNode.location,
        neighbours = { it.destination.directNeighbours },
        reachable = { _, it -> map[it] != '#' && it.x in 0..maxX && it.y in 0..maxY },
        process = { if (map[it.destination] == 'S') it.pathLength else null })?.toInt() ?: error("Could not find path")

    // S is now at x=0, maxY-1

    // Step 2: move S to T, each step takes 5 moves

    steps += (maxX - 1) * 5

    println(steps)

}

private fun part1(data: List<Disk>) {
    var count = 0
    for (i in data.indices) {
        for (j in data.indices) {
            val node1 = data[i]
            val node2 = data[j]
            if (isViablePair(node1, node2)) count++
        }
    }
    println(count)
}
