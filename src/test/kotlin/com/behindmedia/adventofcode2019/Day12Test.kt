package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*

class Day12Test {

    @Test
    fun puzzle1() {

        val day12 = Day12()

        /*
        <x=15, y=-2, z=-6>
<x=-5, y=-4, z=-11>
<x=0, y=-6, z=0>
<x=5, y=9, z=6>
         */

        val initialCoordinates = listOf(
            Coordinate3D(15, -2, -6),
            Coordinate3D(-5, -4, -11),
            Coordinate3D(0, -6, 0),
            Coordinate3D(5, 9, 6)
        )
        val initialVelocities = listOf (
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0)
        )

        val result = day12.simulate(initialCoordinates, initialVelocities)
        println(result)
    }

    @Test
    fun sample1() {

        val day12 = Day12()

        /*
        <x=15, y=-2, z=-6>
<x=-5, y=-4, z=-11>
<x=0, y=-6, z=0>
<x=5, y=9, z=6>
         */

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

        val result = day12.simulate(initialCoordinates, initialVelocities, 10)
        println(result)
    }

    @Test
    fun statesEqual() {
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
        val initialVelocities1 = listOf (
            Coordinate3D(1, 0, 0),
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0),
            Coordinate3D(0, 0, 0)
        )

        val state1 = Day12.State(initialCoordinates, initialVelocities)
        val state2 = Day12.State(initialCoordinates, initialVelocities)

        assertTrue(state1 == state2)

        val state3 = Day12.State(initialCoordinates, initialVelocities1)

        assertFalse(state1 == state3)


        // Now determine if the state is the same by offsetting only the intial coordinates
    }
}