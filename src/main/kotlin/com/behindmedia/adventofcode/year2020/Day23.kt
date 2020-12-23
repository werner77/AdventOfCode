package com.behindmedia.adventofcode.year2020

class Day23 {

    data class Cup(val values: List<Int>) {

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
        var currentNode: Node
            private set
        val size: Int
            get() = values.size
        private val minValue = values.minOrNull() ?: error("Expected at least one value to be present")
        private val maxValue = values.maxOrNull() ?: error("Expected at least one value to be present")
        private fun node(value: Int): Node {
            return lookupMap[value] ?: error("Could find no node with the specified value")
        }

        init {
            var firstNode: Node? = null
            var lastNode: Node? = null
            val map = HashMap<Int, Node>(size, 1.0f)
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
            currentNode = firstNode!!
            lookupMap = map
        }

        fun toList(fromValue: Int? = null, inclusive: Boolean = true, count: Int = size): List<Int> {
            return ArrayList<Int>(size).apply {
                var node = fromValue?.let { node(it) } ?: currentNode
                repeat(count) {
                    if (it > 0 || inclusive) {
                        add(node.value)
                    }
                    node = node.requireNext()
                }
            }
        }

        fun play() {
            val pickedUpNodes = ArrayList<Node>(3)
            val pickedUpValues = HashSet<Int>(3, 1.0f)
            var nextNode = currentNode.requireNext()
            var targetValue = currentNode.value - 1
            repeat(3) {
                pickedUpValues.add(nextNode.value)
                pickedUpNodes.add(nextNode)
                nextNode = nextNode.requireNext()
            }
            currentNode.next = nextNode
            pickedUpNodes.last().next = null
            while (pickedUpValues.contains(targetValue) || targetValue < minValue) {
                if (targetValue < minValue) {
                    targetValue = maxValue
                } else {
                    targetValue--
                }
            }
            val findNode = node(targetValue)
            val next = findNode.next
            findNode.next = pickedUpNodes.first()
            pickedUpNodes.last().next = next
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
        return cup.toList(fromValue = 1, inclusive = false, count = 3).map { it.toLong() }.reduce { acc, i -> acc * i }
    }
}