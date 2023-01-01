package com.behindmedia.adventofcode.year2018.day24

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2018.day24.Army.ImmuneSystem
import kotlin.math.*

private enum class Army {
   ImmuneSystem, Infection
}

private data class Group(val number: Int, val initialUnitCount: Int, val hitPoints: Int, val weaknesses: Set<String>, val immunities: Set<String>, val attackDamage: Int, val attackType: String, val initiative: Int, val army: Army): Comparable<Group> {
    var unitCount: Int = initialUnitCount
    val effectivePower: Int
        get() = unitCount * attackDamage

    override fun compareTo(other: Group): Int {
        return compare(
            { other.effectivePower.compareTo(this.effectivePower) },
            { other.initiative.compareTo(this.initiative) }
        )
    }

    fun damageTo(other: Group): Int {
        if (other.army == this.army) return Int.MIN_VALUE
        if (other.immunities.contains(attackType)) {
            return 0
        }
        var damage = effectivePower
        if (other.weaknesses.contains(attackType)) {
            damage *= 2
        }
        return damage
    }

    fun reset(): Group {
        this.unitCount = initialUnitCount
        return this
    }

    companion object {
        fun attackComparator(attacker: Group): Comparator<Group> = Comparator<Group> { first, second ->
            attacker.damageTo(second).compareTo(attacker.damageTo(first))
        }.thenComparing { first, second ->
            second.effectivePower.compareTo(first.effectivePower)
        }.thenComparing { first, second ->
            second.initiative.compareTo(first.initiative)
        }
    }
}

fun main() {
    val (immune, infection) = read("/2018/day24.txt").split("\n\n")

    val groups = mutableListOf<Group>()
    groups += parseGroups(immune, Army.ImmuneSystem)
    groups += parseGroups(infection, Army.Infection)

    part1(groups)
    part2(groups)
}

private fun parseGroups(
    input: String,
    army: Army
): List<Group> {
    val regex = """(\d+) units each with (\d+) hit points (.*)with an attack that does (\d+) ([a-z]+) damage at initiative (\d+)""".toRegex()
    val immuneRegex = """immune to ([a-z, ]+)""".toRegex()
    val weakToRegex = """weak to ([a-z, ]+)""".toRegex()
    var first = true
    var number = 1
    val immuneSystem = input.split("\n").mapNotNull { line ->
        if (first || line.isBlank()) {
            first = false
            null
        } else {
            regex.matchEntire(line)?.destructured?.let { (unitCount, hitPoints, weaknessesImmunitiesString, attackDamage, attackType, initiative) ->
                val immunities: Set<String> =
                    immuneRegex.find(weaknessesImmunitiesString)?.destructured?.let { (immuneTo) ->
                        immuneTo.split(", ").map { it }.toSet()
                    } ?: emptySet()
                val weaknesses: Set<String> =
                    weakToRegex.find(weaknessesImmunitiesString)?.destructured?.let { (weakTo) ->
                        weakTo.split(", ").map { it }.toSet()
                    } ?: emptySet()

                Group(
                    number++,
                    unitCount.toInt(),
                    hitPoints.toInt(),
                    weaknesses,
                    immunities,
                    attackDamage.toInt(),
                    attackType,
                    initiative.toInt(),
                    army
                )
            } ?: error("Could not match line: $line")
        }
    }
    return immuneSystem
}

private fun List<Group>.withBoost(boost: Int): List<Group> {
    return this.map {
        if (it.army == ImmuneSystem) {
            it.copy(attackDamage = it.attackDamage + boost).reset()
        } else {
            it.reset()
        }
    }
}

private fun part1(groups: List<Group>) {
    val ans = simulate(groups)
    println(ans!!.second)
}

private fun part2(groups: List<Group>) {
    var lastResult: Pair<Army, Int>? = null
    val ans = search(lowerBound = 1, upperBound = 100_000, evaluation = { boost ->
        lastResult = simulate(groups.withBoost(boost))
        if (lastResult?.first == ImmuneSystem) {
            1
        } else {
            -1
        }
    })
    println("Boost: $ans")
    println("Units remaining: ${lastResult?.second}")
}

private fun search(
    lowerBound: Int,
    upperBound: Int,
    evaluation: (Int) -> Int
): Int? {
    var begin = lowerBound
    var end = upperBound
    var result: Int? = null
    while (begin <= end) {
        val mid = (begin + end) / 2
        if (evaluation(mid) < 0) {
            begin = mid + 1
        } else {
            result = mid
            end = mid - 1
        }
    }
    return result
}

private fun simulate(groups: List<Group>): Pair<Army, Int>? {
    val remainingGroups = groups.toMutableList()
    while (remainingGroups.map { it.army }.distinct().size > 1) {
        remainingGroups.sort()
        val targets = remainingGroups.toMutableSet()
        val pairs = mutableListOf<Pair<Group, Group>>()
        for (g in remainingGroups) {
            val comparator = Group.attackComparator(g)
            val sortedTargets = targets.sortedWith(comparator)
            val targetGroup = sortedTargets.first()
            if (g.damageTo(targetGroup) <= 0) {
                // No target
                continue
            }
            pairs.add(Pair(g, targetGroup))
            targets.remove(targetGroup)
        }
        pairs.sortByDescending { it.first.initiative }
        var totalUnitsKilled = 0
        for ((attacker, defender) in pairs) {
            if (attacker.unitCount <= 0) {
                continue
            }
            val damage = attacker.damageTo(defender)
            val unitsKilled = min(defender.unitCount, damage / defender.hitPoints)
            defender.unitCount -= unitsKilled
            totalUnitsKilled += unitsKilled
        }
        remainingGroups.removeAll { it.unitCount <= 0 }
        if (totalUnitsKilled == 0) {
            return null
        }
    }
    return Pair(remainingGroups.map {it.army }.distinct().single(), remainingGroups.sumOf { it.unitCount })
}