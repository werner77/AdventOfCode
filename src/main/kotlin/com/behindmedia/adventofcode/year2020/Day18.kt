package com.behindmedia.adventofcode.year2020

class Day18 {
    data class State(val string: String, var position: Int) {
        val isFinished: Boolean
            get() = position >= string.length

        fun consume() : Char {
            return string[position++]
        }

        fun revert() {
            position--
        }
    }

    enum class Operator {
        Plus,
        Times;
        fun evaluate(value1: Long, value2: Long): Long {
            return when (this) {
                Plus -> value1 + value2
                Times -> value1 * value2
            }
        }
    }

    fun part1(input: String): Long {
        var sum = 0L
        input.split("\n").forEach {
            sum += parse(State(it, 0), false)
        }
        return sum
    }

    fun part2(input: String): Long {
        var sum = 0L
        input.split("\n").forEach {
            sum += parse(State(it, 0), true)
        }
        return sum
    }

    // ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2
    private fun parse(state: State, addPrecedence: Boolean, inBracket: Boolean = false) : Long {
        // The currently evaluated number for the expression (left hand side)
        var evaluation: Long? = null

        // The last parsed operand (right hand side of expression)
        var operand: Long? = null

        // The last encountered operator
        var operator: Operator? = null

        // Buffer to hold the operand
        val operandBuffer = StringBuilder()

        fun evaluate(): Long? {
            if (operandBuffer.isNotEmpty()) {
                assert(operand == null)
                operand = operandBuffer.toString().toLong()
                operandBuffer.clear()
            }
            operand?.let { arg ->
                val eval = evaluation
                val op = operator
                evaluation = when {
                    eval == null -> arg
                    op != null -> op.evaluate(eval, arg)
                    else -> error("Should not happen, either current evaluation should be null or operator should be not null")
                }
                operator = null
            }
            operand = null
            return evaluation
        }

        while (!state.isFinished) {
            val c = state.consume()
            var isNumber = false
            when (c) {
                '(' -> {
                    operand = parse(state, addPrecedence, true)
                }
                ')' -> {
                    if (!inBracket) {
                        // Decrement the position by one, because the bracket should not be part of this parse call
                        state.revert()
                    }
                    break
                }
                ' ' -> {
                    // Ignore
                }
                '+' -> {
                    operator = Operator.Plus
                }
                '*' -> {
                    operator = Operator.Times
                    if (addPrecedence) {
                        // Perform a recursive call as if a '(' was encountered
                        operand = parse(state, addPrecedence, false)
                    }
                }
                else -> {
                    isNumber = true
                }
            }
            if (isNumber) {
                operandBuffer.append(c)
            } else {
                evaluate()
            }
        }
        return evaluate() ?: error("Final evaluation should not be null")
    }
}