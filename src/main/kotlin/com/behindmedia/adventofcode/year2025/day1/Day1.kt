package com.behindmedia.adventofcode.year2025.day1

import com.behindmedia.adventofcode.common.*

private data class Rotation(val left: Boolean, val amount: Int) {
    override fun toString(): String {
        return if (left) "L$amount" else "R$amount"
    }
}

private fun part1(rotations: List<Rotation>): Int {
    var currentPos = 50
    var count = 0
    for (rotation in rotations) {
        val amountLeft = rotation.amount % 100
        if (rotation.left) {
            currentPos -= amountLeft
        } else {
            currentPos += amountLeft
        }
        if (currentPos < 0) {
            currentPos += 100
        } else if (currentPos >= 100) {
            currentPos -= 100
        }
        if (currentPos == 0) {
            count++
        }
    }
    return count
}

private fun part2(rotations: List<Rotation>): Int {
    var currentPos = 50
    var count = 0
    for (rotation in rotations) {
        val last = currentPos

        // Count the complete rotations
        count += rotation.amount / 100

        // Amount left after complete rotations
        val amountLeft = rotation.amount % 100

        if (amountLeft == 0) {
            if (last == 0 && rotation.amount == 0) {
                // Special case, rotation amount exactly zero, not sure if this is possible or not.
                count++
            }
            continue
        }

        if (rotation.left) {
            currentPos -= amountLeft
        } else {
            currentPos += amountLeft
        }
        if (currentPos < 0) {
            currentPos += 100
            if (last != 0) count++
        } else if (currentPos >= 100) {
            currentPos -= 100
            if (last != 0) count++
        } else if (currentPos == 0) {
            // Exactly 0
            count++
        }
    }
    return count
}

fun main() {
    val rotations = parseLines("/2025/day1.txt") { line ->
        if (line.startsWith("R")) {
            Rotation(false, line.substring(1).toInt())
        } else {
            Rotation(true, line.substring(1).toInt())
        }
    }

    println(part1(rotations))
    println(part2(rotations))
}