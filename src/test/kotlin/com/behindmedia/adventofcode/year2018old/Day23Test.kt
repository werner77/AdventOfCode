package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.read
import org.junit.Test
import kotlin.test.assertEquals

class Day23Test {

    @Test
    fun puzzle1() {
        val day23 = Day23()
        val count = day23.strongestCount(read("/2018/day23.txt"))
        println(count)
        assertEquals(248, count)
    }

    @Test
    fun puzzle2() {
        val day23 = Day23()
        val point = day23.bestPoint(read("/2018/day23.txt"))
        println(point)
        println(Coordinate3D.origin.manhattenDistance(point))
        assertEquals(124623002, Coordinate3D.origin.manhattenDistance(point))
    }

    @Test
    fun findBestPoint() {
        val day23 = Day23()

        val input = """
            pos=<10,12,12>, r=2
            pos=<12,14,12>, r=2
            pos=<16,12,12>, r=4
            pos=<14,14,14>, r=6
            pos=<50,50,50>, r=200
            pos=<10,10,10>, r=5
        """.trimIndent()

        val point = day23.bestPoint(input)
        assertEquals(Coordinate3D(12,12,12), point)
    }
}