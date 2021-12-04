package com.behindmedia.adventofcode.year2016

import com.behindmedia.adventofcode.common.*

private fun <T> parseLines(lineParser: (String) -> T): List<T> {
    return parseLines("/2016/day7.txt") {
        lineParser.invoke(it)
    }
}

private fun String.containsAbba(): Boolean {
    for (i in 0 until this.length - 3) {
        if (this[i] == this[i + 3] && this[i + 1] == this[i + 2] && this[i + 1] != this[i]) {
            return true
        }
    }
    return false
}

private fun String.findBabs(): Set<String> {
    val babs = mutableSetOf<String>()
    for (i in 0 until this.length - 2) {
        if (this[i] == this[i + 2] && this[i + 1] != this[i]) {
            babs.add(this.substring(i until i + 3))
        }
    }
    return babs
}

private fun isTls(ip: String): Boolean {
    val components = ip.splitNonEmptySequence("[","]").toList()
    var foundAbba = false
    for (i in components.indices) {
        if (i % 2 == 1) {
            if (components[i].containsAbba()) return false
        } else {
            if (components[i].containsAbba()) foundAbba = true
        }
    }
    return foundAbba
}

private fun String.inverted() : String {
    return "${this[1]}${this[0]}${this[1]}"
}

private fun isSsl(ip: String): Boolean {
    val components = ip.splitNonEmptySequence("[","]").toList()
    val allBabs = mutableSetOf<String>()
    for (i in components.indices) {
        if (i % 2 == 0) {
            allBabs.addAll(components[i].findBabs())
        }
    }

    for (i in components.indices) {
        if (i % 2 == 1) {
            for (bab in allBabs) {
                if (components[i].contains(bab.inverted())) return true
            }
        }
    }
    return false
}

private fun part1() {
    val lines = parseLines { it }
    println(lines.count { isTls(it) })
}

private fun part2() {
    val lines = parseLines { it }
    println(lines.count { isSsl(it) })
}

fun main() {
    //part1()
    part2()
}