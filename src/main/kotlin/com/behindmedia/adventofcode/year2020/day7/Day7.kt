package com.behindmedia.adventofcode.year2020.day7

import com.behindmedia.adventofcode.common.*

private val lineRegex = """(.*) bags? contain (.*)""".toRegex()
private val otherRegex = """(\d+) ([\w\s]+) bags?[,\\.]+""".toRegex()

fun main() {
    val children = defaultMutableMapOf<String, MutableList<Pair<String, Int>>>(true) { mutableListOf() }
    val parents = defaultMutableMapOf<String, MutableSet<String>>(true) { mutableSetOf() }
    parseLines("/2020/day7.txt") { line ->
        val (source, destinations) = lineRegex.matchEntire(line)?.destructured ?: error("No match for line: $line")
        val contained: List<Pair<String, Int>> = if (destinations == "no other bags.") {
            listOf()
        } else {
            otherRegex.findAll(destinations).map {
                val (count, name) = it.destructured
                require(!name.contains(",")) {
                    "Name is not valid: $name"
                }
                name to count.toInt()
            }.toList()
        }
        for (c in contained) {
            children[source] += c
            parents[c.first] += source
        }
    }
    println(traverseParents("shiny gold", parents) - 1)
    println(traverseChildren("shiny gold", children) - 1)
}

private fun traverseParents(current: String, parents: DefaultMap<String, out Set<String>>, totalSet: MutableSet<String> = mutableSetOf()): Int {
    totalSet += current
    for (parent in parents[current]) {
        traverseParents(parent, parents, totalSet)
    }
    return totalSet.size
}

private fun traverseChildren(current: String, children: DefaultMap<String, out List<Pair<String, Int>>>): Int {
    var totalCount = 1
    for ((name, count) in children[current]) {
        totalCount += count * traverseChildren(name, children)
    }
    return totalCount
}