package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day12Test {

    private fun parseInput(string: String): Pair<List<Coordinate3D>, List<Coordinate3D>> {

        val lines = string.split("\n")

        val coordinates = lines.map {
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
        val input = parseInput(read("/day12.txt"))
        val result = day12.getTotalEnergy(input.first, input.second)
        println(result)
        assertEquals(6735, result)
    }

    @Test
    fun puzzle2() {

        val day12 = Day12()
        val input = parseInput(read("/day12.txt"))

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
        val inputString = "<x=-1, y=0, z=2>\n" +
                "<x=2, y=-10, z=-7>\n" +
                "<x=4, y=-8, z=8>\n" +
                "<x=3, y=5, z=-1>"
        val day12 = Day12()

        val input = parseInput(inputString)

        val result = day12.getTotalEnergy(input.first, input.second, 10)
        println(result)
        assertEquals(179, result)
    }
}