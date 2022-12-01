package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.printMap
import com.behindmedia.adventofcode.common.read
import org.junit.Test

class Day20Test {

    @Test
    fun parseInput() {
        val day20 = Day20()
        val input = """^ENWWW(NEEE|SSE(EE|N))$"""
        val map = day20.parse(input)
        map.printMap(default='#')

        /*

        #########
        #.|.|.|.#
        #-#######
        #.|.|.|.#
        #-#####-#
        #.#.#X|.#
        #-#-#####
        #.|.|.|.#
        #########

         */
    }

    @Test
    fun puzzle1() {
        val day20 = Day20()
        val map = day20.parse(read("/2018/day20.txt"))
        val shortestPaths = day20.shortestPaths(map=map)
        val maxPathLength = shortestPaths.maxByOrNull { it.pathLength }!!.pathLength

        // Only count doors
        println(maxPathLength/2)
    }

    @Test
    fun puzzle2() {
        val day20 = Day20()
        val map = day20.parse(read("/2018/day20.txt"))
        val shortestPaths = day20.shortestPaths(map=map)

        val count = shortestPaths.count { it.pathLength >= 2 * 1000 }
        println(count)
    }
}