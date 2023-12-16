package com.behindmedia.adventofcode.year2023.day15

import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.splitNonEmpty
import com.behindmedia.adventofcode.common.splitWithDelimiters

fun main() {
    val data = read("/2023/day15.txt").splitNonEmpty(",", " ", "\n")

    // Part 1
    println(data.sumOf { it.hash })

    // Part 2
    println(process(data).focussingPower)
}

private fun process(data: List<String>): Map<Long, Map<String, Long>> {
    val map = data.fold(
        defaultMutableMapOf<Long, MutableMap<String, Long>>(
            putValueImplicitly = true,
            defaultValue = { linkedMapOf() })
    ) { map, item ->
        val (label, delimiter, rest) = item.splitWithDelimiters("-", "=")
        val boxNumber = label.hash
        if (delimiter == "=") {
            val focalLength = rest.toLong()
            map[boxNumber][label] = focalLength
        } else {
            map[boxNumber].remove(label)
        }
        map
    }
    return map
}

private val String.hash: Long
    get() = fold(0L) { hash, c -> ((hash + c.code.toLong()) * 17L) % 256L }

private val Map<Long, Map<String, Long>>.focussingPower: Long
    get() = entries.sumOf { (boxNumber, contents) ->
        contents.values.withIndex().sumOf { (i, f) -> (1 + boxNumber) * (i + 1) * f }
    }