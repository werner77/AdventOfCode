package com.behindmedia.adventofcode.year2018.day13

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.RotationDirection.Left
import com.behindmedia.adventofcode.common.RotationDirection.Right
import com.behindmedia.adventofcode.common.parseMap

private data class Cart(
    val id: Int,
    private var coordinate: Coordinate,
    private var facingDirection: Coordinate,
    private var rotationIndex: Int = 0,
    var crashed: Boolean = false
) {
    val currentCoordinate: Coordinate
        get() = coordinate

    private fun rotateForCrossing(): Boolean {
        return (rotations[rotationIndex++ % 3])?.let { facingDirection = facingDirection.rotate(it) } != null
    }

    private fun move(): Coordinate {
        coordinate += facingDirection
        return coordinate
    }

    fun moveAndRotate(map: Map<Coordinate, Char>) {
        val newCoordinate = move()
        rotateIfNeeded(map[newCoordinate] ?: error("Location not found on map: $newCoordinate"))
    }

    private fun rotateIfNeeded(d: Char): Boolean {
        return when (d) {
            '+' -> rotateForCrossing()
            '\\' -> {
                facingDirection = when (facingDirection) {
                    Coordinate.left -> Coordinate.up
                    Coordinate.right -> Coordinate.down
                    Coordinate.down -> Coordinate.right
                    Coordinate.up -> Coordinate.left
                    else -> error("Invalid direction")
                }
                true
            }

            '/' -> {
                facingDirection = when (facingDirection) {
                    Coordinate.left -> Coordinate.down
                    Coordinate.right -> Coordinate.up
                    Coordinate.down -> Coordinate.left
                    Coordinate.up -> Coordinate.right
                    else -> error("Invalid direction")
                }
                true
            }

            else -> false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cart

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


    companion object {
        private val rotations = listOf(Left, null, Right)
    }
}

fun main() {
    val map = parseMap("/2018/day13.txt") {
        it
    }
    var id = 1
    val carts = map.entries.filter { it.value in setOf('<', '>', '^', 'v') }.map {
        val direction = when (it.value) {
            '<' -> Coordinate.left
            '>' -> Coordinate.right
            '^' -> Coordinate.up
            'v' -> Coordinate.down
            else -> error("Invalid direction: ${it.value}")
        }
        Cart(id = id++, coordinate = it.key, facingDirection = direction)
    }
    // Replace the robot location with corresponding map characters
    val mapWithoutRobots = map.mapValues {
        when (it.value) {
            in setOf('<', '>') -> {
                '-'
            }

            in setOf('^', 'v') -> {
                '|'
            }

            else -> {
                it.value
            }
        }
    }
    part1(carts, mapWithoutRobots)
    part2(carts, mapWithoutRobots)
}

private fun part1(
    carts: List<Cart>,
    mapWithoutRobots: Map<Coordinate, Char>
) {
    val coordinate = simulate(carts, mapWithoutRobots) {
        if (it.size < carts.size) {
            val crashedCarts = (carts - it)
            crashedCarts.first().currentCoordinate
        } else {
            null
        }
    }
    println(coordinate)
}

private fun part2(
    carts: List<Cart>,
    mapWithoutRobots: Map<Coordinate, Char>
) {
    val c = simulate(carts, mapWithoutRobots) {
        if (it.size == 1) {
            it.single().currentCoordinate
        } else {
            null
        }
    }
    println(c)
}

private fun <T> simulate(
    carts: List<Cart>,
    mapWithoutRobots: Map<Coordinate, Char>,
    exitPredicate: (List<Cart>) -> T?
): T {
    while (true) {
        val remainingCarts = carts.filter { !it.crashed }.sortedBy { it.currentCoordinate }
        val result = exitPredicate.invoke(remainingCarts)
        if (result != null) return result
        for (cart in remainingCarts) {
            if (cart.crashed) continue
            // Move in the direction it is currently facing
            cart.moveAndRotate(mapWithoutRobots)
            val collisionCart = remainingCarts.firstOrNull {
                cart.currentCoordinate == it.currentCoordinate &&
                        cart != it &&
                        !cart.crashed
            }
            if (collisionCart != null) {
                cart.crashed = true
                collisionCart.crashed = true
            }
        }
    }
}