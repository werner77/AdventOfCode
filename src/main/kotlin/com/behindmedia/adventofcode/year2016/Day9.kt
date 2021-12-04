package com.behindmedia.adventofcode.year2016

import com.behindmedia.adventofcode.common.*

private fun <T> parseLines(lineParser: (String) -> T): List<T> {
    return parseLines("/2016/day9.txt") {
        lineParser.invoke(it)
    }
}

private fun deconstruct(code: String, pos: Int, line: String, builder: StringBuilder): Int {
    val (x, y) = code.splitNonEmptySequence("x") { it.toInt() }.toList()
    val toRepeat = line.substring(pos, pos + x)
    for (j in 0 until y) {
        builder.append(toRepeat)
    }
    return x
}

private fun part1() {
    val result = parseLines { line ->
        decompress(line)
    }.firstOrNull() ?: error("No result found")
    println(result.length)
}

private fun decompress(
    line: String
) : String {
    val output = StringBuilder()
    val buffer = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < line.length) {
        val c = line[i++]
        when (c) {
            '(' -> inQuotes = true
            ')' -> {
                i += deconstruct(buffer.toString(), i, line, output)
                buffer.clear()
                inQuotes = false
            }
            else -> {
                if (inQuotes) {
                    buffer.append(c)
                } else {
                    output.append(c)
                }
            }
        }
    }
    return output.toString()
}

private fun decompressedLength(line: String, start: Int, end: Int): Long {
    val codeBuffer = StringBuilder()
    var inQuotes = false
    var index = start
    var totalSize = 0L
    while (index < end) {
        val c = line[index++]
        when (c) {
            '(' -> {
                inQuotes = true
            }
            ')' -> {
                val (a, b) = codeBuffer.toString().splitNonEmptySequence("x") { it.toInt() }.toList()
                val d = decompressedLength(line, index, index + a)
                totalSize += b * d
                index += a
                codeBuffer.clear()
                inQuotes = false
            }
            else -> {
                if (inQuotes) {
                    codeBuffer.append(c)
                } else {
                    // Increase size
                    totalSize++
                }
            }
        }
    }
    return totalSize
}

private fun part2() {
    val input = read("/2016/day9.txt").trim()
    println(decompressedLength(input, 0, input.length))
}

fun main() {
    part1()
    part2()
}