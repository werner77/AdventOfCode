package com.behindmedia.adventofcode.year2020.day6

import com.behindmedia.adventofcode.common.read

fun main() {
    val data = read("/2020/day6.txt")
    //val data = example

    val sections = data.split("\n\n").map { it.trim() }

    println(sections.sumOf {
        it.split("\n").fold(mutableSetOf<Char>()) { set, line -> set.apply { addAll(line.toList()) } }.size
    })

    println(sections.sumOf {
        var first = true
        it.split("\n").fold(mutableSetOf<Char>()) { set, line -> set.apply {
            if (first) {
                addAll(line.toList())
                first = false
            } else {
                retainAll(line.toSet())
            }
        } }.size
    })
}