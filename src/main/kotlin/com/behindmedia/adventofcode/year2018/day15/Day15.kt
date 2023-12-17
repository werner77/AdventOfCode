package com.behindmedia.adventofcode.year2018.day15

import com.behindmedia.adventofcode.common.*
import kotlin.math.max

fun main() {
    val map = parseMap("/2018/day15.txt") { it }
    part1(map)
    part2(map)
}

private fun part1(map: Map<Coordinate, Char>) {
    val (completedRounds, actors) = simulate(map)
    println("---------- Part 1 ----------")
    printMap(map, actors)
    println("Number of rounds: $completedRounds")
    printState(actors)
    val outcome = completedRounds * actors.values.sumOf { it.hitPoints }
    println("Outcome: $outcome")
}

private fun part2(map: Map<Coordinate, Char>) {
    println("---------- Part 2 ----------")
    val initialNumberOfElves = map.count { it.value == 'E' }
    var attackPower = 4
    while(true) {
        val (completedRounds, actors) = simulate(map, attackPower)
        val success = actors.count { it.value.type == 'E' } == initialNumberOfElves
        if (success) {
            val outcome = completedRounds * actors.values.sumOf { it.hitPoints }
            printMap(map, actors)
            println("Number of rounds: $completedRounds")
            println("Minimum elf attack power needed: $attackPower")
            println("Outcome: $outcome")
            break
        }
        attackPower++
    }
}

private fun simulate(startMap: Map<Coordinate, Char>, elfAttackPower: Int = 3): Pair<Int, Map<Coordinate, Actor>> {
    val actors = startMap
        .filter { it.value in listOf('E', 'G') }
        .map { it.key to Actor(type = it.value, attackPower = if (it.value == 'E') elfAttackPower else 3) }
        .toMap(mutableMapOf())
    val map = startMap.mapValues { (_, value) -> if (value in listOf('E', 'G')) '.' else value }
    var completedRounds = 0
    while(true) {
        var combatFinished = false
        for (current in actors.entries.sortedBy { it.key }) {
            val (c, actor) = current
            if (actors[c] == null || actor.hitPoints == 0) continue
            if (!performTurn(current.toPair(), map, actors)) {
                combatFinished = true
                break
            }
        }
        if (combatFinished) {
            break
        }
        completedRounds++
    }
    return completedRounds to actors
}

private fun performTurn(current: Pair<Coordinate, Actor>, map: Map<Coordinate, Char>, actors: MutableMap<Coordinate, Actor>): Boolean {
    var (coordinate, actor) = current
    val candidateTargets = mutableSetOf<Coordinate>()
    val currentType = actor.type
    for ((c, a) in actors) {
        if (a.type != currentType) {
            for (neighbour in c.directNeighbours) {
                candidateTargets += neighbour
            }
        }
    }
    if (candidateTargets.isEmpty()) return false
    val moveCoordinate = findMoveCoordinate(coordinate, map, actors, candidateTargets)
    if (moveCoordinate != null) {
        actors.remove(coordinate)
        actors[moveCoordinate] = actor
        coordinate = moveCoordinate
    }
    val attackCandidate = findAttackCandidate(Pair(coordinate, actor), actors)
    if (attackCandidate != null) {
        if (executeAttack(actor, attackCandidate)) {
            actors.remove(attackCandidate.first)
        }
    }
    return true
}

private fun printMap(map: Map<Coordinate, Char>, actors: Map<Coordinate, Actor>) {
    map.mapValues {
        actors[it.key]?.type ?: it.value
    }.printMap('.')
}
private fun printState(actors: Map<Coordinate, Actor>) {
    println(actors.entries.sortedBy { it.key }.map { "${it.value.type}(${it.value.hitPoints})" }.joinToString())
}

private fun findMoveCoordinate(current: Coordinate, map: Map<Coordinate, Char>, actors: Map<Coordinate, Actor>, candidateTargets: Set<Coordinate>): Coordinate? {
    val candidatePaths = mutableListOf<Path<Coordinate>>()
    var shortestLength = Int.MAX_VALUE
    shortestPath(
        from = current,
        neighbours = { it.destination.directNeighbours },
        reachable = { _, c -> map[c] == '.' && c !in actors },
        process = { path ->
            if (path.destination in candidateTargets) {
                if (path.length <= shortestLength) {
                    shortestLength = path.length.toInt()
                    candidatePaths += path
                    null
                } else {
                    true
                }
            } else {
                null
            }
        }
    )
    return if (candidatePaths.isEmpty()) {
        // No reachable targets
        null
    } else {
        candidatePaths.mapNotNull { it.allNodes.getOrNull(1) }.minOfOrNull { it }
    }
}

private fun <T: Any>Collection<T>.getOrNull(index: Int): T? {
    val iterator = this.iterator()
    var i = 0
    while (iterator.hasNext()) {
        val current = iterator.next()
        if (i == index) {
            return current
        }
        i++
    }
    return null
}

private data class Actor(val type: Char, val attackPower: Int = 3, var hitPoints: Int = 200) {
}

private fun findAttackCandidate(current: Pair<Coordinate, Actor>, actors: Map<Coordinate, Actor>): Pair<Coordinate, Actor>? {
    val (coordinate, actor) = current
    val currentType = actor.type
    var candidate: Pair<Coordinate, Actor>? = null
    var minHitPoints = Int.MAX_VALUE
    for (c in coordinate.directNeighbours) {
        val target = actors[c] ?: continue
        if (target.type == currentType) continue
        if (candidate == null || target.hitPoints < minHitPoints) {
            candidate = Pair(c, target)
            minHitPoints = target.hitPoints
        } else if (target.hitPoints == minHitPoints && candidate.first > c) {
            candidate = Pair(c, target)
        }
    }
    return candidate
}

private fun executeAttack(current: Actor, target: Pair<Coordinate, Actor>): Boolean {
    target.second.hitPoints = max(0, target.second.hitPoints - current.attackPower)
    return target.second.hitPoints == 0
}