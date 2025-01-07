package com.behindmedia.adventofcode.common

import java.util.function.Function

/**
 * Bron-Kerbosch algorithm to find the maximum cliques
 */
fun <T> findAllMaximalCliques(adjacencyList: Map<T, Set<T>>): List<Set<T>> {
    fun compute(
        r: Set<T>,
        p: Set<T>,
        x: Set<T>,
        adjacencyList: Map<T, Set<T>>,
        result: MutableList<Set<T>>
    ) {
        if (p.isEmpty() && x.isEmpty()) {
            result.add(r)
            return
        }

        // Pick a pivot
        val pivot = p.firstOrNull() ?: x.firstOrNull() ?: error("No pivot found")
        val pivotNeighbors = adjacencyList[pivot] ?: emptySet()

        val p1 = p.toMutableSet()
        val x1 = x.toMutableSet()
        val iterator = p1.iterator()
        while (iterator.hasNext()) {
            val v = iterator.next()
            if (v in pivotNeighbors) {
                continue
            }
            val neighbors = adjacencyList[v] ?: emptySet()
            compute(
                r = r + v,
                p = p1.intersect(neighbors),
                x = x1.intersect(neighbors),
                adjacencyList = adjacencyList,
                result = result
            )
            iterator.remove()
            x1.add(v)
        }
    }
    val result = mutableListOf<Set<T>>()
    compute(emptySet(), adjacencyList.keys, emptySet(), adjacencyList, result)
    return result
}

fun <C: Comparable<C>>topologicalSort(adjacencyList: Map<C, Set<C>>): List<C> {
    return topologicalSort(adjacencyList, Comparator.comparing(Function.identity()))
}

fun <C> topologicalSort(adjacencyList: Map<C, Set<C>>, comparator: Comparator<C>): List<C> {
    return topologicalSort(adjacencyList, SortedQueue(adjacencyList.size, comparator))
}

/**
 * Topological sort without ordering when multiple vertexes have inDegree count of 0.
 *
 * The ordering used will be just the order in which the vertices are encountered in the adjacencyList.
 */
fun <C> topologicalSortUnordered(adjacencyList: Map<C, Set<C>>): List<C> {
    return topologicalSort(adjacencyList, FifoQueue(adjacencyList.size))
}

private fun <C> topologicalSort(adjacencyList: Map<C, Set<C>>, queue: Queue<C>): List<C> {
    val inDegrees = defaultMutableMapOf<C, Int> { 0 }
    for ((_, edges) in adjacencyList) {
        for (to in edges) {
            inDegrees[to]++
        }
    }
    for (vertex in adjacencyList.keys) {
        if (inDegrees[vertex] == 0) {
            queue += vertex
        }
    }
    val result = mutableListOf<C>()
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        result += current
        for (to in adjacencyList[current] ?: emptySet()) {
            val count = inDegrees[to]
            if (count > 1) {
                inDegrees[to] = count - 1
            } else {
                inDegrees.remove(to)
                queue += to
            }
        }
    }
    return result
}