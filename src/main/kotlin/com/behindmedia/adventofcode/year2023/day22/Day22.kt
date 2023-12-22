package com.behindmedia.adventofcode.year2023.day22

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.CoordinateRange3D
import com.behindmedia.adventofcode.common.DefaultMap
import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.parseLinesWithIndex

data class Brick(val index: Int, val minCoordinate: Coordinate3D, val maxCoordinate: Coordinate3D) {
    val range: CoordinateRange3D
        get() = CoordinateRange3D(minCoordinate, maxCoordinate)
    val minZ: Long
        get() = minCoordinate.z
    fun planeRange(z: Long): CoordinateRange3D {
        return CoordinateRange3D(minCoordinate.copy(z = z), maxCoordinate.copy(z = z))
    }
}

fun main() {
    val comparator: Comparator<Brick> = Comparator<Brick> { first, second ->
        first.minCoordinate.z.compareTo(second.minCoordinate.z)
    }.thenComparing { first, second ->
        first.maxCoordinate.z.compareTo(second.maxCoordinate.z)
    }
    val bricks = parseLinesWithIndex("/2023/day22.txt") { brickIndex, line ->
        val (left, right) = line.split("~")
        val (x1, y1, z1) = left.split(",").map { it.toLong() }
        val (x2, y2, z2) = right.split(",").map { it.toLong() }
        Brick(brickIndex, Coordinate3D(x1, y1, z1), Coordinate3D(x2, y2, z2))
    }.sortedWith(comparator).associateBy { it.index }

    val map = bricks.values.fold(mutableMapOf<Coordinate3D, Int>()) { m, b ->
        m.apply { settle(b) }
    }

    // Look in the z direction if any of the other bricks touches it
    val above = defaultMutableMapOf<Int, MutableSet<Int>>(putValueImplicitly = true) { mutableSetOf() }
    val below = defaultMutableMapOf<Int, MutableSet<Int>>(putValueImplicitly = true) { mutableSetOf() }
    for ((coordinate, index) in map) {
        // look at coordinate above and below
        map[coordinate + Coordinate3D.zIdentity].takeIf { it != index }?.let {
            above[index] += it
        }
        map[coordinate - Coordinate3D.zIdentity].takeIf { it != index }?.let {
            below[index] += it
        }
    }

    // Part 1
    // Get the tiles on which at least one other tile is fully dependent
    val cannotRemove = bricks.keys.fold(mutableSetOf<Int>()) { set, index ->
        below[index].singleOrNull()?.let { set += it }
        set
    }
    println(bricks.size - cannotRemove.size)

    // Part 2
    println(bricks.keys.sumOf { countFallen(it, below, above) })
}

private fun countFallen(index: Int, below: DefaultMap<Int, out Set<Int>>, above: DefaultMap<Int, out Set<Int>>): Int {
    val pending = ArrayDeque<Int>()
    pending += index
    val fallen = mutableSetOf<Int>()
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        fallen += current
        for (brick in above[current]) {
            val remaining = below[brick] - fallen
            if (remaining.isEmpty()) {
                // If there are no bricks below remaining: add to pending
                pending += brick
            }
        }
    }
    // subtract one for current brick, only count other bricks
    return fallen.size - 1
}

private fun MutableMap<Coordinate3D, Int>.settle(brick: Brick) {
    var offset = 0L
    while(true) {
        val floorZ = (brick.minZ - offset - 1).takeIf { it > 0L } ?: break
        if (brick.planeRange(z = floorZ).any { it in this }) break
        offset++
    }
    // Settle the brick at the selected z-offset
    brick.range.map { it - Coordinate3D.zIdentity * offset }.forEach {
        require(it !in this)
        this[it] = brick.index
    }
}