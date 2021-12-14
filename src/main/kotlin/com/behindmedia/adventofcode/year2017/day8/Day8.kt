package com.behindmedia.adventofcode.year2017.day8

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

//b inc 5 if a > 1
//a inc 1 if b < 5
//c dec -10 if a >= 1
//c inc -20 if c == 10

class State(private val registers: DefaultMutableMap<String, Int> = defaultMutableMapOf { 0 }) :
    DefaultMutableMap<String, Int> by registers {
    var highest = 0
    operator fun set(key: String, value: Int): Int? =
        registers.put(key, value).also { if (it != null && it > highest) highest = it }

    var pos = 0
}

class Condition(args: List<String>)

sealed class Operation {
    companion object {
        operator fun invoke(string: String): Operation {
            val components = string.split(" ")
            return when (components[1]) {
                "inc" -> Increment(components[0], components[2], components.subList(3, components.size))
                "dec" -> Decrement(components[0], components[2], components.subList(3, components.size))
                else -> error("Unrecognized instruction: $string")
            }
        }
    }

    abstract fun execute(state: State)
    fun evaluateCondition(condition: List<String>, state: State): Boolean {
        val value1 = state[condition[1]]
        val value2 = condition[3].toInt()
        val operator = condition[2]
        return when (operator) {
            "!=" -> value1 != value2
            "<=" -> value1 <= value2
            "<" -> value1 < value2
            ">=" -> value1 >= value2
            ">" -> value1 > value2
            "==" -> value1 == value2
            else -> error("Invalid operator: $operator")
        }
    }

    class Increment(val arg1: String, val arg2: String, val condition: List<String>) : Operation() {
        override fun execute(state: State) {
            if (evaluateCondition(condition, state)) {
                state[arg1] += arg2.toInt()
            }
            state.pos++
        }
    }

    class Decrement(val arg1: String, val arg2: String, val condition: List<String>) : Operation() {
        override fun execute(state: State) {
            if (evaluateCondition(condition, state)) {
                state[arg1] -= arg2.toInt()
            }
            state.pos++
        }
    }
}

fun main() {
    val operations = parseLines("/2017/day8.txt") { line ->
        Operation(line)
    }
    val state = State()

    for (operation in operations) {
        operation.execute(state)
    }

    // Part 1
    println(state.values.max())

    // Part 2
    println(state.highest)
}