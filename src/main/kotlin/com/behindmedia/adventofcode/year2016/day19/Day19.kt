package com.behindmedia.adventofcode.year2016.day19

typealias Operation = (Long) -> Long

private fun part1(n: Long): Long {
    // x is the number of elves remaining
    var x = n
    var divisionCount = 0

    // Keep track of the inverse of the operations done
    val operations = mutableListOf<Operation>()
    while (x != 1L) {
        divisionCount++
        if (x % 2L == 0L) {
            // Even
            operations.add {
                it * 2
            }
            x /= 2
        } else {
            operations.add {
                (it - 1) * 2
            }
            x = x / 2L + 1L
        }
    }
    var number = 1L

    // Apply the operations in reverse to get the number
    operations.reversed().forEach { number = it.invoke(number) }

    // Add 1 for 1 based
    return number + 1L
}

class Node(val value: Long) {
    var next: Node? = null
    var prev: Node? = null
    override fun toString(): String {
        return "Node(value=$value)"
    }
}

private fun part2(n: Long): Long {
    var lastNode: Node? = null
    var firstNode: Node? = null
    var currentNode: Node? = null
    for (i in 1..n) {
        val node = Node(i)
        if (firstNode == null) firstNode = node
        lastNode?.next = node
        node.prev = lastNode
        lastNode = node
        if (i == 1 + n / 2) currentNode = node
    }
    lastNode?.next = firstNode
    firstNode?.prev = lastNode

    var count = n
    while (count > 1) {
        // Eliminate currentNode
        val x = currentNode
        currentNode = if (count % 2 == 1L) {
            currentNode?.next?.next
        } else {
            currentNode?.next
        }
        val prev = x?.prev
        val next = x?.next
        next?.prev = prev
        prev?.next = next
        count--
    }
    return currentNode?.value ?: error("Expected current node to exist")
}

const val INPUT = 3017957L

fun main() {
    println(part1(INPUT))
    println(part2(INPUT))
}