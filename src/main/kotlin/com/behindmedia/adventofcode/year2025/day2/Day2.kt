package com.behindmedia.adventofcode.year2025.day2

import com.behindmedia.adventofcode.common.*

private fun isInvalid1(n: Long): Boolean {
    val s = n.toString()
    if (s.length % 2 == 1) return false
    val firstHalf = s.take(s.length / 2)
    val secondHalf = s.substring(s.length / 2)
    return firstHalf == secondHalf
}

private fun isInvalid2(n: Long): Boolean {
    val s = n.toString()
    for (j in 1 .. s.length / 2) {
        val pattern = StringView(s, 0, j)
        if (matchesPattern(s, pattern)) return true
    }
    return false
}

private fun matchesPattern(s: CharSequence, pattern: CharSequence): Boolean {
    if (pattern.length >= s.length) return false
    if (s.length % pattern.length != 0) return false
    for (i in 1 until s.length / pattern.length) {
        for (j in 0 until pattern.length) {
            if (s[i * pattern.length + j] != pattern[j]) return false
        }
    }
    return true
}

fun main() {
    val ranges = parse("/2025/day2.txt") { line ->
        line.trim().split(",").map { part ->
            val components = part.split("-")
            require(components.size == 2) { "Unparsable input: $part" }
            components[0].toLong()..components[1].toLong()
        }
    }
    timing {
        for (part in 1..2) {
            var ans = 0L
            for (range in ranges) {
                for (l in range) {
                    val invalid = if (part == 1) isInvalid1(l) else isInvalid2(l)
                    if (invalid) {
                        ans += l
                    }
                }
            }
            println(ans)
        }
    }
}

