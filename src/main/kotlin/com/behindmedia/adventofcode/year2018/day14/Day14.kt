package com.behindmedia.adventofcode.year2018.day14

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

fun main() {
    val target = parseLines("/2018/day14.txt") { line ->
        line.toInt()
    }.first()
    part1(target)
    part2(target)
}

private fun part1(
    target: Int
) {
    val recipes = ArrayList<Int>()
    // Initial state
    recipes.add(3)
    recipes.add(7)
    var i = 0
    var j = 1
    while (recipes.size < target + 10) {
        var sum = recipes[i] + recipes[j]
        val toAdd = ArrayList<Int>()
        while (true) {
            val digit = sum % 10
            toAdd += digit
            sum /= 10
            if (sum == 0) break
        }
        recipes += toAdd.reversed()
        i = (i + recipes[i] + 1) % recipes.size
        j = (j + recipes[j] + 1) % recipes.size
    }
    val ans = simulate {
        if (it.size >= target + 10) {
            (0 until 10).map { i -> recipes[target + i] }.joinToString("")
        } else {
            null
        }
    }
    println(ans)
}

private fun part2(target: Int) {
    val match = target.toList()
    var start = 0

    val ans = simulate { recipes ->
        // Check the digits
        var result: Int? = null
        while (start < recipes.size) {
            val maxMatchSize = min(match.size, recipes.size - start)
            var matchCount = 0
            for (k in 0 until maxMatchSize) {
                if (match[k] != recipes[start + k]) {
                    break
                }
                matchCount++
            }
            if (matchCount == maxMatchSize) {
                if (maxMatchSize == match.size) {
                    // Found complete match
                    result = start
                }
                break
            } else {
                start++
            }
        }
        result
    }
    println(ans)
}

private fun <T>simulate(predicate: (List<Int>) -> T?): T {
    val recipes = ArrayList<Int>()
    // Initial state
    recipes.add(3)
    recipes.add(7)
    var i = 0
    var j = 1
    while (true) {
        val sum = recipes[i] + recipes[j]
        recipes += sum.toList()
        i = (i + recipes[i] + 1) % recipes.size
        j = (j + recipes[j] + 1) % recipes.size
        return predicate(recipes) ?: continue
    }
}

private fun Int.toList(): List<Int> {
    val result = ArrayList<Int>(2)
    var k = this
    do {
        result += k % 10
        k /= 10
    } while(k > 0)
    return result.also { it.reverse() }
}