package com.behindmedia.adventofcode.year2018old

class Day11 {

    data class Coordinate(val x: Int, val y: Int)

    fun calculatePower(coordinate: Coordinate, serialNumber: Int): Int {
        return calculatePower(coordinate.x, coordinate.y, serialNumber)
    }

    fun calculatePower(x: Int, y: Int, serialNumber: Int): Int {
        val rackId = x + 10
        var powerLevel = rackId * y
        powerLevel += serialNumber
        powerLevel *= rackId
        powerLevel /= 100
        powerLevel %= 10
        powerLevel -= 5
        return powerLevel
    }

    fun calculateMax(serialNumber: Int, blockSize: Int = 3): Coordinate {
        val gridSize = 300
        val prefixSums = prefixSumMatrix(gridSize, serialNumber) { x, y, s ->
            calculatePower(x, y, s)
        }

        val result = calculateMaxImpl(gridSize, blockSize, prefixSums)

        val maxCoordinate = result.first
        val maxAreaSum = result.second

        println("Found coordinate ${maxCoordinate} for an area sum of ${maxAreaSum}")
        return maxCoordinate
    }

    fun calculateAnyMax(serialNumber: Int): Pair<Coordinate, Int> {
        val gridSize = 300
        val prefixSums = prefixSumMatrix(gridSize, serialNumber) { x, y, s ->
            calculatePower(x, y, s)
        }

        var maxCoordinate = Coordinate(0, 0)
        var maxAreaSum = Int.MIN_VALUE
        var maxBlockSize = 0

        for (blockSize in 1..gridSize) {
            val result = calculateMaxImpl(gridSize, blockSize, prefixSums)

            if (result.second > maxAreaSum) {
                maxAreaSum = result.second
                maxBlockSize = blockSize
                maxCoordinate = result.first
            }
        }
        println("Found coordinate ${maxCoordinate} for an area sum of ${maxAreaSum} with blockSize ${maxBlockSize}")
        return Pair(maxCoordinate, maxBlockSize)
    }

    private fun calculateMaxImpl(gridSize: Int, blockSize: Int, prefixSums: Array<IntArray>): Pair<Coordinate, Int> {
        var maxAreaSum = Int.MIN_VALUE
        var maxCoordinate = Coordinate(0, 0)

        for (x in 1..(gridSize - blockSize + 1)) {
            for (y in 1..(gridSize - blockSize + 1)) {
                val areaSum = sumForBlock(x, y, blockSize, prefixSums)
                if (areaSum > maxAreaSum) {
                    maxAreaSum = areaSum
                    maxCoordinate = Coordinate(x, y)
                }
            }
        }
        return Pair(maxCoordinate, maxAreaSum)
    }

    fun valueMatrix(gridSize: Int, serialNumber: Int, valueBlock: (x: Int, y: Int, serialNumber: Int) -> Int): Array<IntArray> {
        val values = Array(gridSize + 1, {IntArray(gridSize + 1)})

        for (x in 1..gridSize) {
            for (y in 1..gridSize) {
                val value = valueBlock(x, y, serialNumber)
                values[x][y] = value
            }
        }
        return values
    }

    fun prefixSumMatrix(gridSize: Int, serialNumber: Int, valueBlock: (x: Int, y: Int, serialNumber: Int) -> Int): Array<IntArray> {
        val prefixSums = Array(gridSize + 1, {IntArray(gridSize + 1)})

        for (x in 1..gridSize) {
            for (y in 1..gridSize) {
                val value = valueBlock(x, y, serialNumber)
                prefixSums[x][y] = prefixSums[x - 1][y] + prefixSums[x][y - 1] - prefixSums[x - 1][y - 1] + value
            }
        }
        return prefixSums
    }

    fun sumForBlockReference(x: Int, y: Int, blockSize: Int, values: Array<IntArray>): Int {
        var sum = 0
        for (i in x..(x + blockSize - 1)) {
            for (j in y..(y + blockSize - 1)) {
                val value = values[i][j]
                sum += value
            }
        }
        return sum
    }

    fun sumForBlock(x: Int, y: Int, blockSize: Int, prefixSums: Array<IntArray>): Int {

        val maxX = x + blockSize - 1
        val maxY = y + blockSize - 1
        val minX = x - 1
        val minY = y - 1

        val area1 = prefixSums[minX][minY]
        val area2 = prefixSums[maxX][minY]
        val area3 = prefixSums[minX][maxY]

        return prefixSums[maxX][maxY] - area2 - area3 + area1
    }
}