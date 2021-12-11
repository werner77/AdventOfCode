package com.behindmedia.adventofcode.year2016.day12

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2016.common.OpCode
import com.behindmedia.adventofcode.year2016.common.Program

/*
cpy 41 a
inc a
inc a
dec a
jnz a 2
dec a
 */
fun main() {
    // part 1
    solve()

    // part 2
    solve {
        it["c"] = 1
    }
}

private fun solve(init: (Program) -> Unit = {}) {
    val opCodes = parseLines("/2016/day12.txt") { line ->
        OpCode(line)
    }
    val program = Program(opCodes)
    init(program)
    program.run()
    println(program["a"])
}