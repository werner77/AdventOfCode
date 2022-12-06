package com.behindmedia.adventofcode.year2022.day6

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = parseLines("/2022/day6.txt") { line ->
        line
    }
    findMarker(data.first(), 4)
    findMarker(data.first(), 14)
}

private fun findMarker(data: String, markerSize: Int) {
    var count = 0
    val window = ArrayDeque<Char>()
    for (c in data) {
        if (window.size == markerSize) {
            window.popFirst()
        }
        window.add(c)
        count++
        if (window.size == markerSize && HashSet(window).size == window.size) {
            break
        }
    }
    println(count)
}

