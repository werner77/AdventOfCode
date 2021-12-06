package com.behindmedia.adventofcode.year2016.day21

import com.behindmedia.adventofcode.common.*

private val swapPosRegex = """swap position ([\d]+) with position ([\d]+)""".toRegex()
private val swapLetterRegex = """swap letter ([a-zA-Z]+) with letter ([a-zA-Z]+)""".toRegex()
private val rotateRegex = """rotate (left|right) ([\d]+) step[s]?""".toRegex()
private val rotateBasedOnRegex = """rotate based on position of letter ([a-zA-Z]+)""".toRegex()
private val reverseRegex = """reverse positions ([\d]+) through ([\d]+)""".toRegex()
private val moveRegex = """move position ([\d]+) to position ([\d]+)""".toRegex()

sealed class Operation {

    companion object {
        operator fun invoke(string: String): Operation {
            var operation: Operation? = null
            operation =
                swapPosRegex.matchEntire(string)?.groupValues?.let { SwapPositions(it[1].toInt(), it[2].toInt()) }
            if (operation != null) return operation
            operation = swapLetterRegex.matchEntire(string)?.groupValues?.let { SwapLetters(it[1], it[2]) }
            if (operation != null) return operation
            operation = rotateRegex.matchEntire(string)?.groupValues?.let { Rotate(it[1] == "left", it[2].toInt()) }
            if (operation != null) return operation
            operation = rotateBasedOnRegex.matchEntire(string)?.groupValues?.let { RotateBasedOn(it[1]) }
            if (operation != null) return operation
            operation = reverseRegex.matchEntire(string)?.groupValues?.let { Reverse(it[1].toInt(), it[2].toInt()) }
            if (operation != null) return operation
            operation = moveRegex.matchEntire(string)?.groupValues?.let { Move(it[1].toInt(), it[2].toInt()) }
            if (operation != null) return operation
            error("Could not parse string: $string")
        }
    }

    abstract fun execute(input: String): String

    abstract fun inverted(input: String): Operation

    data class SwapPositions(val pos1: Int, val pos2: Int) : Operation() {
        override fun execute(input: String): String {
            val output = StringBuilder(input)
            output[pos1] = input[pos2].also { output[pos2] = input[pos1] }
            return output.toString()
        }

        override fun inverted(input: String): Operation {
            return SwapPositions(pos2, pos1)
        }
    }

    data class SwapLetters(val letter1: String, val letter2: String) : Operation() {
        override fun execute(input: String): String {
            val c1 = letter1[0]
            val c2 = letter2[0]
            val output = StringBuilder()
            for (c in input) {
                output.append(if (c == c1) c2 else if (c == c2) c1 else c)
            }
            return output.toString()
        }

        override fun inverted(input: String): Operation {
            return SwapLetters(letter2, letter1)
        }
    }

    data class Rotate(val left: Boolean, val steps: Int) : Operation() {
        override fun execute(input: String): String {
            val output = StringBuilder()
            for (i in input.indices) {
                val j = (if (left) i + steps else i - steps) % input.length
                val k = if (j < 0) j + input.length else j
                output.append(input[k])
            }
            return output.toString()
        }

        override fun inverted(input: String): Operation {
            return Rotate(!left, steps)
        }
    }

    data class RotateBasedOn(val letter: String) : Operation() {
        override fun execute(input: String): String {
            val c = letter[0]
            val index = input.indexOfFirst { it == c }
            val steps = if (index >= 4) {
                1 + index + 1
            } else {
                1 + index
            }
            return Rotate(false, steps).execute(input)
        }

        override fun inverted(input: String): Operation {
            for (i in input.indices) {
                val inverseCandidate = Rotate(true, steps = i)
                if (this.execute(inverseCandidate.execute(input)) == input) return inverseCandidate
            }
            error("Could not find inverse")
        }
    }

    data class Reverse(val start: Int, val end: Int) : Operation() {
        override fun execute(input: String): String {
            val output = StringBuilder()
            for (i in input.indices) {
                if (i in start..end) {
                    output.append(input[end + start - i])
                } else {
                    output.append(input[i])
                }
            }
            return output.toString()
        }

        override fun inverted(input: String): Operation {
            return this
        }
    }

    data class Move(val from: Int, val to: Int) : Operation() {
        override fun execute(input: String): String {
            val output = StringBuilder()
            var toInsert: Char? = null
            for (i in input.indices) {
                if (i == from) {
                    toInsert = input[i]
                } else {
                    output.append(input[i])
                }
            }
            output.insert(to, toInsert)
            return output.toString()
        }

        override fun inverted(input: String): Operation {
            return Move(to, from)
        }
    }
}


fun main() {
    val data = parseLines("/2016/day21.txt") { line ->
        Operation(line)
    }
    println(part1(data, "abcdefgh"))
    println(part2(data, "fbgdceah"))
}

private fun part1(
    data: List<Operation>,
    input: String
): String {
    var s = input
    for (operation in data) {
        s = operation.execute(s)
    }
    return s
}

private fun part2(
    data: List<Operation>,
    input: String
): String {
    var s = input
    for (operation in data.reversed()) {
        s = operation.inverted(s).execute(s)
    }
    return s
}