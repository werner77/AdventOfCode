package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Test

class Day3Test {

    @Test
    fun calculateOverlap() {
        val claims = parseLines("/2018/day3.txt") {
            Day3.Claim.fromString(it)
        }
        val result = Day3().calculateOverlap(claims)
        println("Overlap: ${result}")
    }

    @Test
    fun getNonOverlappingClaim() {
        val claims = parseLines("/2018/day3.txt") {
            Day3.Claim.fromString(it)
        }
        val result = Day3().getNonOverlappingClaim(claims)
        println("Non overlapping: ${result}")
    }
}