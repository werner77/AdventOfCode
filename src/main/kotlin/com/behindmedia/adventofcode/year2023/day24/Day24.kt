package com.behindmedia.adventofcode.year2023.day24

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.LongCoordinate
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.timing
import kotlin.math.min
import kotlin.math.sign
import java.math.BigInteger

fun main() {
    val stones = parseLines("/2023/day24.txt") { line ->
        val (first, second) = line.split("@")
        val (x, y, z) = first.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        val (vx, vy, vz) = second.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        Stone(Coordinate3D(x, y, z), Coordinate3D(vx, vy, vz))
    }

    timing {
        // Part 1
        part1(stones)

        // Part 2
        part2(stones)
    }
}

private fun part1(stones: List<Stone>) {
    val validRange = 200000000000000L..400000000000000L
    var ans = 0L
    processPairs(stones, 2) { intersection ->
        if (intersection != null && intersection.x in validRange && intersection.y in validRange) {
            ans++
        }
        true
    }
    println(ans)
}

private fun solve(first: ProjectedStone, second: ProjectedStone): LongCoordinate? {
    return solve(first.a, first.b, first.c, second.a, second.b, second.c)
}

// Solve two linear equations for x and y
// Equations of the form: ax + by = c
private fun solve(a1: BigInteger, b1: BigInteger, c1: BigInteger, a2: BigInteger, b2: BigInteger, c2: BigInteger): LongCoordinate? {
    // a1*x + b1*y = c1
    // a2*x + b2*y = c2

    // b2*a1*x + b2*b1*y = b2*c1
    // b1*a2*x + b2*b1*y = b1*c2

    // x(b2*a1 - b1*a2) = b2*c1 - b1*c2

    // x = (b2*c1 - b1*c2) / (b2*a1 - b1*a2)

    // let d = (b2*a1 - b1*a2)

    // if d == 0: lines are parallel and there is no solution

    val d = b2 * a1 - b1 * a2
    if (d == BigInteger.ZERO) return null
    val x = (b2 * c1 - b1 * c2) / d
    val y = (c2 * a1 - c1 * a2) / d
    return LongCoordinate(x.toLong(), y.toLong())
}

/**
 * Stone projected unto some axis (basically eliminating the projected axis, being x, y or z).
 */
private data class ProjectedStone(val position: LongCoordinate, val velocity: LongCoordinate) {
    val a: BigInteger = velocity.y.toBigInteger()
    val b: BigInteger = -velocity.x.toBigInteger()
    val c: BigInteger = (velocity.y.toBigInteger() * position.x.toBigInteger() - velocity.x.toBigInteger() * position.y.toBigInteger())

    /**
     * Adds the specified velocity to this projected stone
     */
    fun addingVelocity(delta: LongCoordinate): ProjectedStone {
        return copy(velocity = velocity + delta)
    }

    override fun toString(): String = "$position @ $velocity"
}

private data class Stone(val position: Coordinate3D, val velocity: Coordinate3D) {
    fun projected(component: Int): ProjectedStone {
        // Take all components except the specified component
        return when (component) {
            0 -> ProjectedStone(position = LongCoordinate(position.y, position.z), velocity = LongCoordinate(velocity.y, velocity.z))
            1 -> ProjectedStone(position = LongCoordinate(position.x, position.z), velocity = LongCoordinate(velocity.x, velocity.z))
            2 -> ProjectedStone(position = LongCoordinate(position.x, position.y), velocity = LongCoordinate(velocity.x, velocity.y))
            else -> error("Invalid component: $component")
        }
    }

    override fun toString(): String = "$position @ $velocity"
}

/**
 * Processes all pairs of stones by projecting them unto the specified component (0 == x, 1 == y, 2 == z).
 *
 * Optionally a delta velocity is applied to each stone.
 *
 * If the processing block returns false this function immediately exits
 */
private fun processPairs(stones: List<Stone>, projectedComponent: Int, deltaSpeed: LongCoordinate = LongCoordinate.origin, process: (LongCoordinate?) -> Boolean) {
    for (i in 0 until stones.size) {
        for (j in i + 1 until stones.size) {
            val firstStone = stones[i].projected(projectedComponent).addingVelocity(deltaSpeed)
            val secondStone = stones[j].projected(projectedComponent).addingVelocity(deltaSpeed)
            val intersection = solve(firstStone, secondStone)?.takeIf { p ->
                listOf(firstStone, secondStone).all { (p.y - it.position.y).sign == it.velocity.y.sign }
            }
            if (!process(intersection)) return
        }
    }
}

/**
 * Searches for multiple intersection position using the specified projected component (x, y or z-axis).
 *
 * Brute forces over combinations of vx, vy to find a possible solution.
 *
 * The key insight is that a minus delta velocity can be applied to any stone and assume the rock to remain stationary (speed zero).
 * Because the rock has to collide with every stone, the stone paths should all have an intersection (which is the position of the rock).
 *
 * Returns a pair of position to velocity of the rock found for the projection, or null if no solution could be found.
 */
private fun findRockPositionAndVelocity(stones: List<Stone>, component: Int): Pair<LongCoordinate, LongCoordinate>? {
    val maxValue = 400L
    val minResultCount = 5
    for (vx in -maxValue..maxValue) {
        for (vy in -maxValue..maxValue) {
            val deltaV = LongCoordinate(vx, vy)
            val matchingPositions = mutableSetOf<LongCoordinate>()
            var resultCount = 0
            processPairs(stones, component, deltaV) { intersection ->
                if (intersection != null) {
                    matchingPositions += intersection
                    resultCount++
                    resultCount < minResultCount
                } else {
                    false
                }
            }
            // We need exactly 1 position with at least minResultCount matches
            if (matchingPositions.size == 1 && resultCount >= min(minResultCount, stones.size / 2)) {
                return matchingPositions.single() to -deltaV
            }
        }
    }
    return null
}

private fun part2(stones: List<Stone>) {
    // Project to z-axis
    val result1 = findRockPositionAndVelocity(stones = stones, component = 2) ?: error("Could not find result")

    // Project to x-axis
    val result2 = findRockPositionAndVelocity(stones = stones, component = 0) ?: error("Could not find result")

    // Project to y-axis
    val result3 = findRockPositionAndVelocity(stones = stones, component = 1) ?: error("Could not find result")

    val (x1, y1) = result1.first
    val (y2, z1) = result2.first
    val (x2, z2) = result3.first
    val (vx1, vy1) = result1.second
    val (vy2, vz1) = result2.second
    val (vx2, vz2) = result3.second

    require(y1 == y2 && x1 == x2 && z1 == z2) {
        "Expected positions to match"
    }
    require(vy1 == vy2 && vx1 == vx2 && vz1 == vz2) {
        "Expected velocities to match"
    }

    println("Found rock position and velocity: $x1,$y1,$z1 @ $vx1,$vy1,$vz1")

    println(x1 + y1 + z1)
}
