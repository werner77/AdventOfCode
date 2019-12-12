package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day12Test {

    private fun parseInput(string: String): Pair<List<Coordinate3D>, List<Coordinate3D>> {

        val lines = string.split("\n")

        val coordinates = lines.map {
            val components = it.split('<', '>', '=', ',', ' ', 'x', 'y', 'z')
                .filter { s -> s.isNotEmpty() }
            assert(components.size == 3)
            Coordinate3D(components[0].toInt(), components[1].toInt(), components[2].toInt())
        }

        val velocities = List<Coordinate3D>(coordinates.size) {
            Coordinate3D.origin
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
        val result = day12.findPeriod(input.first, input.second)
        println(result)
        assertEquals(326489627728984L, result)
    }

    @Test
    fun totalEnergySample1() {
        val inputString = """
        <x=-1, y=0, z=2>
        <x=2, y=-10, z=-7>
        <x=4, y=-8, z=8>
        <x=3, y=5, z=-1>
        """.trimIndent()

        val day12 = Day12()
        val input = parseInput(inputString)
        val result = day12.getTotalEnergy(input.first, input.second, 10)
        println(result)
        assertEquals(179, result)
    }

    @Test
    fun totalEnergySample2() {
        val inputString = """
        <x=-8, y=-10, z=0>
        <x=5, y=5, z=10>
        <x=2, y=-7, z=3>
        <x=9, y=-8, z=-3>
        """.trimIndent()

        val day12 = Day12()
        val input = parseInput(inputString)
        val result = day12.getTotalEnergy(input.first, input.second, 100)
        println(result)
        assertEquals(1940, result)
    }

    @Test
    fun periodSample1() {
        val inputString = """
        <x=-1, y=0, z=2>
        <x=2, y=-10, z=-7>
        <x=4, y=-8, z=8>
        <x=3, y=5, z=-1>
        """.trimIndent()

        val day12 = Day12()
        val input = parseInput(inputString)
        val result = day12.findPeriod(input.first, input.second)
        println(result)
        assertEquals(2772, result)
    }
}