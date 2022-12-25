package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.popFirst
import java.util.*

class Day5 {

    fun react(polymer: String, ignore: Char? = null): String {
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

    fun reactOptimal(polymer: String) : String {
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

}