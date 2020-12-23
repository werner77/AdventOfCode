package com.behindmedia.adventofcode.year2020

class Day23 {

    class Cup(values: List<Int>) {

        /**
         * Single linked list
         */
        class Node(val value: Int, var next: Node?) {
            override fun toString(): String {
                return "Node(value=$value)"
            }

            fun requireNext(): Node {
                return next ?: error("No next node present")
            }
        }

        /**
         * Map to quickly find nodes by value
         */
        private val lookupMap: Map<Int, Node>
        private var currentNode: Node
        private val size: Int = values.size
        private val minValue = values.minOrNull() ?: error("Expected at least one value to be present")
        private val maxValue = values.maxOrNull() ?: error("Expected at least one value to be present")
        private val pickedUpNodeValues = IntArray(3)

        private fun node(value: Int): Node {
            return lookupMap[value] ?: error("Could find no node with the specified value")
        }

        init {
            lookupMap = HashMap<Int, Node>(size, 1.0f).also { map ->
                var firstNode: Node? = null
                var lastNode: Node? = null
                for (value in values) {
                    val node = Node(value, null)
                    if (firstNode == null) {
                        firstNode = node
                    }
                    map[value] = node
                    lastNode?.next = node
                    lastNode = node
                }
                lastNode?.next = firstNode
                currentNode = firstNode ?: error("Expected at least one node to be present")
            }
        }

        fun toList(fromValue: Int? = null, inclusive: Boolean = true, count: Int = size - 1): List<Int> {
            return ArrayList<Int>(size).apply {
                var node = fromValue?.let { node(it) } ?: currentNode
                repeat(count + 1) {
                    if (it > 0 || inclusive) {
                        add(node.value)
                    }
                    node = node.requireNext()
                }
            }
        }

        fun play() {
            var nextNode = currentNode.requireNext()
            val firstPickedUpNode = nextNode
            var lastPickedUpNode = firstPickedUpNode
            var targetValue = currentNode.value - 1
            repeat(3) { index ->
                pickedUpNodeValues[index] = nextNode.value
                lastPickedUpNode = nextNode
                nextNode = nextNode.requireNext()
            }
            currentNode.next = nextNode
            while (targetValue < minValue || pickedUpNodeValues.contains(targetValue)) {
                if (targetValue < minValue) {
                    targetValue = maxValue
                } else {
                    targetValue--
                }
            }
            val targetNode = node(targetValue)
            val next = targetNode.next
            targetNode.next = firstPickedUpNode
            lastPickedUpNode.next = next
            currentNode = currentNode.requireNext()
        }
    }

    fun part1(input: String): String {
        val values = input.map { it.toString().toInt() }
        val cup = Cup(values)
        repeat(100) {
            cup.play()
        }
        return cup.toList(fromValue = 1, inclusive = false).joinToString(separator = "")
    }

    fun part2(input: String): Long {
        val values = input.map { it.toString().toInt() }.toMutableList()
        var currentValue = values.maxOrNull()!!
        while (values.size < 1_000_000) {
            values.add(++currentValue)
        }
        val cup = Cup(values)
        repeat(10_000_000) {
            cup.play()
        }
        return cup.toList(fromValue = 1, inclusive = false, count = 2).map { it.toLong() }.reduce { acc, i -> acc * i }
    }
}