package com.behindmedia.adventofcode.year2018

class Day9 {

    private class Node(val value: Int) {
        var prev: Node = this
        var next: Node = this

        fun traverse(steps: Int): Node {
            var currentNode = this
            if (steps >= 0) {
                for (i in 0 until steps) {
                    currentNode = currentNode.next
                }
            } else {
                for (i in 0 until -steps) {
                    currentNode = currentNode.prev
                }
            }
            return currentNode
        }

        fun insertAfter(node: Node) {
            val currentNext = this.next
            this.next = node
            node.prev = this
            node.next = currentNext
            currentNext.prev = node
        }

        fun remove() {
            prev.next = next
            next.prev = prev
        }

        override fun toString(): String {
            return value.toString()
        }

        fun toArrayString(): String {
            val buffer = StringBuilder()
            var currentNode = this
            do {
                if (buffer.length > 0) {
                    buffer.append(" ")
                }
                buffer.append(currentNode.value)
                currentNode = currentNode.next
            } while (currentNode != this)
            return buffer.toString()
        }
    }

    fun winningScoreSlow(numberOfPlayers: Int, lastMarble: Int): Int {
        val start = System.currentTimeMillis()
        val marbles = mutableListOf(0)
        var currentMarbleIndex = 0
        var currentPlayer = 0
        val playerScores = IntArray(numberOfPlayers)

        for (i in 1..lastMarble) {
            val marbleCount = marbles.size
            if (i % 23 == 0) {
                currentMarbleIndex -= 7
                if (currentMarbleIndex < 0) {
                    currentMarbleIndex += marbleCount
                }
                val removedMarble = marbles[currentMarbleIndex]
                val score = i + removedMarble

                playerScores[currentPlayer] += score

                marbles.removeAt(currentMarbleIndex)
                if (currentMarbleIndex == marbleCount - 1) {
                    currentMarbleIndex = 0
                }
            } else {
                currentMarbleIndex += 2
                if (currentMarbleIndex > marbleCount) {
                    currentMarbleIndex -= marbleCount
                }
                marbles.add(currentMarbleIndex, i)
            }

            if (currentPlayer == numberOfPlayers - 1) {
                currentPlayer = 0
            } else {
                currentPlayer++
            }
        }

        playerScores.sort()
        val end = System.currentTimeMillis()
        val duration = end - start

        println("Took ${duration} ms")

        return playerScores.last()
    }

    fun winningScore(numberOfPlayers: Int, lastMarble: Int): Long {
        val start = System.currentTimeMillis()
        val beginMarble = Node(0)
        var currentMarble = beginMarble
        var currentPlayer = 0
        val playerScores = LongArray(numberOfPlayers)

        for (i in 1..lastMarble) {
            if (i % 23 == 0) {
                val removedMarble = currentMarble.traverse(-7)
                val score = i + removedMarble.value
                playerScores[currentPlayer] += score.toLong()
                currentMarble = removedMarble.next
                removedMarble.remove()
            } else {
                val insertionPoint = currentMarble.traverse(1)
                currentMarble = Node(i)
                insertionPoint.insertAfter(currentMarble)
            }
            if (currentPlayer == numberOfPlayers - 1) {
                currentPlayer = 0
            } else {
                currentPlayer++
            }
        }

        playerScores.sort()
        val end = System.currentTimeMillis()
        val duration = end - start

        println("Took ${duration} ms")

        return playerScores.last()
    }
}