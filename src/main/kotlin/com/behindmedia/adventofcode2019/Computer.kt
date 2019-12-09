package com.behindmedia.adventofcode2019

class Computer(initialState: List<Long>) {

    enum class Status {
        Initial, Processing, WaitingForInput, Done
    }

    private var position: Long = 0L
    private val state: MutableMap<Long, Long> = initialState.foldIndexed(mutableMapOf<Long, Long>()) { address, map, value ->
        map[address.toLong()] = value
        map
    }
    private val inputStack = mutableListOf<Long>()
    private var baseAddress: Long = 0L
    private val outputs = mutableListOf<Long>()

    val lastOutput: Long
        get() = outputs.lastOrNull() ?: 0L

    val allOutputs: List<Long>
        get() = outputs.toList()

    var status = Status.Initial
        private set

    val currentState: Map<Long, Long>
        get() = state.toMap()

    fun process(inputs: List<Long>): Long {
        status = Status.Processing
        inputStack.addAll(inputs)
        while (true) {
            val opcode = Opcode.at(position, this)
            val operation = Operation.forCode(opcode.code)
            position = operation.perform(this, opcode.operandModes) ?: break
        }
        return lastOutput
    }

    private fun getValue(address: Long): Long {
        return state[address] ?: 0
    }

    private class Opcode(val code: Int, val operandModes: IntArray) {
        companion object {
            fun at(position: Long, computer: Computer): Opcode {
                var encodedOpcode = computer.getValue(position).toInt()

                println("Opcode: $encodedOpcode")

                val code = encodedOpcode % 100
                val operandModes = IntArray(3)

                encodedOpcode /= 100
                for (i in 0 until 3) {
                    operandModes[i] = encodedOpcode % 10
                    encodedOpcode /= 10
                    assert(operandModes[i] == 0 || operandModes[i] == 1 || operandModes[i] == 2)
                }
                return Opcode(code, operandModes)
            }
        }
    }

    private sealed class Operation {
        companion object {
            fun forCode(code: Int): Operation {
                return when(code) {
                    1 -> Add
                    2 -> Multiply
                    3 -> Input
                    4 -> Output
                    5 -> JumpIfTrue
                    6 -> JumpIfFalse
                    7 -> LessThan
                    8 -> Equals
                    9 -> OffsetBaseAddress
                    99 -> Exit
                    else -> throw IllegalStateException("Unknown opcode encountered: $code")
                }
            }
        }

        abstract fun perform(computer: Computer, operandModes: IntArray): Long?

        protected fun getValue(index: Int, operandModes: IntArray, computer: Computer): Long {
            val value = getValue(index, computer)
            return when(val operandMode = operandModes[index]) {
                0 -> computer.getValue(value)
                1 -> value
                2 -> computer.getValue(computer.baseAddress + value)
                else -> throw IllegalStateException("Unknown operand mode: $operandMode")
            }
        }

        protected fun getValue(index: Int, computer: Computer): Long {
            val effectivePosition = computer.position + 1 + index
            assert(effectivePosition < computer.state.size)
            return computer.getValue(effectivePosition)
        }

        protected fun getAddress(index: Int, operandModes: IntArray, computer: Computer): Long {
            val value = getValue(index, computer)
            return when(val operandMode = operandModes[index]) {
                0 -> value
                2 -> {
                    println("Encountered relative address")
                    computer.baseAddress + value
                }
                else -> throw IllegalStateException("Unknown operand mode: $operandMode")
            }
        }

        object Add: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                val address = getAddress(2, operandModes, computer)
                computer.state[address] = first + second
                return computer.position + 4
            }
        }

        object Multiply: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                val address = getAddress(2, operandModes, computer)
                computer.state[address] = first * second
                return computer.position + 4
            }
        }

        object Input: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                // Input
                val address = getAddress(0, operandModes, computer)
                val input = computer.inputStack.popFirst()
                if (input == null) {
                    // reset position and return status
                    computer.status = Status.WaitingForInput
                    return null
                }
                computer.state[address] = input
                return computer.position + 2
            }
        }

        object Output: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                // Output
                val output = getValue(0, operandModes, computer)
                computer.outputs.add(output)
                return computer.position + 2
            }
        }

        object JumpIfTrue: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                return if (first != 0L) second else computer.position + 3
            }
        }

        object JumpIfFalse: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                return if (first == 0L) second else computer.position + 3
            }
        }

        object LessThan: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                val address = getAddress(2, operandModes, computer)
                if (first < second) {
                    computer.state[address] = 1
                } else {
                    computer.state[address] = 0
                }
                return computer.position + 4
            }
        }

        object Equals: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                val address = getAddress(2, operandModes, computer)
                if (first == second) {
                    computer.state[address] = 1
                } else {
                    computer.state[address] = 0
                }
                return computer.position + 4
            }
        }

        object OffsetBaseAddress: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val offset = getValue(0, operandModes, computer)
                computer.baseAddress += offset
                return computer.position + 2
            }
        }

        object Exit: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                // Done
                computer.status = Status.Done
                return null
            }
        }
    }
}