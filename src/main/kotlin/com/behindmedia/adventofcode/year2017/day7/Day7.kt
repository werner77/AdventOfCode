package com.behindmedia.adventofcode.year2017.day7

import com.behindmedia.adventofcode.common.*

private val regex = """([a-z]+) (\d+)( -> .*)""".toRegex()

data class Node(val name: String, val weight: Int, val dependencies: Set<String>)

fun main() {
    val nodes = parseLines("/2017/day7.txt") { line ->
        val components = line.split(" -> ")
        if (components.size == 1) {
            val (name, id) = components[0].splitNonEmptySequence(" ", "(", ")").toList()
            Node(name, id.toInt(), emptySet())
        } else if (components.size == 2) {
            val (name, id) = components[0].splitNonEmptySequence(" ", "(", ")").toList()
            val dependencies = components[1].splitNonEmptySequence(" ", ",").toSet()
            Node(name, id.toInt(), dependencies)
        } else {
            error("Invalid input: $line")
        }
    }

    val allDependencies = nodes.flatMap { it.dependencies }.toSet()
    val allNodes = nodes.map { it.name }.toSet()
    val rest = allNodes - allDependencies

    // Part 1
    val rootNodeName = rest.only()
    println(rootNodeName)

    val rootNode = nodes.first { it.name == rootNodeName }
    val nodeMap = nodes.fold(mutableMapOf<String, Node>()) { map, node ->
        map.apply {
            put(node.name, node)
        }
    }

    // Part 2
    findIncorrectWeight(rootNode, nodeMap)
}

private fun findIncorrectWeight(node: Node, nodeMap: Map<String, Node>): Int? {
    var cumulativeWeight = node.weight

    // Count the different weights
    val weightCounts = defaultMutableMapOf<Int, MutableList<Node>> { mutableListOf() }
    for (d in node.dependencies) {
        // All weights need to be equal
        val dependentNode = nodeMap[d] ?: error("Node $d not found in map")
        val nodeWeight = findIncorrectWeight(dependentNode, nodeMap) ?: return null
        weightCounts.getOrPutDefault(nodeWeight) += dependentNode
        cumulativeWeight += nodeWeight
    }

    if (weightCounts.size > 1) {
        require(weightCounts.size == 2) { "Incorrect number of weights encountered" }
        val (incorrectWeight, incorrectNodes) = weightCounts.entries.minByOrNull { it.value.size } ?: error("No entry found")
        val incorrectNode = incorrectNodes.only()
        val correctWeight = weightCounts.entries.maxByOrNull { it.value.size }?.key ?: error("No entry found")

        println("Node ${incorrectNode.name} has total weight $incorrectWeight but should be $correctWeight")

        val diff = correctWeight - incorrectWeight
        val modifiedWeight = incorrectNode.weight + diff

        println("Correct weight for this node would be: $modifiedWeight")

        return null
    }
    return cumulativeWeight
}