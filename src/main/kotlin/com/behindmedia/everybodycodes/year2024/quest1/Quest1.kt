package com.behindmedia.everybodycodes.year2024.quest1

import com.behindmedia.adventofcode.common.*

/*
Ancient Ant (A): Not very dangerous. Can be managed without using any potions.
Badass Beetle (B): A big and strong bug that requires 1 potion to defeat.
Creepy Cockroach (C): Fast and aggressive! This creature requires 3 potions to defeat it.
 */
fun main() {
    part1()
    part2()
}

private fun part1() {
    val data = read("/2024/quest1-1.txt")
    val potions = mapOf(
        'A' to 0,
        'B' to 1,
        'C' to 3,
    )
    println(data.sumOf { potions[it]!! })
}

private fun part2() {
    val data = read("/2024/quest1-2.txt")
    val potions = mapOf(
        'A' to 0,
        'B' to 1,
        'C' to 3,
        'D' to 5,
        'x' to 0
    )

    val ans = data.chunked(2).sumOf { s ->
        val i = s[0]
        val j = s[1]
        if (i != 'x' && j != 'x') {
            potions[i]!! + potions[j]!! + 2
        } else {
            potions[i]!! + potions[j]!!
        }
    }
    println(ans)
}