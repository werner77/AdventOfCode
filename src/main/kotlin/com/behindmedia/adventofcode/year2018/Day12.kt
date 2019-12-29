package com.behindmedia.adventofcode.year2018

import kotlin.math.min

class Day12 {

    fun parseInput(input: String): Pair<String, Map<String, Char>> {
        val lines = input.split('\n')

        var initialState: String? = null
        val initialStatePrefix = "initial state: "
        val patterns = mutableMapOf<String, Char>()

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue

            if (trimmedLine.startsWith(initialStatePrefix)) {
                initialState = line.substring(initialStatePrefix.length).trim()
            } else {
                val components = line.split(" => ").map { it.trim() }
                assert(components[1].length == 1)
                patterns[components[0]] = components[1].first()
            }
        }
        return if (initialState != null) Pair(initialState, patterns) else
            throw IllegalArgumentException("Could not find initial state in input")
    }

    private fun String.patternSubString(index: Int): String {
        val start = index - 2
        val end = start + 5
        val buffer = StringBuilder()
        for (i in start until end) {
            if (i < 0 || i >= this.length) {
                buffer.append(".")
            } else {
                buffer.append(this[i])
            }
        }
        return buffer.toString()
    }

    private fun StringBuilder.trimPattern(): Pair<String, Int> {
        var startIndex = 0
        var endIndex = this.length
        while(startIndex < this.length) {
            if (this[startIndex] != '.') break
            startIndex++
        }

        while(endIndex > startIndex) {
            if (this[endIndex - 1] != '.') break
            endIndex--
        }
        return Pair(this.substring(startIndex, endIndex), startIndex)
    }

    private fun process(state: String, patterns: Map<String, Char>): Pair<String, Int> {
        val nextState = StringBuilder()
        nextState.append("..")
        nextState.append(state)
        nextState.append("..")
        for (i in -2 until state.length + 2) {
            val subString = state.patternSubString(i)
            val replacement = patterns[subString] ?: '.'
            nextState[i + 2] = replacement
        }
        val trimmed = nextState.trimPattern()
        val offset = trimmed.second - 2
        return Pair(trimmed.first, offset)
    }

    fun checksum(state: String, startIndex: Long): Long {
        var sum = 0L
        for ((j, i) in (startIndex until startIndex + state.length).withIndex()) {
            if (state[j] == '#') sum += i
        }
        return sum
    }

    private fun findPeriod(initialState: String, patterns: Map<String, Char>): Pair<Long, Long> {
        val seen = mutableMapOf<String, Long>()
        var startIndex = 0L
        var state = initialState
        var index = 1L
        while (true) {
            val result = process(state, patterns)
            startIndex += result.second
            state = result.first

            val previousIndex = seen[state]
            if (previousIndex != null) {
                return Pair(previousIndex, index - previousIndex)
            }
            seen[state] = index++
        }
    }

    fun process(initialState: String, patterns: Map<String, Char>, iterationCount: Long): Pair<String, Long> {
        var state = initialState
        var startIndex = 0L
        for (i in 0 until iterationCount) {
            val result = process(state, patterns)
            startIndex += result.second
            state = result.first
        }
        return Pair(state, startIndex)
    }

    fun process(input: String, iterationCount: Long): Long {
        val (initialState, patterns) = parseInput(input)
        val period = findPeriod(initialState, patterns)

        assert(period.second == 1L)

        val processResult = process(initialState, patterns, min(iterationCount, period.first))

        val iterationsLeft = iterationCount - period.first

        val baseChecksum = checksum(processResult.first, processResult.second)

        if (iterationsLeft <= 0) {
            return baseChecksum
        }

        val amountOFPlants = processResult.first.sumBy { if (it == '#') 1 else 0 }

        return (iterationCount - period.first) * amountOFPlants.toLong()  + baseChecksum
    }
}