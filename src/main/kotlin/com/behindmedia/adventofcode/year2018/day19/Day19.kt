package com.behindmedia.adventofcode.year2018.day19

import com.behindmedia.adventofcode.common.DefaultMutableMap
import com.behindmedia.adventofcode.common.defaultMutableMapOf
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.year2018.day19.OpCode.addi
import com.behindmedia.adventofcode.year2018.day19.OpCode.addr
import com.behindmedia.adventofcode.year2018.day19.OpCode.bani
import com.behindmedia.adventofcode.year2018.day19.OpCode.banr
import com.behindmedia.adventofcode.year2018.day19.OpCode.bori
import com.behindmedia.adventofcode.year2018.day19.OpCode.borr
import com.behindmedia.adventofcode.year2018.day19.OpCode.eqir
import com.behindmedia.adventofcode.year2018.day19.OpCode.eqri
import com.behindmedia.adventofcode.year2018.day19.OpCode.eqrr
import com.behindmedia.adventofcode.year2018.day19.OpCode.gtir
import com.behindmedia.adventofcode.year2018.day19.OpCode.gtri
import com.behindmedia.adventofcode.year2018.day19.OpCode.gtrr
import com.behindmedia.adventofcode.year2018.day19.OpCode.muli
import com.behindmedia.adventofcode.year2018.day19.OpCode.mulr
import com.behindmedia.adventofcode.year2018.day19.OpCode.seti
import com.behindmedia.adventofcode.year2018.day19.OpCode.setr

fun main() {
    val instructions = mutableListOf<Instruction>()
    var instructionPointerRegister = 0
    parseLines("/2018/day19.txt") { line ->
        if (line.startsWith("#ip")) {
            instructionPointerRegister = line.split(" ").firstNotNullOf { it.toIntOrNull() }
        } else {
            instructions += Instruction(string = line)
        }
    }
    debug(instructionPointerRegister)
    debug(instructions)
    val state = defaultMutableMapOf<Int, Int> {
        0
    }
    state[0] = 1
    executeProgram(program = instructions, state = state, ipRegister = instructionPointerRegister)
    println(state[0])
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

private fun executeProgram(program: List<Instruction>, state: DefaultMutableMap<Int, Int>, ipRegister: Int) {
    var ip = 0
    while (true) {
        val nextInstruction = program.getOrNull(ip) ?: break
        ip = operate(nextInstruction.code, nextInstruction.arguments, state, ipRegister, ip)
        debug(state)
        debug("ip=$ip")
        debug("")
    }
}

private fun operate(code: OpCode, operands: List<Int>, state: DefaultMutableMap<Int, Int>, ipRegister: Int, ip: Int): Int {
    state[ipRegister] = ip
    require(operands.size == 3)
    val (a, b, c) = operands
    when (code) {
        addr -> {
            debug("state[$c] = state[$a] + state[$b]")
            state[c] = state[a] + state[b]
        }
        addi -> {
            debug("state[$c] = state[$a] + $b")
            state[c] = state[a] + b
        }
        mulr -> {
            debug("state[$c] = state[$a] * state[$b]")
            state[c] = state[a] * state[b]
        }
        muli -> {
            debug("state[$c] = state[$a] * $b")
            state[c] = state[a] * b
        }
        banr -> {
            debug("state[$c] = state[$a] and state[$b]")
            state[c] = state[a] and state[b]
        }
        bani -> {
            debug("state[$c] = state[$a] and $b")
            state[c] = state[a] and b
        }
        borr -> {
            debug("state[$c] = state[$a] and state[$b]")
            state[c] = state[a] or state[b]
        }
        bori -> {
            debug("state[$c] = state[$a] or $b")
            state[c] = state[a] or b
        }
        setr -> {
            debug("state[$c] = state[$a]")
            state[c] = state[a]
        }
        seti -> {
            debug("state[$c] = $a")
            state[c] = a
        }
        gtir -> {
            debug("state[$c] = if ($a > state[$b]) 1 else 0")
            state[c] = if (a > state[b]) 1 else 0
        }
        gtri -> {
            debug("state[$c] = if (state[$a] > $b) 1 else 0")
            state[c] = if (state[a] > b) 1 else 0
        }
        gtrr -> {
            debug("state[$c] = if (state[$a] > state[$b]) 1 else 0")
            state[c] = if (state[a] > state[b]) 1 else 0
        }
        eqir -> {
            debug("state[$c] = if ($a == state[$b]) 1 else 0")
            state[c] = if (a == state[b]) 1 else 0
        }
        eqri -> {
            debug("state[$c] = if (state[$a] == $b) 1 else 0")
            state[c] = if (state[a] == b) 1 else 0
        }
        eqrr -> {
            debug("state[$c] = if (state[$a] == state[$b]) 1 else 0")
            state[c] = if (state[a] == state[b]) 1 else 0
        }
    }
    return state[ipRegister] + 1
}

private val DEBUG = false

private fun debug(message: Any) {
    if (DEBUG) {
        println(message)
    }
}