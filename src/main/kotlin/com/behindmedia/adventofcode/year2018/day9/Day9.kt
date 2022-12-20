package com.behindmedia.adventofcode.year2018.day9

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private data class Node(val value: Long) {
    lateinit var next: Node
    lateinit var previous: Node
}

fun main() {
    val regex = """(\d+) players; last marble is worth (\d+) points""".toRegex()
    val (playerCount, worth) = parseLines("/2018/day9.txt") { line ->
        val (playerCount, worth) = regex.matchEntire(line)?.destructured ?: error("Line does not match")
        Pair(playerCount.toInt(), worth.toLong())
    }.first()

    // part 1
    play(playerCount, worth)

    // part 2
    play(playerCount, worth * 100)
}

private fun play(playerCount: Int, worth: Long) {
    var n = 0L
    var k = 0
    var currentNode: Node? = null
    val scores = LongArray(playerCount)
    while (n <= worth) {
        val node = Node(value = n)
        if (currentNode == null) {
            // First node
            currentNode = node
            node.previous = node
            node.next = node
        } else if (n % 23L != 0L) {
            // Insert node betreen currentNode.next and currentNode.next.next
            val node1 = currentNode.next
            val node2 = node1.next
            node1.next = node
            node2.previous = node
            node.next = node2
            node.previous = node1
            currentNode = node
        } else {
            scores[k] += node.value
            var n: Node = currentNode
            repeat(7) {
                n = n.previous
            }
            scores[k] += n.value

            val nLeft = n.previous
            val nRight = n.next

            // Remove this node
            currentNode = nRight
            nLeft.next = nRight
            nRight.previous = nLeft
        }
        n++
        k++
        k %= playerCount
    }
    println(scores.toList().max())
}