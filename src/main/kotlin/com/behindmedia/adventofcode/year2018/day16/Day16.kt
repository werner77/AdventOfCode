package com.behindmedia.adventofcode.year2018.day16

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2018.day16.OpCode.addi
import com.behindmedia.adventofcode.year2018.day16.OpCode.addr
import com.behindmedia.adventofcode.year2018.day16.OpCode.bani
import com.behindmedia.adventofcode.year2018.day16.OpCode.banr
import com.behindmedia.adventofcode.year2018.day16.OpCode.bori
import com.behindmedia.adventofcode.year2018.day16.OpCode.borr
import com.behindmedia.adventofcode.year2018.day16.OpCode.eqir
import com.behindmedia.adventofcode.year2018.day16.OpCode.eqri
import com.behindmedia.adventofcode.year2018.day16.OpCode.eqrr
import com.behindmedia.adventofcode.year2018.day16.OpCode.gtir
import com.behindmedia.adventofcode.year2018.day16.OpCode.gtri
import com.behindmedia.adventofcode.year2018.day16.OpCode.gtrr
import com.behindmedia.adventofcode.year2018.day16.OpCode.muli
import com.behindmedia.adventofcode.year2018.day16.OpCode.mulr
import com.behindmedia.adventofcode.year2018.day16.OpCode.seti
import com.behindmedia.adventofcode.year2018.day16.OpCode.setr

private data class TestCase(val beforeState: List<Long>, val afterState: List<Long>, val sample: List<Long>) {

    val intCode: Long
        get() = sample[0]

    val operands: List<Long>
        get() = sample.subList(1, sample.size)

    companion object {
        private val regex = """\d+""".toRegex()
        operator fun invoke(encoded: String): TestCase {
            val values: List<List<Long>> = encoded.split("\n")
                .map { line ->
                    regex.findAll(line).map { it.groupValues.first().toLong() }.toList()
                }
            require(values.size == 3)
            return TestCase(values[0], values[2], values[1])
        }
    }
}

fun main() {
    val data = read("/2018/day16.txt")
    val (encodedTestCases, encodedProgram) = data.split("\n\n\n")
    val testCases = encodedTestCases.split("\n\n").map { section ->
        TestCase(section)
    }
    val program = encodedProgram.split("\n").filter{ it.isNotBlank() }.map { line ->
        line.split(" ").filter { it.isNotBlank() }.map { it.toLong() }
    }
    part1(testCases)
    part2(testCases, program)
}

private fun part1(testCases: List<TestCase>) {
    var count = 0
    for (testCase in testCases) {
        val inputState = testCase.beforeState.toMap()
        val outputState = testCase.afterState.toMap()
        var matchCount = 0
        for (code in OpCode.values()) {
            try {
                val state = inputState.toMutableMap()
                operate(code, testCase.sample.subList(1, testCase.sample.size), state)
                if (state == outputState) {
                    matchCount++
                }
            } catch (e: Exception) {
                // Fail
            }
        }
        if (matchCount >= 3) {
            count++
        }
    }
    println(count)
}

private fun part2(testCases: List<TestCase>, program: List<List<Long>>) {
    val candidates = mutableMapOf<Long, MutableSet<OpCode>>()
    for (testCase in testCases) {
        val inputState = testCase.beforeState.toMap()
        val outputState = testCase.afterState.toMap()
        val intCode = testCase.intCode
        for (code in OpCode.values()) {
            try {
                val state = inputState.toMutableMap()
                operate(code, testCase.operands, state)
                if (state == outputState) {
                    candidates.getOrPut(intCode) { mutableSetOf() }.add(code)
                }
            } catch (e: Exception) {
                // Fail
            }
        }
    }

    // Resolve
    val resolved = mutableMapOf<Long, OpCode>()
    while (candidates.isNotEmpty()) {
        val (intCode, codeSet) = candidates.entries.firstOrNull { it.value.size == 1 } ?: error("No unique entry found")
        val code = codeSet.single()
        resolved[intCode] = code
        candidates.entries.toList().forEach {
            it.value.remove(code)
            if (it.value.isEmpty()) {
                candidates.remove(it.key)
            }
        }
    }
    require(resolved.size == OpCode.values().size)

    val state = defaultMutableMapOf<Long, Long> {
        0L
    }
    for (line in program) {
        val intCode = line[0]
        val operands = line.subList(1, line.size)
        val code = resolved[intCode]!!
        operate(code, operands, state)
    }
    println(state[0L])
}

private fun List<Long>.toMap(): Map<Long, Long> {
    return this.withIndex().fold(mutableMapOf()) { m, (i, v) ->
        m.apply { put(i.toLong(), v) }
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

private fun operate(code: OpCode, operands: List<Long>, state: MutableMap<Long, Long>) {
    require(operands.size == 3)
    val (a, b, c) = operands
    when (code) {
        addr -> state[c] = state[a]!! + state[b]!!
        addi -> state[c] = state[a]!! + b
        mulr -> state[c] = state[a]!! * state[b]!!
        muli -> state[c] = state[a]!! * b
        banr -> state[c] = state[a]!! and state[b]!!
        bani -> state[c] = state[a]!! and b
        borr -> state[c] = state[a]!! or state[b]!!
        bori -> state[c] = state[a]!! or b
        setr -> state[c] = state[a]!!
        seti -> state[c] = a
        gtir -> state[c] = if (a > state[b]!!) 1 else 0
        gtri -> state[c] = if (state[a]!! > b) 1 else 0
        gtrr -> state[c] = if (state[a]!! > state[b]!!) 1 else 0
        eqir -> state[c] = if (a == state[b]!!) 1 else 0
        eqri -> state[c] = if (state[a]!! == b) 1 else 0
        eqrr -> state[c] = if (state[a]!! == state[b]!!) 1 else 0
    }
}