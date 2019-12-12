package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day12Test {

    private fun parseInput(): Pair<List<Coordinate3D>, List<Coordinate3D>> {

        val coordinates = parseLines("/day12.txt") {
            val components = it.split("<", ">", "=", ",", " ", "x", "y", "z").filter { it.isNotEmpty() }
            assert(components.size == 3)
            Coordinate3D(components[0].toInt(), components[1].toInt(), components[2].toInt())
        }

        val velocities = List<Coordinate3D>(coordinates.size) {
            Coordinate3D(0, 0, 0)
        }

        return Pair(coordinates, velocities)
    }

    @Test
    fun puzzle1() {
        val day12 = Day12()
        val input = parseInput()
        val result = day12.getTotalEnergy(input.first, input.second)
        println(result)
        assertEquals(6735, result)
    }

    @Test
    fun puzzle2() {

        val day12 = Day12()
        val input = parseInput()

        var lcm = 1L

        for (component in 0 until 3) {
            val period = day12.findPeriod(input.first, input.second, component)
            assert(period.first == 0)
            lcm = leastCommonMultiple(lcm, period.second.toLong())
        }
        println(lcm)
        assertEquals(326489627728984L, lcm)
    }

    @Test
    fun sample1() {
        val day12 = Day12()

        val initialCoordinates = listOf(
            Coordinate3D(-1, 0, 2),
            Coordinate3D(2, -10, -7),
            Coordinate3D(4, -8, 8),
            Coordinate3D(3, 5, -1)
        )
        val initialVelocities = listOf (
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0)
        )

        val result = day12.getTotalEnergy(initialCoordinates, initialVelocities, 10)
        println(result)
    }
}