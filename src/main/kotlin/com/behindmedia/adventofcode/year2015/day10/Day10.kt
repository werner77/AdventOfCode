package com.behindmedia.adventofcode.year2015.day10

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

class Node(val value: Int) {
    var next: Node? = null

    fun entireSequence(): String {
        val builder = StringBuilder()
        var current: Node? = this
        while (current != null) {
            builder.append(current.value)
            current = current.next
        }
        return builder.toString()
    }
}

private fun iterate(firstNode: Node): Node {
    var ret = firstNode
    var current: Node? = firstNode
    var previous: Node? = null
    while (current != null) {
        var next = current.next
        var count = 1
        while (next?.value == current.value) {
            count++
            next = next.next
        }
        require(count < 10)

        val countNode = Node(count)
        previous?.next = countNode
        countNode.next = current
        if (current == firstNode) ret = countNode
        previous = current
        current.next = next
        current = next
    }
    return ret
}

private fun solve(input: String, iterationCount: Int): String {
    var firstNode: Node? = null
    var lastNode: Node? = null
    for (c in input) {
        val node = Node(c - '0')
        if (firstNode == null) firstNode = node
        lastNode?.next = node
        lastNode = node
    }

    var current = firstNode ?: error("No node present")
    repeat(iterationCount) {
        current = iterate(current)
    }
    return current.entireSequence()
}

fun main() {
    val input = read("/2015/day10.txt").trim()

    timing {
        println(solve(input, 40).length)
        println(solve(input, 50).length)
    }
}