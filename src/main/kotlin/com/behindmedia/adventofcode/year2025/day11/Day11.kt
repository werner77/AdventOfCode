package com.behindmedia.adventofcode.year2025.day11

import com.behindmedia.adventofcode.common.*

private data class Device(val name: String, val outputs: Set<String>)

private fun dp(current: String, blackList: Set<String>, devices: Map<String, Device>, cache: MutableMap<String, Long>): Long {
    if (current in blackList) {
        return 0L
    }
    if (current == "out") {
        return 1L
    }
    val cachedValue = cache[current]
    if (cachedValue != null) return cachedValue

    val currentDevice = devices[current] ?: error("Unknown device $current")

    var result = 0L
    for (conn in currentDevice.outputs) {
        result += dp(conn, blackList, devices, cache)
    }
    cache[current] = result
    return result
}

private fun solve(fileName: String, part: Int) {
    val data = parseLines("/2025/$fileName") { line ->
        val components= line.split(":")
        require(components.size == 2)
        val name = components[0].trim()
        val outputs = components[1].trim().split(" ").toSet()
        Device(name, outputs)
    }
    val devices = data.associateBy { it.name }
    if( part == 1) {
        val start = data.first { it.name == "you" }
        println(dp(start.name, emptySet(), devices, mutableMapOf<String, Long>()))
    } else {
        // All paths
        val count1 = dp("svr", emptySet(), devices, mutableMapOf<String, Long>())

        // All paths NOT containing "fft"
        val count2 = dp("svr", setOf("fft"), devices, mutableMapOf<String, Long>())

        // All paths NOT containing "dac"
        val count3 = dp("svr", setOf("dac"), devices, mutableMapOf<String, Long>())

        // All paths NOT containing either "fft" or "dac"
        val count4 = dp("svr", setOf("fft", "dac"), devices, mutableMapOf<String, Long>())

        val ans = count1 - count2 - count3 + count4
        println(ans)
    }
}

fun main() {
    for (part in 1..2) {
        solve("day11-sample$part.txt", part)
        solve("day11.txt", part)
    }
}
