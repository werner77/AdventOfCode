package com.behindmedia.adventofcode.year2024.day9

import com.behindmedia.adventofcode.common.*
import java.util.*

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
    val gaps = TreeMap<Int, Int>()
    val files = mutableMapOf<Int, Pair<Int, Int>>()
    var pos = 0
    for ((i, c) in data.withIndex()) {
        val length = c.digitToInt()
        if (i % 2 == 0) {
            // File
            val id = i / 2
            files[id] = pos to length
        } else {
            // Gap
            gaps[pos] = length
        }
        pos += length
    }
    val maxId = files.keys.max()
    for (id in maxId downTo 0) {
        val (filePos, fileLength) = files[id] ?: error("No file with id $id found")

        // Find first fitting position
        for ((gapPos, gapLength) in gaps) {
            if (gapPos >= filePos) break
            if (gapLength < fileLength) continue

            files[id] = gapPos to fileLength

            // Remove gap
            gaps.remove(gapPos)
            if (gapLength > fileLength) {
                gaps[gapPos + fileLength] = gapLength - fileLength
            }
            break
        }
    }
    var result = 0L
    for ((id, info) in files) {
        val (p, l) = info
        for (i in p until p + l) {
            result += id.toLong() * i.toLong()
        }
    }
    return result
}