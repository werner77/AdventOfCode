package com.behindmedia.adventofcode.year2016.day22

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

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
            val nodeCoordinate = Coordinate(nodeNameSplit[1].substring(1).toInt(), nodeNameSplit[2].substring(1).toInt())
            Disk(nodeCoordinate, parseSize(components[1]), parseSize(components[2]), parseSize(components[3]))
        }
    }
    part1(data)
    part2(data)
}

private fun part2(data: List<Disk>) {
    val map = mutableMapOf<Coordinate, Disk>()
    var maxX = 0
    for (disk in data) {
        if (disk.location.y == 0) maxX = max(maxX, disk.location.x)
        map[disk.location] = disk
    }

    val startDisk = map[Coordinate(maxX, 0)]!!
    val endDisk = map[Coordinate.origin]

    println("Start: $startDisk")
    println("End: $endDisk")

//    reachableNodes(
//        from = startDisk,
//        neighbours = {
//            it.destination.location.directNeighbours.mapNotNull { d -> map[d] }
//        },
//        reachable = { },
//        process = { null }
//    )
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

private fun backtrack(start: Coordinate, end: Coordinate, map: Map<Coordinate, Disk>) {
    // Get possible coordinates from end

    val neighbours = end.directNeighbours

    for (neighbour in neighbours) {
        // Take the minimum from all the neighbours
        val neighbourDisk = map[neighbour] ?: continue


    }
}