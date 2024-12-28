package com.behindmedia.adventofcode.year2024.day9

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.common.PriorityQueue

private fun minFileId(size: Int): Int {
    return 0
}

fun main() = timing {
    val data = read("/2024/day9.txt").trim()
    println(part1(data))
    println(part2(data))
}

private fun part1(data: String): Long {
    var size = 0
    for ((i, c) in data.withIndex()) {
        size += c.digitToInt()
    }
    val array = IntArray(size) { -1 }
    var pos = 0
    for ((i, c) in data.withIndex()) {
        val length = c.digitToInt()
        if (i % 2 == 0) {
            val id = i / 2
            for (j in 0 until length) {
                array[pos + j] = id
            }
        }
        pos += length
    }

    var head = 0
    var tail = array.size - 1
    while (true) {
        while (array[head] >= 0) {
            head++
        }
        while (array[tail] == -1) {
            tail--
        }
        if (head >= tail) break
        array[head] = array[tail]
        array[tail] = -1
        head++
        tail--
    }
    var sum = 0L
    for (i in array.indices) {
        val id = array[i]
        if (id < 0) continue
        sum += id.toLong() * i.toLong()
    }
    return sum
}

private fun part2(data: String): Long {
    val fileCount = data.length / 2 + 1
    val files = Array(fileCount) { 0 to 0 }
    var pos = 0
    val gapQueues = Array<PriorityQueue<Pair<Int, Int>>>(9) { PriorityQueue(comparator = Comparator.comparing(Pair<Int, Int>::first)) }
    for ((i, c) in data.withIndex()) {
        val length = c.digitToInt()
        if (i % 2 == 0) {
            // File
            files[i / 2] = pos to length
        } else if (length > 0) {
            // Gap
            for (gapSize in 1 .. length) {
                gapQueues[gapSize - 1] += pos to length
            }
        }
        pos += length
    }
    val seen = hashSetOf<Int>()
    for (fileId in files.size - 1 downTo minFileId(files.size)) {
        val (filePos, fileLength) = files[fileId]
        val queue = gapQueues[fileLength - 1]
        var gap: Pair<Int, Int>?
        do {
            gap = queue.removeFirst()
        } while(gap != null && !seen.add(gap.first))
        val (gapPos, gapLength) = gap ?: continue
        if (gapPos >= filePos) continue
        files[fileId] = gapPos to fileLength
        val newGapLength = gapLength - fileLength
        for (i in 0 until newGapLength) {
            gapQueues[i].add(gapPos + fileLength to newGapLength)
        }
    }
    return files.withIndex().sumOf { (id, info) ->
        val (p, l) = info
        (p until p + l).sumOf { id.toLong() * it.toLong() }
    }
}
