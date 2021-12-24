package com.behindmedia.adventofcode.year2021.day24

import com.behindmedia.adventofcode.common.*

/*
mul x 0
add x z
mod x 26
div z a
add x b
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y c
mul y x
add z y
 */
// This is the effective function used to transform input to output, it has three parameters a, b, c which differ per input
private fun function(w: Long, value: Long, a: Long, b: Long, c: Long): Long {
    var x = 0L
    var y = 25L
    var z = value
    x += value
    x %= 26L
    z /= a
    x += b
    x = if (x == w) 0 else 1
    y *= x
    y += 1
    z *= y
    y = w + c
    y *= x
    z += y
    return z
}

private fun findParameters(data: List<List<String>>): MutableList<List<Long>> {
    val p = mutableListOf<Long>()
    val parameters = mutableListOf<List<Long>>()
    fun addParameters() {
        if (p.isNotEmpty()) {
            require(p.size == 3)
            parameters += p.toList()
        }
    }
    var i = 0
    for (d in data) {
        // New input section
        if (d[0] == "inp") {
            addParameters()
            p.clear()
            i = 0
        } else {
            when (i) {
                3 -> p += d[2].toLong()
                4 -> p += d[2].toLong()
                14 -> p += d[2].toLong()
            }
            i++
        }
    }
    addParameters()
    return parameters
}

fun solve(
    smallest: Boolean,
    parameters: List<List<Long>>,
    level: Int,
    value: Long,
    seen: MutableSet<Pair<Int, Long>> = HashSet()
): String? {
    if (level >= parameters.size) {
        // value should be zero
        return if (value == 0L) "" else null
    }

    val cacheKey = Pair(level, value)
    if (cacheKey in seen) return null

    val p = parameters[level]
    var w = 9L
    while (w >= 1L) {
        val i = if (smallest) 10 - w else w
        val next = function(i, value, p[0], p[1], p[2])
        val result = solve(smallest, parameters, level + 1, next, seen)
        if (result != null) {
            return "$i$result"
        }
        w--
    }
    seen += cacheKey
    return null
}

fun main() {
    val data = parseLines("/2021/day24.txt") { line ->
        line.split(" ")
    }
    val parameters = findParameters(data)

    // Part 1
    println(solve(false, parameters, 0, 0L, HashSet()))

    // Part 2
    println(solve(true, parameters, 0, 0L, HashSet()))
}
