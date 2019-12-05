package com.behindmedia.adventofcode2019

import java.lang.IllegalStateException

class ThermalEnvironmentSupervisionTerminal {

    data class OpCode(val code: Int, val operandModes: IntArray) {
        companion object {
            fun from(encoding: Int): OpCode {
                var encodedOpCode = encoding
                val code = encodedOpCode % 100
                val operandModes = IntArray(3)

                encodedOpCode /= 100
                for (i in 0 until 3) {
                    operandModes[i] = encodedOpCode % 10
                    encodedOpCode /= 10
                    assert(operandModes[i] < 2)
                }
                return OpCode(code, operandModes)
            }
        }
    }

    interface Operation {
        val code: Int
        fun execute(position: Int, operandModes: IntArray, state: MutableList<Int>): Int?

        companion object {
            fun from(code: Int): Operation? {
                return when (code) {
                    1 -> AddOperation()
                    else -> null
                }
            }
        }

        fun valueOfOperand(index: Int, position: Int, operandModes: IntArray, state: List<Int>, requiredOperandMode: Int? = null): Int {
            val value = state[position + index]
            val operandMode = operandModes[index]
            assert(operandMode == 0 || operandMode == 1)
            requiredOperandMode?.let {
                if (it != operandMode) {
                    throw IllegalArgumentException("Invalid operand mode $it")
                }
            }
            return if (operandMode == 0) state[value] else value
        }
    }

    class AddOperation : Operation {
        override val code = 1

        override fun execute(position: Int, operandModes: IntArray, state: MutableList<Int>): Int? {
            val address = valueOfOperand(2, position, operandModes, state, 0)
            state[address] = valueOfOperand(0, position, operandModes, state) + valueOfOperand(1, position, operandModes, state)
            return position + 3
        }
    }

    class MultiplyOperation : Operation {
        override val code = 2

        override fun execute(position: Int, operandModes: IntArray, state: MutableList<Int>): Int? {
            val address = valueOfOperand(2, position, operandModes, state, 0)
            state[address] = valueOfOperand(0, position, operandModes, state) * valueOfOperand(1, position, operandModes, state)
            return position + 3
        }
    }

    class InputOperation : Operation {
        override val code = 3

        override fun execute(position: Int, operandModes: IntArray, state: MutableList<Int>): Int? {
            val address = valueOfOperand(0, position, operandModes, state, 0)
            state[address] = input
            return position + 1
        }
    }

    class OutputOperation : Operation {
        override val code = 4

        override fun execute(position: Int, operandModes: IntArray, state: MutableList<Int>): Int? {
            val address = valueOfOperand(0, position, operandModes, state, 0)
            state[address] = valueOfOperand(0, position, operandModes, state) * valueOfOperand(1, position, operandModes, state)
            return position + 1
        }
    }


    fun <T>processOpcodes(initialState: List<Int>, input: Int, resultExtractor: (List<Int>, Operation) -> T?): T? {
        val mutableState = initialState.toMutableList()
        var position = 0
        var result: T? = null
        while (true) {
            val opCode = OpCode.from(mutableState[position])
            val operation = Operation.from(opCode.code)
                ?: throw IllegalStateException("Found unrecognized opcode at position $position: ${mutableState[position]}")

            val nextPosition = operation.execute(++position, opCode.operandModes, mutableState) ?: break
            result = resultExtractor(mutableState, operation)
            position = nextPosition
        }
        return result
    }
}