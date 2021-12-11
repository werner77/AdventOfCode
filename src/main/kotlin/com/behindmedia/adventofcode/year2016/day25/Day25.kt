package com.behindmedia.adventofcode.year2016.day25

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2016.common.OpCode
import com.behindmedia.adventofcode.year2016.common.Program
import com.behindmedia.adventofcode.year2016.common.TransmitState

fun main() {
    val data = parseLines("/2016/day25.txt") { line ->
        OpCode(line)
    }
    val program = Program(data)
    var i = 1
    while (true) {
        val state = program.copy()
        state["a"] = i
        state.run()
        if (state.transmitState == TransmitState.Complete) break
        i++
    }
    println(i)
}