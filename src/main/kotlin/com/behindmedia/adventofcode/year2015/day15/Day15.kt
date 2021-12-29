package com.behindmedia.adventofcode.year2015.day15

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

private data class Ingredient(
    val name: String,
    val capacity: Long,
    val durability: Long,
    val flavor: Long,
    val texture: Long,
    val calories: Long
) {
    companion object {
        operator fun invoke(string: String): Ingredient {
            val components = string.trim().splitNonEmptySequence(":", ",", " ").toList()
            val name = components[0]
            val capacity = components[2].toLong()
            val durability = components[4].toLong()
            val flavor = components[6].toLong()
            val texture = components[8].toLong()
            val calories = components[10].toLong()
            return Ingredient(name, capacity, durability, flavor, texture, calories)
        }
    }

    operator fun get(index: Int): Long {
        return when (index) {
            0 -> capacity
            1 -> durability
            2 -> flavor
            3 -> texture
            4 -> calories
            else -> error("Invalid index: $index")
        }
    }
}

private fun solve(ingredients: List<Ingredient>, predicate: (Long) -> Boolean = { _ -> true }): Long {
    fun solve(sums: LongArray, amount: Long, ingredients: Array<Ingredient>, ingredientIndex: Int): Long? {
        val ingredient = ingredients[ingredientIndex]
        if (ingredientIndex == ingredients.size - 1) {
            // Last ingredient, calculate score
            var score = 1L
            val calories = sums[4] + ingredient[4] * amount
            return if (predicate(calories)) {
                for (i in 0 until 4) {
                    score *= max(0, sums[i] + ingredient[i] * amount)
                }
                score
            } else {
                null
            }
        }
        var maxScore: Long? = null
        for (j in 0..amount) {
            for (i in 0 until 5) {
                sums[i] += j * ingredient[i]
            }
            val result = solve(sums, amount - j, ingredients, ingredientIndex + 1)
            if (result != null) maxScore = max(maxScore ?: 0L, result)
            for (i in 0 until 5) {
                sums[i] -= j * ingredient[i]
            }
        }
        return maxScore
    }
    return solve(LongArray(5) { 0L }, 100, ingredients.toTypedArray(), 0) ?: error("No solution found")
}

fun main() {
    val data = parseLines("/2015/day15.txt") { line ->
        Ingredient(line)
    }
    timing {
        // part 1
        println(solve(data))

        // part 2
        println(solve(data) {
            it == 500L
        })
    }
}