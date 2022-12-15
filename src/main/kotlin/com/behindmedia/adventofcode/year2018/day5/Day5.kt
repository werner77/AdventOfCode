package com.behindmedia.adventofcode.year2018.day5

import com.behindmedia.adventofcode.common.*
import kotlin.math.*
import java.util.LinkedList

private fun react(polymer: String, ignore: Char? = null): String {
    val before = LinkedList<Char>()
    val after = LinkedList<Char>(polymer.toList())
    while (true) {
        val next = after.popFirst() ?: break
        if (next.lowercaseChar() == ignore) continue
        val previous = before.lastOrNull()
        if (previous?.lowercaseChar() == next.lowercaseChar() && previous.isLowerCase() != next.isLowerCase()) {
            before.removeLast()
        } else {
            before.add(next)
        }
    }
    return String(before.toCharArray())
}

private fun reactOptimal(polymer: String) : String {
    var optimal: String? = null
    val allChars = polymer.fold(mutableSetOf<Char>()) { set, c ->
        set.add(c.lowercaseChar())
        set
    }
    for (ignoredChar in allChars) {
        val result = react(polymer, ignoredChar)
        if (optimal == null || result.length < optimal.length) {
            optimal = result
        }
    }
    return optimal ?: polymer
}

fun main() {
    val data = read("/2018/day5.txt").trim()

    // part 1
    println(react(data).length)

    // part 2
    println(reactOptimal(data).length)
}