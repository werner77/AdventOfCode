package com.behindmedia.adventofcode.year2015.day19

import com.behindmedia.adventofcode.common.parse
import com.behindmedia.adventofcode.common.plusAssign
import java.util.*

fun replace(input: String, rules: List<Pair<String, String>>): Sequence<String> = sequence {
    for (i in input.indices) {
        for (rule in rules) {
            if (rule.first.length <= i + 1) {
                var matches = true
                for (j in rule.first.indices) {
                    if (input[i + 1 - rule.first.length + j] != rule.first[j]) {
                        matches = false
                        break
                    }
                }
                if (matches) {
                    val builder = StringBuilder(input.length + rule.second.length - rule.first.length)
                    for (k in 0 until i + 1 - rule.first.length) {
                        builder += input[k]
                    }
                    builder += rule.second
                    for (k in i + 1 until input.length) {
                        builder += input[k]
                    }
                    yield(builder.toString())
                }
            }
        }
    }
}

fun factory(target: String, start: String, rules: List<Pair<String, String>>): Int {
    val queue = PriorityQueue<Pair<String, Int>>(Comparator.comparing { it.first.length })
    queue += Pair(start, 0)
    while (queue.isNotEmpty()) {
        val current = queue.poll()

        if (current.first == target) {
            return current.second
        }

        for (next in replace(current.first, rules)) {
            queue += Pair(next, current.second + 1)
        }
    }
    error("No possibilities found")
}

private fun List<Pair<String, String>>.inverted(): List<Pair<String, String>> {
    return this.map { Pair(it.second, it.first) }
}

fun main() {
    val rules = mutableListOf<Pair<String, String>>()
    lateinit var molecule: String
    parse("/2015/day19.txt") { text ->
        val parts = text.trim().split("\n\n")
        require(parts.size == 2)
        for (line in parts[0].split("\n")) {
            val components = line.trim().split(" => ")
            require(components.size == 2)
            rules += Pair(components[0], components[1])
        }
        molecule = parts[1].trim()
    }

    // Part 1
    val result1 = replace(molecule, rules)
    println(result1.toSet().size)

    // Part 2
    val result2 = factory("e", molecule, rules.inverted())
    println(result2)
}