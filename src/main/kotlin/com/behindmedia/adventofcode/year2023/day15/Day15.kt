package com.behindmedia.adventofcode.year2023.day15

import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.read

fun main() {
    val data = read("/2023/day15.txt").split(',', ' ', '\n').filter { it.isNotEmpty() }

    // Part 1
    println(data.sumOf { hash(it) })

    // Part 2
    val map = process(data)
    println(focussingPower(map))
}

private fun process(data: List<String>): Map<Long, Map<String, Long>> {
    val map = data.fold(
        defaultMutableMapOf<Long, MutableMap<String, Long>>(
            putValueImplicitly = true,
            defaultValue = { linkedMapOf() })
    ) { map, item ->
        val (label, rest) = item.split('-', '=')
        val boxNumber = hash(label)
        if (item.indexOf('=') >= 0) {
            val focalLength = rest.toLong()
            map[boxNumber][label] = focalLength
        } else {
            map[boxNumber].remove(label)
        }
        map
    }
    return map
}

private fun hash(string: String): Long {
    return string.fold(0L) { value, c ->
        ((value + c.code.toLong()) * 17L) % 256L
    }
}

private fun focussingPower(map: Map<Long, Map<String, Long>>): Long {
    return map.entries.sumOf { (boxNumber, value) ->
        value.values.withIndex().sumOf { (i, f) -> (1 + boxNumber) * (i + 1) * f }
    }
}