package com.behindmedia.adventofcode2019

class Computer(initialState: List<Int>) {

    enum class Status {
        Initial, Processing, WaitingForInput, Done
    }

    private var position = 0
    private val state = initialState.toMutableList()
    private val inputStack = mutableListOf<Int>()

    var lastOutput = 0
        private set

    var status = Status.Initial
        private set

    val currentState: List<Int>
        get() = state.toList()

    fun process(inputs: List<Int>): Int {
        status = Status.Processing
        inputStack.addAll(inputs)
        while (true) {
            val opcode = Opcode.at(position++, state)
            val operation = Operation.forCode(opcode.code) ?:
            throw IllegalStateException("Unknown opcode encountered: $opcode.code")
            position = operation.perform(this, opcode.operandModes) ?: break
        }
        return lastOutput
    }

    private class Opcode(val code: Int, val operandModes: IntArray) {
        companion object {
            fun at(position: Int, state: List<Int>): Opcode {
                var encodedOpcode = state[position]

                val code = encodedOpcode % 100
                val operandModes = IntArray(3)

                encodedOpcode /= 100
                for (i in 0 until 3) {
                    operandModes[i] = encodedOpcode % 10
                    encodedOpcode /= 10
                    assert(operandModes[i] < 2)
                }

                return Opcode(code, operandModes)
            }
        }
    }

    private sealed class Operation {
        companion object {
            fun forCode(code: Int): Operation? {
                return when(code) {
                    1 -> Add
                    2 -> Multiply
                    3 -> Input
                    4 -> Output
                    5 -> JumpIfTrue
                    6 -> JumpIfFalse
                    7 -> LessThan
                    8 -> Equals
                    99 -> Exit
                    else -> null
                }
            }
        }

        abstract fun perform(computer: Computer, operandModes: IntArray): Int?

        protected fun getValue(index: Int, operandModes: IntArray, computer: Computer): Int {
            val value = getValue(index, computer)
            val operandMode = operandModes[index]
            assert(operandMode == 0 || operandMode == 1)
            return if (operandMode == 0) computer.state[value] else value
        }

        protected fun getValue(index: Int, computer: Computer): Int {
            return computer.state[computer.position + index]
        }

        object Add: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                assert(operandModes[2] == 0)
                val address = getValue(2, computer)
                computer.state[address] = first + second
                return computer.position + 3
            }
        }

        object Multiply: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                assert(operandModes[2] == 0)
                val address = getValue(2, computer)
                computer.state[address] = first * second
                return computer.position + 3
            }
        }

        object Input: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                // Input
                val address = getValue(0, computer)
                val input = computer.inputStack.popFirst()
                if (input == null) {
                    // reset position and return status
                    computer.status = Status.WaitingForInput
                    computer.position--
                    return null
                }
                computer.state[address] = input
                return computer.position + 1
            }
        }

        object Output: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                // Output
                val address = getValue(0, computer)
                val output = computer.state[address]
                computer.lastOutput = output
                return computer.position + 1
            }
        }

        object JumpIfTrue: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                return if (first != 0) second else computer.position + 2
            }
        }

        object JumpIfFalse: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                return if (first == 0) second else computer.position + 2
            }
        }

        object LessThan: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                assert(operandModes[2] == 0)
                val address = getValue(2, computer)
                if (first < second) {
                    computer.state[address] = 1
                } else {
                    computer.state[address] = 0
                }
                return computer.position + 3
            }
        }

        object Equals: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                assert(operandModes[2] == 0)
                val address = getValue(2, computer)
                if (first == second) {
                    computer.state[address] = 1
                } else {
                    computer.state[address] = 0
                }
                return computer.position + 3
            }
        }

        object Exit: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Int? {
                // Done
                computer.status = Status.Done
                return null
            }
        }
    }
}