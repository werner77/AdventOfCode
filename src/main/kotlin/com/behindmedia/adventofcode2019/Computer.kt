package com.behindmedia.adventofcode2019

/**
 * Class with the IntCode Computer functionality.
 *
 * This class is used by all puzzles that require the IntCode Computer,
 * it has been refactored to be fully backwards compatible.
 */
class Computer(initialState: List<Long>) {

    companion object {
        fun parseEncodedState(encodedState: String): List<Long> {
            return encodedState.split(",").map { value -> value.toLong() }
        }
    }

    constructor(encodedInitialState: String): this(parseEncodedState(encodedInitialState))

    /**
     * The status of the computer
     */
    enum class Status {
        Initial, Processing, WaitingForInput, Finished
    }

    /**
     * The processing result of the computer. Returns the status and the list of encountered outputs.
     */
    class Result(val status: Status, val outputs: List<Long>) {
        val lastOutput: Long
            get() = outputs.lastOrNull() ?: 0L
    }

    private val _state: MutableMap<Long, Long> = initialState.toMutableMap()
    private val _inputs = mutableListOf<Long>()
    private val _outputs = mutableListOf<Long>()

    /**
     * The status of the computer
     */
    var status = Status.Initial
        private set

    /**
     * Base address for relative address operations
     */
    var baseAddress = 0L
        private set

    /**
     * Position of the instruction pointer
     */
    var position = 0L
        private set

    /**
     * Returns the current state of the computer as read-only map: <address, value>
     */
    val currentState: Map<Long, Long>
        get() = _state.toMap()

    /**
     * Processes with the specified list of inputs
     */
    fun process(inputs: List<Long>): Result {
        status = Status.Processing
        _outputs.clear()
        _inputs.addAll(inputs)
        while (true) {
            val opcode = Opcode.atCurrentPosition(this)
            val operation = Operation.forCode(opcode.code)
            position = operation.perform(this, opcode.operandModes) ?: break
        }
        return Result(status, _outputs.toList())
    }

    /**
     * Processes with a single input
     */
    fun process(input: Long? = null): Result {
        return process(input?.let { listOf(it) } ?: emptyList())
    }

    private fun getValue(address: Long): Long {
        return _state[address] ?: 0
    }

    /**
     * Class describing an Opcode with its operandModes
     */
    private class Opcode(val code: Int, val operandModes: IntArray) {
        companion object {
            fun atCurrentPosition(computer: Computer): Opcode {
                var encodedOpcode = computer.getValue(computer.position).toInt()

                val code = encodedOpcode % 100
                val operandModes = IntArray(3)

                encodedOpcode /= 100
                for (i in 0 until 3) {
                    operandModes[i] = encodedOpcode % 10
                    encodedOpcode /= 10
                    assert(operandModes[i] in 0..2)
                }
                return Opcode(code, operandModes)
            }
        }
    }

    /**
     * Sealed class describing all the possible Operations that are possible
     */
    private sealed class Operation {
        companion object {
            /**
             * Returns the relevant Operation for the integer code
             */
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

        /**
         * Performs the operation on the computer with the specified operandModes.
         *
         * Returns the new position for the instruction pointer or null if processing should be stopped.
         */
        abstract fun perform(computer: Computer, operandModes: IntArray): Long?

        /**
         * Gets the plain value for the operand at the specified zero-based index
         * (relative to the current position of the instruction pointer)
         */
        private fun getValue(index: Int, computer: Computer): Long {
            val effectivePosition = computer.position + 1 + index
            assert(effectivePosition < computer._state.size)
            return computer.getValue(effectivePosition)
        }

        /**
         * Gets a value for the operand at the specified index, taking the supplied operandModes into account.
         */
        protected fun getValue(index: Int, operandModes: IntArray, computer: Computer): Long {
            val value = getValue(index, computer)
            return when(val operandMode = operandModes[index]) {
                0 -> computer.getValue(value)
                1 -> value
                2 -> computer.getValue(computer.baseAddress + value)
                else -> throw IllegalStateException("Invalid operand mode for getValue(): $operandMode")
            }
        }

        /**
         * Gets an address for the operand at the specified index, taking the supplied operandModes into account.
         *
         * An address is different from a plain value in that it only supports operand modes 0 and 2.
         */
        protected fun getAddress(index: Int, operandModes: IntArray, computer: Computer): Long {
            val value = getValue(index, computer)
            return when(val operandMode = operandModes[index]) {
                0 -> value
                2 -> computer.baseAddress + value
                else -> throw IllegalStateException("Invalid operand mode for getAddress(): $operandMode")
            }
        }

        object Add: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                val address = getAddress(2, operandModes, computer)
                computer._state[address] = first + second
                return computer.position + 4
            }
        }

        object Multiply: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                val address = getAddress(2, operandModes, computer)
                computer._state[address] = first * second
                return computer.position + 4
            }
        }

        object Input: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                // Input
                val address = getAddress(0, operandModes, computer)
                val input = computer._inputs.popFirst()
                if (input == null) {
                    // reset position and return status
                    computer.status = Status.WaitingForInput
                    return null
                }
                computer._state[address] = input
                return computer.position + 2
            }
        }

        object Output: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                // Output
                val output = getValue(0, operandModes, computer)
                computer._outputs.add(output)
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
                computer._state[address] = if (first < second) 1L else 0L
                return computer.position + 4
            }
        }

        object Equals: Operation() {
            override fun perform(computer: Computer, operandModes: IntArray): Long? {
                val first = getValue(0, operandModes, computer)
                val second = getValue(1, operandModes, computer)
                val address = getAddress(2, operandModes, computer)
                computer._state[address] = if (first == second) 1L else 0L
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
                computer.status = Status.Finished
                return null
            }
        }
    }
}