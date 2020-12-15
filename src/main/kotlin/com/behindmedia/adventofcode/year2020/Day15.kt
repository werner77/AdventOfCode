package com.behindmedia.adventofcode.year2020

class Day15 {

    fun part1(input: String, endCount: Long = 2020): Long {
        val values = input.split(",").map { it.toLong() }
        val map = values.subList(0, values.size - 1).foldIndexed(mutableMapOf<Long, Long>()) { index, m, value ->
            m.apply {
                put(value, index.toLong())
            }
        }
        var index = map.size.toLong()
        var lastNumber = values.last()
        while (index < endCount - 1) {
            val lastIndex = map[lastNumber]
            map[lastNumber] = index
            lastNumber = if (lastIndex == null) 0 else index - lastIndex
            index += 1L
        }
        return lastNumber
    }
}