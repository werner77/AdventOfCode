package com.behindmedia.adventofcode.year2017.day17

import com.behindmedia.adventofcode.common.read

data class Node(val value: Int) {
    var next: Node = this
    var prev: Node = this

    fun insertAfter(node: Node) {
        val currentNext = this.next
        this.next = node
        node.next = currentNext
        node.prev = this
        currentNext.prev = node
    }

    fun all(): List<Node> {
        var current = this
        val result = mutableListOf<Node>()
        while (true) {
            result += current
            current = current.next
            if (current == this) break
        }
        return result
    }
}

fun part1(stepSize: Int, count: Int): Node {
    val start = Node(0)
    var size = 1
    var current = start
    for (i in 1 ..count) {
        for (j in 0 until (stepSize % size)) {
            current = current.next
        }
        val newNode = Node(i)
        current.insertAfter(newNode)
        size++
        current = newNode
        if (i % 1_000_000 == 0) println(i)
    }
    return current
}

fun part2(stepSize: Int, count: Int): Int {
    var lastOneValue = 0
    var pos = 0
    var size = 1
    for (value in 1..count) {
        pos = ((pos + stepSize) % size) + 1
        if (pos == 1) {
            lastOneValue = value
        }
        size++
    }
    return lastOneValue
}

fun main() {
    val stepSize = read("/2017/day17.txt").trim().toInt()
    println(part1(stepSize, 2017))
    println(part2(stepSize, 50_000_000))
}