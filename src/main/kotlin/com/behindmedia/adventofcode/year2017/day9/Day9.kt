package com.behindmedia.adventofcode.year2017.day9

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private fun parse(string: String, initialPos: Int, level: Int): Triple<Int, Int, Int> {
    var inGarbage = false
    var escape = false
    var totalLevel = level - 1
    var pos = initialPos
    var garbageCount = 0
    while(pos < string.length) {
        val c = string[pos++]
        if (escape) {
            escape = false
            continue
        }
        when (c) {
            '!' -> escape = true
            '<' -> if (!inGarbage) inGarbage = true else garbageCount++
            '{' -> if (!inGarbage) {
                val (p, l, g) = parse(string, pos,level + 1)
                garbageCount += g
                totalLevel += l
                pos = p
            } else garbageCount++
            '}' -> if (!inGarbage) return Triple(pos, totalLevel, garbageCount) else garbageCount++
            '>' -> if (inGarbage) inGarbage = false
            else -> if (inGarbage) garbageCount++
        }
    }
    return Triple(pos, totalLevel, garbageCount)
}

fun main() {
    val data = parse("/2017/day9.txt") { line ->
        line.trim()
    }
    val (_, level, garbageCount) = parse(data, 0, 1)
    println(level)
    println(garbageCount)
}