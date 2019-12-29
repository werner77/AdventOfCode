package com.behindmedia.adventofcode.year2018

import org.junit.Test

import org.junit.Assert.*

class Day2Test {

    @Test
    fun calculateChecksum1() {
        val ids = parseList("/2018/day2-test.txt") { it }
        val checksum = Day2().calculateChecksum(ids)
        println("Checksum: ${checksum}")
    }

    @Test
    fun calculateChecksum2() {
        val ids = parseList("/2018/day2.txt") { it }
        val checksum = Day2().calculateChecksum(ids)
        println("Checksum: ${checksum}")
    }

    @Test
    fun findCommon1() {
        val ids = parseList("/2018/day2-test1.txt") { it }
        val common = Day2().findCommon(ids)

        if (common != null) {
            println("Found common data: ${common}")
        } else {
            println("No common data found")
        }

    }

    @Test
    fun findCommon2() {
        val ids = parseList("/2018/day2.txt") { it }
        val common = Day2().findCommon(ids)

        if (common != null) {
            println("Found common data: ${common}")
        } else {
            println("No common data found")
        }

    }
}