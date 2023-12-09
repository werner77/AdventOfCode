package com.behindmedia.adventofcode.year2023.day8

import com.behindmedia.adventofcode.common.leastCommonMultiple
import com.behindmedia.adventofcode.common.read

fun main() {
    val data = read("/2023/day8.txt")
    val parts = data.split("\n\n")
    val nodeRegex = """([A-Z]+) = \(([A-Z]+), ([A-Z]+)\)""".toRegex()
    val directions = parts[0]
    val nodes: Map<String, Pair<String, String>> = parts[1].split("\n").fold(mutableMapOf()) { map, line ->
        map.apply {
            if (line.isNotEmpty()) {
                val (current, left, right) = nodeRegex.matchEntire(line)?.destructured
                    ?: error("Could not match line: $line")
                put(current, left to right)
            }
        }
    }

    // Part 1
    println(processDirections("AAA", directions, nodes) { it == "ZZZ" })

    // Part2
    println(
        nodes.keys.filter { it.endsWith("A") }
        .map { processDirections(it, directions, nodes) { current -> current.endsWith("Z") } }
        .fold(1L, ::leastCommonMultiple)
    )
}

private fun processDirections(
    startNode: String,
    directions: String,
    nodes: Map<String, Pair<String, String>>,
    predicate: (String) -> Boolean
): Long {
    var currentNode = startNode
    var i = 0
    while (!predicate(currentNode)) {
        val instruction = directions[i % directions.length]
        val option = nodes[currentNode] ?: error("Expected node $currentNode to be present in mappings")
        currentNode = if (instruction == 'L') {
            option.first
        } else {
            option.second
        }
        i++
    }
    return i.toLong()
}