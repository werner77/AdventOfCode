package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.printMatrix
import org.junit.Assert.assertEquals
import org.junit.Test

class Day11Test {

    @Test
    fun calculatePower() {
        val result = Day11().calculatePower(Day11.Coordinate(3, 5), 8)
        assertEquals(4, result)
    }

    @Test
    fun calculateMax1() {
        val result = Day11().calculateMax(18)
        assertEquals(Day11.Coordinate(33, 45), result)
    }

    @Test
    fun calculateMax2() {
        val result = Day11().calculateMax(42)
        assertEquals(Day11.Coordinate(21, 61), result)
    }

    @Test
    fun calculateMax() {
        val result = Day11().calculateMax(3628)
        println("Result: ${result}")
    }

    @Test
    fun calculateAnyMaxSimple() {
        val result = Day11().calculateAnyMax(18)

        assertEquals(Day11.Coordinate(90, 269), result.first)
        assertEquals(16, result.second)
    }

    @Test
    fun calculateAnyMax() {
        val result = Day11().calculateAnyMax(3628)

        println("${result.first.x},${result.first.y},${result.second}")
    }

    @Test
    fun blockSumSimple() {
        val day11 = Day11()
        val gridSize = 5
        val blockSize = 3
        val serialNumber = 0

        val valueProducer: (Int, Int, Int) -> Int = { _, _, _ ->
            1
        }

        val values = day11.valueMatrix(gridSize, serialNumber, valueProducer)

        println("Values:")
        println("------------------")
        values.printMatrix()
        println("------------------")

        val prefixSums = day11.prefixSumMatrix(gridSize, serialNumber, valueProducer)

        println("PrefixSums:")
        println("------------------")
        prefixSums.printMatrix()
        println("------------------")

        for (x in 1..(gridSize - blockSize + 1)) {
            for (y in 1..(gridSize - blockSize + 1)) {
                val expectedValue = day11.sumForBlockReference(x, y, blockSize, values)
                val actualValue = day11.sumForBlock(x, y, blockSize, prefixSums)
                assertEquals("Failed for (x, y)=(${x}, ${y})", expectedValue, actualValue)
            }
        }
    }


}