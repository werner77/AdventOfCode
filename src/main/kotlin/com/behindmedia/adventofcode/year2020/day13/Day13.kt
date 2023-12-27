package com.behindmedia.adventofcode.year2020.day13

import com.behindmedia.adventofcode.common.*

fun main() {
    val data = read("/2020/day13.txt")
    val (time, schedule) = data.split("\n")
    val timestamp = time.toLong()
    val scheduleTimes = schedule.split(",").filter { it != "x" }.map { it.toLong() }
    var best: Pair<Long, Long>? = null
    for (s in scheduleTimes) {
        val m = timestamp % s
        val value = if (m == 0L) {
            timestamp
        } else {
            ((timestamp / s) + 1L) * s
        }
        if (best == null || (value - timestamp) < best.first) {
            best = (value - timestamp) to s
        }
    }
    requireNotNull(best)
    println(best.first * best.second)
    val values = mutableListOf<Pair<Long, Long>>()
    for ((i, s) in schedule.split(",").withIndex()) {
        if (s == "x") {
            continue
        } else {
            val v = s.toLong()
            values += (v - i.toLong()).mod(v) to v
        }
    }
    println(chineseRemainder(values)?.first)
}