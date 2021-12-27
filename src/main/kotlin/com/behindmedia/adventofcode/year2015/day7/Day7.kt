package com.behindmedia.adventofcode.year2015.day7

import com.behindmedia.adventofcode.common.*
import java.util.*

/*
af AND ah -> ai
NOT lk -> ll
hz RSHIFT 1 -> is
NOT go -> gp
du OR dt -> dv
x RSHIFT 5 -> aa
at OR az -> ba
eo LSHIFT 15 -> es
 */

sealed class Operator {
    companion object {
        operator fun invoke(name: String): Operator {
            return when (name) {
                "AND" -> And
                "OR" -> Or
                "LSHIFT" -> LShift
                "RSHIFT" -> RShift
                "NOT" -> Not
                else -> error("No operator found with name: $name")
            }
        }
    }

    abstract fun execute(input: List<Int>): Int

    object And : Operator() {
        override fun execute(input: List<Int>): Int {
            require(input.size == 2)
            return input[0] and input[1]
        }
    }

    object Or : Operator() {
        override fun execute(input: List<Int>): Int {
            require(input.size == 2)
            return input[0] or input[1]
        }
    }

    object RShift : Operator() {
        override fun execute(input: List<Int>): Int {
            require(input.size == 2)
            return input[0] shr input[1]
        }
    }

    object LShift : Operator() {
        override fun execute(input: List<Int>): Int {
            require(input.size == 2)
            return input[0] shl input[1]
        }
    }

    object Not : Operator() {
        override fun execute(input: List<Int>): Int {
            require(input.size == 1)
            return input[0].inv()
        }
    }

    object Copy : Operator() {
        override fun execute(input: List<Int>): Int {
            require(input.size == 1)
            return input[0]
        }
    }

    override fun toString(): String {
        return this::class.simpleName!!
    }
}

data class Circuit(val inputs: Set<String>, val output: String, val operator: Operator) {
    fun unresolvedInputCount(resolvedMap: Map<String, Int>): Int {
        return inputs.count { it.toIntOrNull() == null && it !in resolvedMap.keys }
    }
}

fun main() {
    val circuits = parseLines("/2015/day7.txt") { line ->
        val (input, output) = line.split(" -> ")
        val components = input.splitNonEmptySequence(" ").toMutableList()
        val nameIndex = components.indexOfFirst { it.uppercase() == it && it.toIntOrNull() == null }
        val operator = if (nameIndex >= 0) {
            val name = components.removeAt(nameIndex)
            Operator(name)
        } else {
            Operator.Copy
        }
        Circuit(components.toSet(), output.trim(), operator)
    }
    val value1 = solve(circuits)["a"] ?: error("Value not resolved")

    // Part 1
    println(value1)

    val newCircuits = circuits.map {
        if (it.output == "b") {
            Circuit(inputs = setOf(value1.toString()), output = "b", Operator.Copy)
        } else {
            it
        }
    }
    val value2 = solve(newCircuits)["a"]
    println(value2)
}

private fun solve(circuits: List<Circuit>): MutableMap<String, Int> {
    val resolvedInputs = mutableMapOf<String, Int>()
    val queue = circuits.toMutableList()
    while (queue.isNotEmpty()) {
        queue.sortByDescending { it.unresolvedInputCount(resolvedInputs) }
        val next = queue.removeLast()
        val inputValues = next.inputs.map {
            it.toIntOrNull() ?: resolvedInputs[it] ?: error("Unresolved input: $it")
        }
        val outputValue = next.operator.execute(inputValues)
        resolvedInputs[next.output] = outputValue
    }
    return resolvedInputs
}