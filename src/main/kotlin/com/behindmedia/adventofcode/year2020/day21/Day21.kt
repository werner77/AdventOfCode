package com.behindmedia.adventofcode.year2020.day21

import com.behindmedia.adventofcode.common.parseLines

private data class Food(val ingredients: Set<String>, val allergens: Set<String>) {
    companion object {
        private val regex = """(.*) \(contains (.*)\)""".toRegex()
        operator fun invoke(string: String): Food {
            val (ingredients, allergens) = regex.matchEntire(string)?.destructured ?: error("Invalid line: $string")
            return Food(
                ingredients.split(" ").filter { it.isNotEmpty() }.toSet(),
                allergens.split(", ").filter { it.isNotEmpty() }.toSet()
            )
        }
    }
}

fun main() {
    val foods = parseLines("/2020/day21.txt") { line ->
        Food(line)
    }

    // Map of key = allergen, value = possible ingredients
    val possibleIngredients = foods.fold(mutableMapOf<String, Set<String>>()) { map, food ->
        map.apply {
            food.allergens.forEach {
                this[it] = this[it]?.intersect(food.ingredients) ?: food.ingredients
            }
        }
    }

    // Part 1
    val allergenIngredients = possibleIngredients.values.flatten().toSet()
    println(foods.sumOf { it.ingredients.count { ingredient -> ingredient !in allergenIngredients } })

    // Part 2
    val inDegrees = mutableMapOf<String, MutableSet<String>>()
    for ((allergen, ingredients) in possibleIngredients) {
        for (ingredient in ingredients) {
            inDegrees.getOrPut(ingredient) { mutableSetOf() } += allergen
        }
    }
    val settled = mutableMapOf<String, String>()
    while (true) {
        val (ingredient, allergens) = inDegrees.entries.firstOrNull { it.value.size == 1 } ?: break
        inDegrees.remove(ingredient)

        val allergen = allergens.single()
        settled[ingredient] = allergen

        // Remove allergen from others
        (possibleIngredients[allergen] ?: emptySet()).forEach {
            inDegrees[it]?.remove(allergen)
        }
    }
    require(settled.size == possibleIngredients.size)
    println(settled.entries.sortedBy { it.value }.joinToString(",") { it.key })
}