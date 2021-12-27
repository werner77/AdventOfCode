package com.behindmedia.adventofcode.year2015.day8

import com.behindmedia.adventofcode.common.*

private fun part1(string: String): Pair<Int, Int> {
    var escape = false
    var inQuotes = false
    val buffer = StringBuilder()
    var i = 0
    while (i < string.length) {
        val c = string[i++]
        if (escape) {
            escape = false
            if (c == 'x') {
                val charCode = string.substring(i, i + 2).toInt(16)
                buffer.append(Char(charCode))
                i += 2
            } else {
                buffer.append(c)
            }
        } else {
            when(c) {
                '\"' -> {
                    inQuotes = !inQuotes
                }
                '\\' -> {
                    escape = true
                }
                else -> {
                    require(inQuotes)
                    buffer.append(c)
                }
            }
        }
    }
    return Pair(string.length, buffer.length)
}

private fun part2(string: String): Pair<Int, Int> {
    val buffer = StringBuilder()
    buffer.append('"')
    for (c in string) {
        when (c) {
            '"' -> {
                buffer.append("\\\"")
            }
            '\\' -> {
                buffer.append("\\\\")
            }
            else -> {
                buffer.append(c)
            }
        }
    }
    buffer.append('"')
    return Pair(buffer.length, string.length)
}

val sample = """
    ""
    "abc"
    "aaa\"aaa"
    "\x27"
""".trimIndent()

fun main() {
    val data = parseLines("/2015/day8.txt") { line ->
        line
    }
    println(data.sumOf {
        val p = part1(it)
        p.first - p.second
    })

    println(data.sumOf {
        val p = part2(it)
        p.first - p.second
    })
}