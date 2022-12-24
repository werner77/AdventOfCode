package com.behindmedia.adventofcode.year2016.day4

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.popFirst
import java.util.*

private fun part1() {
    var sum = 0
    parseLines("/2016/day4.txt") { line ->
        // aaaaa-bbb-z-y-x-123[abxyz]
        val components = line.split("-")
        val characterMap = mutableMapOf<Char, Int>()
        for (component in components) {
            if (component.any { it.isDigit() }) {
                // ID and checksum
                val otherComponents = component.split('[', ']').filter { it.isNotBlank() }
                if (otherComponents.size == 2) {
                    val sectorID: Int = otherComponents.first().toInt()
                    val checkSum = otherComponents.last()

                    if (isValid(characterMap, checkSum)) {
                        sum += sectorID
                    }
                } else {
                    error("Parse error for : $component")
                }

            } else {
                for (c in component) {
                    characterMap[c] = (characterMap[c] ?: 0) + 1
                }
            }
        }
    }
    println(sum)
}

private fun part2() {
    parseLines("/2016/day4.txt") { line ->
        // aaaaa-bbb-z-y-x-123[abxyz]
        val components = line.split("-")
        val characterMap = mutableMapOf<Char, Int>()
        val string = StringBuilder()
        var first = true
        for (component in components) {
            if (component.any { it.isDigit() }) {
                // ID and checksum
                val otherComponents = component.split('[', ']').filter { it.isNotBlank() }
                if (otherComponents.size == 2) {
                    val sectorID: Int = otherComponents.first().toInt()
                    val checkSum = otherComponents.last()
                    if (isValid(characterMap, checkSum)) {
                        // decrypt
                        val decrypted = decrypt(string.toString(), sectorID)
                        println("$sectorID: $decrypted")
                    }
                } else {
                    error("Parse error for : $component")
                }
            } else {
                if (first) {
                    first = false
                } else {
                    string.append("-")
                }
                for (c in component) {
                    characterMap[c] = (characterMap[c] ?: 0) + 1
                    string.append(c)
                }
            }
        }
    }
}

private fun decrypt(string: String, id: Int): String {
    val d = StringBuilder()
    for (c in string) {
        if (c == '-') {
            d.append(" ")
        } else {
            d.append('a' + ((c - 'a') + id) % 26)
        }
    }
    return d.toString()
}

private fun isValid(map: Map<Char, Int>, checksum: String): Boolean {
    val sortedEntries = LinkedList(map.entries.sortedByDescending { it.value * 1024 - it.key.code })
    for (c in checksum) {
        val currentEntry = sortedEntries.popFirst() ?: return false
        if (c != currentEntry.key) return false
    }
    return true
}

fun main() {
    part1()
    part2()
}