package com.behindmedia.adventofcode.year2015.day20

import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.parse
import com.behindmedia.adventofcode.common.timing

private class Day20(val number: Int) {

    private fun getFactors(n: Int): List<Int> {
        var i = 1
        val result = mutableListOf<Int>()
        while (i * i <= n) {
            if (n % i == 0) {
                val d = n / i
                result += d
                if (d != i) result += i
            }
            i++
        }
        return result
    }

    private fun getTarget(divisor: Int): Int {
        return if (number % divisor == 0) {
            number / divisor
        } else {
            (number / divisor) + 1
        }
    }

    fun part1(): Int {
        val target = getTarget(10)
        var houseNumber = 1
        while (true) {
            val total = getFactors(houseNumber).sum()
            if (total >= target) break
            houseNumber++
        }
        return houseNumber
    }

    fun part2(): Int {
        val target = getTarget(11)
        val seenCount = IntArray(target + 1)
        var houseNumber = 1
        while (true) {
            val total = getFactors(houseNumber).sumOf {
                if (seenCount[it]++ < 50) it else 0
            }
            if (total >= target) break
            houseNumber++
        }
        return houseNumber
    }
}

fun main() {
    val number = parse("/2015/day20.txt") { line ->
        line.trim().toInt()
    }

    val day20 = Day20(number)

    timing {
        println(day20.part1())
    }

    timing {
        println(day20.part2())
    }
}