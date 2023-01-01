package com.behindmedia.adventofcode.year2018.day21

import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.year2018.day21.OpCode.addi
import com.behindmedia.adventofcode.year2018.day21.OpCode.addr
import com.behindmedia.adventofcode.year2018.day21.OpCode.bani
import com.behindmedia.adventofcode.year2018.day21.OpCode.banr
import com.behindmedia.adventofcode.year2018.day21.OpCode.bori
import com.behindmedia.adventofcode.year2018.day21.OpCode.borr
import com.behindmedia.adventofcode.year2018.day21.OpCode.eqir
import com.behindmedia.adventofcode.year2018.day21.OpCode.eqri
import com.behindmedia.adventofcode.year2018.day21.OpCode.eqrr
import com.behindmedia.adventofcode.year2018.day21.OpCode.gtir
import com.behindmedia.adventofcode.year2018.day21.OpCode.gtri
import com.behindmedia.adventofcode.year2018.day21.OpCode.gtrr
import com.behindmedia.adventofcode.year2018.day21.OpCode.muli
import com.behindmedia.adventofcode.year2018.day21.OpCode.mulr
import com.behindmedia.adventofcode.year2018.day21.OpCode.seti
import com.behindmedia.adventofcode.year2018.day21.OpCode.setr

fun main() {
    val instructions = mutableListOf<Instruction>()
    var instructionPointerRegister = 0
    parseLines("/2018/day21.txt") { line ->
        if (line.startsWith("#ip")) {
            instructionPointerRegister = line.split(" ").firstNotNullOf { it.toIntOrNull() }
        } else {
            instructions += Instruction(string = line)
        }
    }
    val state = IntArray(6) {
        0
    }
    state[0] = 0
    printProgram(program = instructions, state = state, ipRegister = instructionPointerRegister)

    val seen = mutableSetOf<Int>()
    var last: Int? = null

    executeProgram(program = instructions, state = state, ipRegister = instructionPointerRegister) { ip ->
        var exit = false
        if (ip == 28) {
            val r5 = state[5]
            if (last == null) {
                // Part 1
                println("Part 1: $r5")
            } else if (r5 in seen) {
                // Part 2
                println("Part 2: $last")
                exit = true
            }
            last = r5
            seen += r5
        }
        exit
    }
}

private data class Instruction(val code: OpCode, val arguments: List<Int>) {
    companion object {
        operator fun invoke(string: String): Instruction {
            val components = string.split(" ").filter { it.isNotBlank() }
            val code = OpCode.values().first { it.name == components[0] }
            return Instruction(code, components.subList(1, components.size).map { it.toInt() })
        }
    }
}

private enum class OpCode {
    addr,
    addi,
    mulr,
    muli,
    banr,
    bani,
    borr,
    bori,
    setr,
    seti,
    gtir,
    gtri,
    gtrr,
    eqir,
    eqri,
    eqrr
}

private fun printProgram(program: List<Instruction>, state: IntArray, ipRegister: Int) {
    for ((ip, line) in program.withIndex()) {
        printInstruction(line.code, line.arguments, ipRegister, ip)
    }
}

private fun executeProgram(program: List<Instruction>, state: IntArray, ipRegister: Int, predicate: (Int) -> Boolean) {
    var ip = 0
    while (true) {
        val nextInstruction = program.getOrNull(ip) ?: break
        ip = operate(nextInstruction.code, nextInstruction.arguments, state, ipRegister, ip, predicate)
    }
}

private fun registerString(index: Int, ipRegister: Int, ip: Int): String {
    if (index == ipRegister) {
        return "$ip"
    } else {
        return "r[$index]"
    }
}

private fun registerPrefix(index: Int, ipRegister: Int): String {
    if (index == ipRegister) {
        return "goto "
    } else {
        return "r[$index] = "
    }
}

private fun printInstruction(code: OpCode, operands: List<Int>, ipRegister: Int, ip: Int) {
    val (a, b, c) = operands
    print("[$ip]: ")
    when (code) {
        addr -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} + ${registerString(b, ipRegister, ip)}")
        }
        addi -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} + $b")
        }
        mulr -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} * ${registerString(b, ipRegister, ip)}")
        }
        muli -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} * $b")
        }
        banr -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} & ${registerString(b, ipRegister, ip)}")
        }
        bani -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} & $b")
        }
        borr -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} | ${registerString(b, ipRegister, ip)}")
        }
        bori -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)} | $b")
        }
        setr -> {
            println("${registerPrefix(c, ipRegister)}${registerString(a, ipRegister, ip)}")
        }
        seti -> {
            println("${registerPrefix(c, ipRegister)}$a")
        }
        gtir -> {
            println("${registerPrefix(c, ipRegister)}if ($a > ${registerString(b, ipRegister, ip)}) 1 else 0")
        }
        gtri -> {
            println("${registerPrefix(c, ipRegister)}if (${registerString(a, ipRegister, ip)} > $b) 1 else 0")
        }
        gtrr -> {
            println("${registerPrefix(c, ipRegister)}if (${registerString(a, ipRegister, ip)} > ${registerString(b, ipRegister, ip)}) 1 else 0")
        }
        eqir -> {
            println("${registerPrefix(c, ipRegister)}if (${a} == ${registerString(b, ipRegister, ip)}) 1 else 0")
        }
        eqri -> {
            println("${registerPrefix(c, ipRegister)}if (${registerString(a, ipRegister, ip)} == ${b}}) 1 else 0")
        }
        eqrr -> {
            println("${registerPrefix(c, ipRegister)}if (${registerString(a, ipRegister, ip)} == ${registerString(b, ipRegister, ip)}) 1 else 0")
        }
    }
}

private fun operate(code: OpCode, operands: List<Int>, state: IntArray, ipRegister: Int, ip: Int, predicate: (Int) -> Boolean): Int {
    state[ipRegister] = ip
    // Exit program if predicate is true
    if (predicate(ip)) return -1
    val (a, b, c) = operands
    when (code) {
        addr -> {
            state[c] = state[a] + state[b]
        }
        addi -> {
            state[c] = state[a] + b
        }
        mulr -> {
            state[c] = state[a] * state[b]
        }
        muli -> {
            state[c] = state[a] * b
        }
        banr -> {
            state[c] = state[a] and state[b]
        }
        bani -> {
            state[c] = state[a] and b
        }
        borr -> {
            state[c] = state[a] or state[b]
        }
        bori -> {
            state[c] = state[a] or b
        }
        setr -> {
            state[c] = state[a]
        }
        seti -> {
            state[c] = a
        }
        gtir -> {
            state[c] = if (a > state[b]) 1 else 0
        }
        gtri -> {
            state[c] = if (state[a] > b) 1 else 0
        }
        gtrr -> {
            state[c] = if (state[a] > state[b]) 1 else 0
        }
        eqir -> {
            state[c] = if (a == state[b]) 1 else 0
        }
        eqri -> {
            state[c] = if (state[a] == b) 1 else 0
        }
        eqrr -> {
            state[c] = if (state[a] == state[b]) 1 else 0
        }
    }
    return state[ipRegister] + 1
}
