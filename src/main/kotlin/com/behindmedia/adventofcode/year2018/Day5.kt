package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.year2018.popFirst
import java.util.*

class Day5 {

    fun react(polymer: String, ignore: Char? = null): String {
        val before = LinkedList<Char>()
        val after = LinkedList<Char>(polymer.toList())
        while (true) {
            val next = after.popFirst() ?: break
            if (next.toLowerCase() == ignore) continue

            val previous = before.lastOrNull()

            if (previous?.toLowerCase() == next.toLowerCase() && previous.isLowerCase() != next.isLowerCase()) {
                before.removeLast()
            } else {
                before.add(next)
            }
        }
        return String(before.toCharArray())
    }

    fun reactOptimal(polymer: String) : String {
        var optimal: String? = null

        val allChars = polymer.fold(mutableSetOf<Char>()) { set, c ->
            set.add(c.toLowerCase())
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

}