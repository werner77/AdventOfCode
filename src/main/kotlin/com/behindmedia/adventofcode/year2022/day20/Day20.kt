package com.behindmedia.adventofcode.year2022.day20

import com.behindmedia.adventofcode.common.*
import kotlin.math.abs
import kotlin.math.sign

private class Node(val value: Long) {
    var before: Node? = null
    var after: Node? = null

    fun move(direction: Int) {
        val a = this.after!!
        val b = this.before!!
        if (direction > 0) {
            this.after = a.after
            a.after!!.before = this

            this.before = a
            a.after = this

            b.after = a
            a.before = b
        } else if (direction < 0) {
            // before becomes after
            this.before = b.before
            b.before!!.after = this

            this.after = b
            b.before = this

            a.before = b
            b.after = a
        }
    }

    fun toList(): List<Long> {
        val result = mutableListOf<Long>()
        var node = this
        while (true) {
            result += node.value
            node = node.after ?: error("No node after found")
            if (node == this) break
        }
        return result
    }

    override fun toString(): String {
        return value.toString()
    }
}

private fun List<Long>.toMap(multiplier: Long = 1L): Map<Int, Node> {
    val result = mutableMapOf<Int, Node>()
    var firstNode: Node? = null
    var lastNode: Node? = null
    for ((i, value) in this.withIndex()) {
        val n = Node(value * multiplier)
        n.before = lastNode
        lastNode?.after = n
        if (firstNode == null) firstNode = n
        result[i] = n
        lastNode = n
    }
    firstNode?.before = lastNode
    lastNode?.after = firstNode
    return result
}

private fun getMoveCount(index: Long, size: Int): Int {
    val modulo = size - 1
    var target = (index % modulo.toLong()).toInt()
    if (abs(target) > size / 2) {
        if (target < 0) {
            target += modulo
        } else {
            target -= modulo
        }
    }
    return target
}

private fun decrypt(nodeMap: Map<Int, Node>, maxCount: Int = nodeMap.size) {
    var i = 0
    val totalSize = nodeMap.size
    while (i < maxCount) {
        val node = nodeMap[i++] ?: error("No node found for index: $i")
        var moveCount = getMoveCount(node.value, totalSize)
        if (moveCount > totalSize / 2) {
            moveCount -= (totalSize - 1)
        }
        repeat(abs(moveCount)) {
            node.move(moveCount.sign)
        }
    }
}

fun main() {
    val data = parseLines("/2022/day20.txt") { line ->
        line.toLong()
    }
    timing {
        part1(data)
    }
    timing {
        part2(data)
    }
}

private fun part1(data: List<Long>) {
    solve(data, 1L, 1)
}

private fun part2(data: List<Long>) {
    solve(data, 811_589_153L, 10)
}

private fun solve(
    data: List<Long>,
    multiplier: Long,
    repeatCount: Int
) {
    val nodeMap = data.toMap(multiplier = multiplier)
    val firstNode = nodeMap[0]!!
    repeat(repeatCount) {
        decrypt(nodeMap)
    }
    val list = firstNode.toList()
    val startIndex = list.indexOfFirst { it == 0L }
    var sum = 0L
    for (i in 1..3) {
        sum += list[(startIndex + i * 1000) % list.size]
    }
    println(sum)
}