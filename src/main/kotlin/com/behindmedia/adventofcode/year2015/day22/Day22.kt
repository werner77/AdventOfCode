package com.behindmedia.adventofcode.year2015.day22

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class Effect(
    val id: Int,
    val cost: Int,
    val damage: Int,
    val healing: Int,
    val armor: Int,
    val mana: Int,
    val turns: Int
)

private val effects = listOf<Effect>(
    Effect(1, 53, 4, 0, 0, 0, 1),
    Effect(2, 73, 2, 2, 0, 0, 1),
    Effect(3, 113, 0, 0, 7, 0, 6),
    Effect(4, 173, 3, 0, 0, 0, 6),
    Effect(5, 229, 0, 0, 0, 101, 5),
)

private data class State(val playerActive: Boolean, val mana: Int, val playerHitPoints: Int, val bossHitPoints: Int, val activeEffects: List<Effect>)

private fun simulate(state: State, bossDamage: Int, cache: MutableMap<State, Int>, hardMode: Boolean): Int {
    val cachedResult = cache[state]
    if (cachedResult != null) {
        return cachedResult
    }

    var newPlayerHitPoints = if (state.playerActive && hardMode) state.playerHitPoints - 1 else state.playerHitPoints
    var newMana = state.mana

    if (newMana <= 0 || newPlayerHitPoints <= 0) {
        return -1
    }

    var newBossHitPoints = state.bossHitPoints
    var playerArmor = 0
    val newEffects = ArrayList<Effect>(state.activeEffects.size + 1)
    for (effect in state.activeEffects) {
        newMana += effect.mana
        newPlayerHitPoints += effect.healing
        playerArmor += effect.armor
        newBossHitPoints -= effect.damage
        if (effect.turns > 1) {
            newEffects += effect.copy(turns = effect.turns - 1)
        }
    }

    if (newBossHitPoints <= 0) {
        return 0
    }

    return if (state.playerActive) {
        // Choose an effect, and recursively simulate with that effect
        var minSpent: Int? = null

        for (effect in effects) {
            if (newEffects.any { it.id == effect.id }) continue
            // Try this effect
            newEffects.add(effect)
            try {
                val newState = State(false, newMana - effect.cost, newPlayerHitPoints, newBossHitPoints, newEffects)
                val result = simulate(newState, bossDamage, cache, hardMode)
                if (result < 0) continue
                val total = result + effect.cost
                if (minSpent == null || total < minSpent) {
                    minSpent = total
                }
            } finally {
                newEffects.removeLast()
            }
        }
        minSpent ?: -1
    } else {
        // Boss deals damage
        val damage = max(1, bossDamage - playerArmor)
        val newState = State(true, newMana, newPlayerHitPoints - damage, newBossHitPoints, newEffects)
        simulate(newState, bossDamage, cache, hardMode)
    }.also { cache[state] = it }
}

fun main() {
    val (bossHitPoints, bossDamage) = parseLines("/2015/day22.txt") { line ->
        line.split(" ").mapNotNull { it.toIntOrNull() }.single()
    }
    println(simulate(State(true, 500, 50, bossHitPoints, emptyList()), bossDamage, hashMapOf(), false))
    println(simulate(State(true, 500, 50, bossHitPoints, emptyList()), bossDamage, hashMapOf(), true))
}