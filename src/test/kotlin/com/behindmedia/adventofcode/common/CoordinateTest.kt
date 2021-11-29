package com.behindmedia.adventofcode.common

import kotlin.test.Test
import kotlin.test.assertEquals

class CoordinateTest {

    @Test
    fun coordinateRepresentation() {

        val testPoints = mapOf<Int, Int>(
            -1 to -1,
            0 to 1,
            1 to 0,
            0 to 0,
            1 to 1,
            -1 to 1,
            1 to -1,
            Int.MAX_VALUE to Int.MAX_VALUE,
            Int.MIN_VALUE to Int.MAX_VALUE,
            Int.MIN_VALUE to Int.MIN_VALUE,
            Int.MAX_VALUE to Int.MIN_VALUE
        )

        for ((x, y) in testPoints) {
            val coordinate = Coordinate(x, y)
            assertEquals(x, coordinate.x)
            assertEquals(y, coordinate.y)
        }
    }
}