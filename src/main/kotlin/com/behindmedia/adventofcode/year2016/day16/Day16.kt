package com.behindmedia.adventofcode.year2016.day16

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

/*
Call the data you have at this point "a".
Make a copy of "a"; call this copy "b".
Reverse the order of the characters in "b".
In "b", replace all instances of 0 with 1 and all 1s with 0.
The resulting data is "a", then a single 0, then "b".
 */
private fun dragonStep(a: String): String {
    val b = StringBuilder(a)
    b.append('0')
    for (i in a.indices) {
        val j = a.length - i - 1
        val c = a[j]
        val d = if (c == '1') {
            '0'
        } else if  (c == '0') {
            '1'
        } else {
            error("Illegal character: $c")
        }
        b.append(d)
    }
    return b.toString()
}

private fun dragon(a: String, maxLength: Int): String {
    var d = a
    while (d.length < maxLength) {
        d = dragonStep(d)
    }
    return d.substring(0, maxLength)
}

private fun checksum(input: String): String {
    var s = input
    val buffer = StringBuilder()
    while (true) {
        for (i in s.indices step 2) {
            buffer.append(if (s[i] == s[i + 1]) '1' else '0')
        }
        if (buffer.length %2 == 1) return buffer.toString()
        s = buffer.toString()
        buffer.clear()
    }
}

fun main() {
    val input = parse("/2016/day16.txt") { it }.trim()
    for (l in listOf(272, 35651584)) {
        val d = dragon(input, l)
        println(checksum(d))
    }
}

