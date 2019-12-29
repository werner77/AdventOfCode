package com.behindmedia.adventofcode.year2018

import org.junit.Test

class Day3Test {

    @Test
    fun calculateOverlap() {
        val claims = parseList("/2018/day3.txt") {
            Day3.Claim.fromString(it)
        }
        val result = Day3().calculateOverlap(claims)
        println("Overlap: ${result}")
    }

    @Test
    fun getNonOverlappingClaim() {
        val claims = parseList("/2018/day3.txt") {
            Day3.Claim.fromString(it)
        }
        val result = Day3().getNonOverlappingClaim(claims)
        println("Non overlapping: ${result}")
    }
}