package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.range

class Day19 {

    /**
     * Class describing a row of the beam (all points of the beam with the same y coordinate)
     */
    data class BeamRow(val x: Int, var length: Int) {

        val minX: Int
            get() = x

        val maxX: Int
            get() = x + length - 1

        val range: ClosedRange<Int>
            get() = minX..maxX

        fun contains(otherX: Int): Boolean {
            return otherX in range
        }
    }

    fun numberOfBeamPoints(program: String, squareSize: Int): Int {
        val map = mutableMapOf<Coordinate, Long>()
        val initialState = Computer.parseEncodedState(program)

        while (true) {
            for (x in 0 until squareSize) {
                for (y in 0 until squareSize) {
                    val computer = Computer(initialState)
                    val coordinate = Coordinate(x, y)
                    val result = computer.process(listOf(x.toLong(), y.toLong()))

                    map[coordinate] = result.lastOutput
                }
            }
            break
        }

        printMap(map)
        return map.values.sum().toInt()
    }

    private fun printMap(map: Map<Coordinate, Long>) {
        val range = map.keys.range()
        for (coordinate in range) {
            val state = map[coordinate] ?: 0L

            val s = if (state == 0L) "." else "#"
            print(s)

            if (coordinate.x == range.endInclusive.x) {
                println()
            }
        }
    }

    fun findSquareLocation(program: String, squareSize: Int): Coordinate {
        val list = mutableMapOf<Int, BeamRow>()
        val initialState = Computer.parseEncodedState(program)

        // Inspecting the map we see the location of the beam in x and y sense always increases
        // So we can simplify

        var y = 0
        var lastRow = BeamRow(0,0)

        // Loop over y
        loop_y@ while (true) {
            var x = lastRow.x

            // Loop over x
            loop_x@ while (true) {
                val computer = Computer(initialState)
                val result = computer.process(listOf(x.toLong(), y.toLong()))
                if (result.lastOutput == 1L) {
                    // Found beam point
                    val existingRow = list.getOrPut(y) {
                        BeamRow(x, 0)
                    }
                    existingRow.length++
                } else {
                    // Found no beam point
                    val existingRow = list[y]

                    if (existingRow != null) {
                        lastRow = existingRow

                        val minY = y - squareSize + 1
                        val maxX = existingRow.minX + squareSize - 1

                        list[minY]?.let {
                            if (it.contains(existingRow.minX) && it.contains(maxX)) {
                                return Coordinate(existingRow.minX, minY)
                            }
                        }
                        break@loop_x
                    } else if (x > lastRow.maxX + 2) {
                        // This row does not have any beam lines
                        break@loop_x
                    }
                }
                x++
            }
            y++
        }
    }
}