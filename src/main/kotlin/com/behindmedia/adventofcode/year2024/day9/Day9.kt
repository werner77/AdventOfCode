package com.behindmedia.adventofcode.year2024.day9

import com.behindmedia.adventofcode.common.*
import java.util.*
import kotlin.math.*

fun main() = timing {
    val data = read("/2024/day9.txt").trim()

    println(part1(data))
    println(part2(data))
}

data class FileInfo(val id: Int, val length: Int)

private fun part1(data: String): Long {
    val fileMap = getFileMap(data)
    var pos = 0
    while (true) {
        val (filePos, file) = fileMap.lastOrNull { it.value.id >= 0 && it.key >= pos } ?: break
        val (gapPos, gap) = fileMap.removeFirstOrNull { it.key < filePos && it.value.id == -1 } ?: break
        if (gap.length >= file.length) {
            // Completely move file
            fileMap.remove(filePos)
            fileMap[gapPos] = file
            val sizeLeft = gap.length - file.length
            if (sizeLeft > 0) {
                fileMap[gapPos + file.length] = FileInfo(id = -1, length = sizeLeft)
            }
        } else {
            // Partly move file
            fileMap[filePos] = FileInfo(file.id, file.length - gap.length)
            fileMap[gapPos] = FileInfo(file.id, gap.length)
        }
        pos = gapPos + min(gap.length, file.length)
    }

    return fileMap.checksum()
}

private fun getFileMap(data: String): TreeMap<Int, FileInfo> {
    val fileMap = TreeMap<Int, FileInfo>()
    var pos = 0
    for ((i, c) in data.withIndex()) {
        val id = if (i % 2 == 0) {
            i / 2
        } else {
            -1
        }
        val length = c.digitToInt()
        fileMap[pos] = FileInfo(id, length)
        pos += length
    }
    return fileMap
}


private fun part2(data: String): Long {
    val fileMap = getFileMap(data)
    val seen = mutableSetOf<Int>()
    while (true) {
        val (filePos, file) = fileMap.lastOrNull { it.value.id >= 0 && seen.add(it.value.id) } ?: break

        // Check if there is a gap to move to, if not just ignore and continue with next
        val (gapPos, gap) = fileMap.removeFirstOrNull { it.key < filePos && it.value.id == -1 && it.value.length >= file.length } ?: continue

        fileMap.remove(filePos)
        fileMap[gapPos] = file
        val sizeLeft = gap.length - file.length
        if (sizeLeft > 0) {
            fileMap[gapPos + file.length] = FileInfo(id = -1, length = sizeLeft)
        }
    }
    return fileMap.checksum()
}

private fun TreeMap<Int, FileInfo>.checksum(): Long {
    var sum = 0L
    for ((pos, file) in this) {
        if (file.id == -1) continue
        for (i in 0 until file.length) {
            sum += (pos + i).toLong() * file.id.toLong()
        }
    }
    return sum
}

private fun TreeMap<Int, FileInfo>.removeFirstOrNull(where: (Map.Entry<Int, FileInfo>) -> Boolean): Pair<Int, FileInfo>? {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (where(next)) {
            val result = next.key to next.value
            iterator.remove()
            return result
        }
    }
    return null
}

private fun TreeMap<Int, FileInfo>.lastOrNull(where: (Map.Entry<Int, FileInfo>) -> Boolean): Pair<Int, FileInfo>? {
    val iterator = this.descendingMap().iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (where(next)) {
            return next.key to next.value
        }
    }
    return null
}

private fun TreeMap<Int, FileInfo>.asString(): String {
    val builder = StringBuilder()
    for ((_, file) in this) {
        for (i in 0 until file.length) {
            builder.append(if (file.id >= 0 ) file.id else ".")
        }
    }
    return builder.toString()
}