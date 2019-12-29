package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

class Day2Test {

    @Test
    fun calculateChecksum1() {
        val ids = parseLines("/2018/day2-test.txt") { it }
        val checksum = Day2().calculateChecksum(ids)
        println("Checksum: ${checksum}")
    }

    @Test
    fun calculateChecksum2() {
        val ids = parseLines("/2018/day2.txt") { it }
        val checksum = Day2().calculateChecksum(ids)
        println("Checksum: ${checksum}")
    }

    @Test
    fun findCommon1() {
        val ids = parseLines("/2018/day2-test1.txt") { it }
        val common = Day2().findCommon(ids)

        if (common != null) {
            println("Found common data: ${common}")
        } else {
            println("No common data found")
        }

    }

    @Test
    fun findCommon2() {
        val ids = parseLines("/2018/day2.txt") { it }
        val common = Day2().findCommon(ids)

        if (common != null) {
            println("Found common data: ${common}")
        } else {
            println("No common data found")
        }

    }
}