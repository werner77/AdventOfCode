package com.behindmedia.adventofcode.year2018.day10

import com.behindmedia.adventofcode.common.*

fun main() {
    val regex = """position=< *(-?\d+), *(-?\d+)> velocity=< *(-?\d+), *(-?\d+)>""".toRegex()
    val data = parseLines("/2018/day10.txt") { line ->
        val (x, y, u, v) = regex.matchEntire(line)?.destructured ?: error("Invalid line: $line")
        Pair(Coordinate(x.toInt(), y.toInt()), Coordinate(u.toInt(), v.toInt()))
    }
    val map = MutableList(data.size) { i ->
        data[i].first
    }
    var t = 0
    var minArea = Long.MAX_VALUE
    while(true) {
        for (j in map.indices) {
            map[j] = map[j] + data[j].second
        }
        val area = rangeSize(map)
        if (area < minArea) {
            minArea = area
        } else {
            // show message, reverting last change
            for (j in map.indices) {
                map[j] = map[j] - data[j].second
            }
            map.fold(mutableMapOf<Coordinate, Char>()) { m, c ->
                m.apply {
                    put(c, '#')
                }
            }.printMap(' ')
            println(t)
            break
        }
        t++
    }
}

fun rangeSize(map: Collection<Coordinate>): Long {
    val range = map.range()
    val start = range.start
    val end = range.endInclusive
    return (end.x.toLong() - start.x.toLong() + 1) * (end.y.toLong() - start.y.toLong() + 1)
}