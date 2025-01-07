package com.behindmedia.adventofcode.year2018.day7

import com.behindmedia.adventofcode.common.*
import kotlin.math.*
import java.util.TreeSet

fun main() {
    val regex = """Step ([A-Z]+) must be finished before step ([A-Z]+) can begin.""".toRegex()

    val steps = mutableMapOf<String, MutableSet<String>>()
    val adjacencyList = mutableMapOf<String, MutableSet<String>>()
    parseLines("/2018/day7.txt") { line ->
        val (a, b) = regex.matchEntire(line)?.destructured ?: error("Line does not match: $line")
        if (steps[a] == null) steps[a] = mutableSetOf()
        steps.getOrPut(b) { mutableSetOf() }.add(a)
        adjacencyList.getOrPut(a) { mutableSetOf() }.add(b)
        Unit
    }

    // Part 1
    println(topologicalSort(adjacencyList).joinToString(""))

    part2(steps, 5)
}

private fun part2(steps: Map<String, Set<String>>, workerCount: Int) {
    val remainingEdges = mutableMapOf<String, MutableSet<String>>()
    val pending = TreeSet<Pair<Int, String>>(compareBy({ it.first }, { it.second }))
    for ((c, edges) in steps) {
        if (edges.isEmpty()) {
            // Add to pending
            pending.add(Pair(0, c))
        } else {
            // Add to remaining
            remainingEdges[c] = edges.toMutableSet()
        }
    }
    val workers = mutableMapOf<Int, Int>()
    for (worker in 1..workerCount) {
        workers[worker] = 0
    }
    var maxTime = 0
    while (pending.isNotEmpty()) {
        val (timeStepAvailable, step) = pending.popFirst() ?: error("No element found left")
        val stepDuration = 60 + (step[0] - 'A' + 1)

        // Check whether a worker is available and let him work
        val (id, timeWorkerAvailable) = workers.entries.minByOrNull { it.value } ?: error("No worker found")

        val time = max(timeStepAvailable, timeWorkerAvailable) + stepDuration
        workers[id] = time
        maxTime = max(maxTime, time)

        // Remove next from the incoming edges
        val iterator = remainingEdges.iterator()
        while (iterator.hasNext()) {
            val (c, e) = iterator.next()
            e.remove(step)
            if (e.isEmpty()) {
                pending += Pair(time, c)
                iterator.remove()
            }
        }
    }
    println(maxTime)
}