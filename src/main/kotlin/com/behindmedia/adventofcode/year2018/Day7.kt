package com.behindmedia.adventofcode.year2018

import java.util.*
import kotlin.math.max

fun List<Day7.Node>.asString(): String {
    return this.joinToString("") { it.value }
}

class Day7 {

    data class Entry(val first: String, val second: String) {

        companion object {
            private val regex = Regex("Step ([A-Z]) must be finished before step ([A-Z]) can begin\\.")

            fun fromString(s: String): Entry {
                val matchResult = regex.find(s)
                if (matchResult != null) {
                    val first = matchResult.groups[1]?.value
                    val second = matchResult.groups[2]?.value

                    if (first != null && second != null) {
                        return Entry(first, second)
                    }
                }
                throw IllegalArgumentException("Could not com.behindmedia.adventofcode.year2018.parse string: ${s}")
            }
        }
    }

    data class Node(val value: String): Comparable<Node> {
        companion object {
            var defaultDuration = 60
        }

        private val parentNodes = mutableSetOf<Node>()
        private val childNodes = mutableSetOf<Node>()

        val children: Set<Node>
            get() = childNodes

        val parents: Set<Node>
            get() = parentNodes

        fun addChild(child: Node) {
            childNodes.add(child)
            child.parentNodes.add(this)
        }

        fun removeAsParentFromChildren() {
            for (child in children) {
                child.parentNodes.remove(this)
            }
        }

        override fun compareTo(other: Node): Int {
            return value.compareTo(other.value)
        }
    }

    fun process(entries: List<Entry>): List<Node> {
        return processWithWorkers(entries, 1) { 0 }.first
    }

    class TimedNode(val node: Node, val time: Int): Comparable<TimedNode> {
        override fun compareTo(other: TimedNode): Int {
            val result = this.time.compareTo(other.time)
            if (result != 0) {
                return result
            }
            return this.node.compareTo(other.node)
        }
    }

    fun processWithWorkers(entries: List<Entry>, workerCount: Int, duration: (Node) -> Int = { 60 + (it.value[0] - 'A' + 1) }): Pair<List<Node>, Int> {

        val nodes: Map<String, Node> = entries.fold(mutableMapOf()) { map, entry ->
            val parent = map.getOrPut(entry.first) { Node(entry.first) }
            val child = map.getOrPut(entry.second) { Node(entry.second) }
            parent.addChild(child)
            map
        }

        val ready: TreeSet<TimedNode> = TreeSet(nodes.values.filter { it.parents.isEmpty() }.map { TimedNode(it, 0) })
        val pending = ArrayList(nodes.values.filter { it.parents.isNotEmpty() })
        var time = 0
        val workers = mutableMapOf<Int, Int>()
        for (i in 0 until workerCount) {
            workers[i] = 0
        }
        val result = mutableListOf<Node>()

        while(true) {
            val nextNode = ready.popFirst() ?: break

            //pickup by first free worker if any
            val firstWorker = workers.entries.minBy { it.value } ?:
                throw IllegalStateException("No worker")

            time = max(time, max(nextNode.time, firstWorker.value))
            val endTime = time + duration(nextNode.node)

            workers[firstWorker.key] = endTime

            println("Worker ${firstWorker.key} starts working on ${nextNode.node.value} at $time")

            nextNode.node.removeAsParentFromChildren()
            result.add(nextNode.node)

            pending.removeIf {
                val shouldRemove = it.parents.isEmpty()
                if (shouldRemove) {
                    ready.add(TimedNode(it, endTime))
                }
                shouldRemove
            }
        }
        time =  workers.values.max() ?: time
        return Pair(result, time)
    }
}