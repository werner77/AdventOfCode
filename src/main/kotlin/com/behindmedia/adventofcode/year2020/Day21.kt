package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.only
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.popFirst

class Day21 {

    data class Food(val ingredients: Set<String>, val allergens: Set<String>)

    fun part1(input: String): Long {
        val foods = parseFoods(input)
        val candidateMap = candidateMapOfIngredients(foods)
        val candidateIngredients = candidateMap.values.flatten().toSet()
        val allIngredients = foods.flatMap { it.ingredients }.toSet()
        val orphanIngredients = allIngredients - candidateIngredients
        return foods.flatMap { it.ingredients }.count { orphanIngredients.contains(it) }.toLong()
    }

    fun part2(input: String): String {
        val foods = parseFoods(input)
        val candidateMap = candidateMapOfIngredients(foods)
        val resolved = reduceCandidates(candidateMap)
        return resolved.sortedBy { it.first }.joinToString(",") { it.second }
    }

    private fun parseFoods(input: String): List<Food> {
        return parseLines(input) { line ->
            val components = line.split("(contains ").map { it.trim() }.filter { it.isNotEmpty() }
            val ingredients = components[0].split(" ").map { it.trim() }.toSet()
            val allergens = components[1].split(' ', ',').map { it.trim(' ', ')') }.filter { it.isNotEmpty() }.toSet()
            Food(ingredients, allergens)
        }
    }

    private fun candidateMapOfIngredients(foods: List<Food>): Map<String, Set<String>> {
        // Construct a map where the key is the allergen and the value is a list of foods containing that allergen
        val allergenToFoodMap: Map<String, List<Food>> = foods.fold(mutableMapOf<String, MutableList<Food>>()) { map, food ->
            map.apply {
                food.allergens.forEach { allergen ->
                    map.getOrPut(allergen) { mutableListOf() }.add(food)
                }
            }
        }

        // Return a map where the key is the allergen and the value is the possible set of ingredients
        return allergenToFoodMap.entries.fold(mutableMapOf()) { m, entry ->
            m.apply {
                // Calculate the intersection of the ingredient list for all foods containing this allergen, that is the
                // list of candidates
                this[entry.key] = entry.value.map { it.ingredients }.intersection()
            }
        }
    }

    private fun reduceCandidates(candidateMap: Map<String, Set<String>>): List<Pair<String, String>> {
        val unresolved = candidateMap.entries.map { Pair(it.key, it.value.toMutableSet()) }.toMutableList()
        // If there is only one candidate, remove it from all other sets
        val resolved = mutableListOf<Pair<String, String>>()
        while (unresolved.isNotEmpty()) {
            unresolved.sortBy { it.second.size }
            val next = unresolved.popFirst() ?: error("There should be at least on item present")
            val ingredient = next.second.only()
            unresolved.forEach {
                it.second.remove(ingredient)
            }
            resolved.add(Pair(next.first, ingredient))
        }
        return resolved
    }

    private fun List<Set<String>>.intersection(): Set<String> {
        var result: MutableSet<String>? = null
        this.forEach { set ->
            result?.retainAll(set) ?: run {
                result = set.toMutableSet()
            }
        }
        return result ?: emptySet()
    }
}