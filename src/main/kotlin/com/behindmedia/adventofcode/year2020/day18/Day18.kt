package com.behindmedia.adventofcode.year2020.day18

import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.year2020.day18.Operator.Times

fun main() {
    val lines = read("/2020/day18.txt").split("\n")


    println(lines.sumOf { evaluate(it, false) })
    println(lines.sumOf { evaluate(it, true) })
}

class State(val special: Boolean = false) {
    var value: Long = 0L
    var operator: Operator? = null

    fun addValue(value: Long) {
        this.value = operator?.invoke(this.value, value)?.also { operator = null } ?: value
    }
}

sealed class Operator {
    companion object {
        operator fun invoke(char: Char): Operator? {
            return when (char) {
                '+' -> Plus
                '*' -> Times
                else -> null
            }
        }
    }

    abstract fun invoke(left: Long, right: Long): Long

    abstract val precedence: Int

    data object Plus: Operator() {
        override fun invoke(left: Long, right: Long): Long {
            return left + right
        }

        override val precedence: Int
            get() = 2
    }

    data object Times: Operator()  {
        override fun invoke(left: Long, right: Long): Long {
            return left * right
        }

        override val precedence: Int
            get() = 1
    }
}

private fun ArrayDeque<State>.push() {
    add(State())
}

private fun ArrayDeque<State>.pushSpecial() {
    add(State(true))
}

private fun ArrayDeque<State>.add(value: Long) {
    last().addValue(value)
}

private fun ArrayDeque<State>.pop() {
    val s = removeLast()
    last().addValue(s.value)
}

private fun ArrayDeque<State>.popSpecial() {
    if (last().special) {
        pop()
    }
}

private fun evaluate(expression: String, precedenceEnabled: Boolean): Long {
    val stack = ArrayDeque<State>()
    val buffer = StringBuilder()
    stack.push()
    for (c in expression) {
        if (c == ' ') {
            continue
        }
        if (c.isDigit()) {
            buffer.append(c)
            continue
        }
        if (buffer.isNotEmpty()) {
            stack.add(buffer.toString().toLong())
            buffer.clear()
        }
        when (c) {
            '(' -> {
                stack.push()
            }
            ')' -> {
                stack.popSpecial()
                stack.pop()
            }
            else -> {
                val operator = Operator(c) ?: error("Invalid character: $c")
                if (operator is Times) {
                    stack.popSpecial()
                }
                stack.last().operator = operator
                if (operator is Times && precedenceEnabled) {
                    stack.pushSpecial()
                }
            }
        }
    }
    if (buffer.isNotEmpty()) {
        stack.last().addValue(buffer.toString().toLong())
    }
    stack.popSpecial()
    require(stack.size == 1)
    return stack.last().value
}
