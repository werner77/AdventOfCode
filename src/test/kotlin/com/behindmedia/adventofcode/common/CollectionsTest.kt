package com.behindmedia.adventofcode.common

import org.junit.Test

import org.junit.Assert.*

class CollectionsTest {
    @Test
    fun permute() = timing {
        val n = 10
        for (k in 1..10) {

            println("Testing $n over $k")

            val list = (0 until n).toList()

            var seen = 0

            list.permute(count = k, mode = PermuteMode.Unique) {
                seen++
                null
            }

            assertTrue(seen == (n over k).toInt())

            seen = 0
            list.permute(count = k, mode = PermuteMode.UniqueSets) {
                seen++
                null
            }

            val expectedSize = (n over k) / k.faculty()

            assertTrue(seen == expectedSize.toInt())
        }
    }
}