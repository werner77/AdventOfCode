package com.behindmedia.adventofcode.year2024.day24

import com.behindmedia.adventofcode.common.*
import kotlin.random.Random


private enum class Operation {
    XOR, AND, OR;

    fun invoke(input1: Boolean, input2: Boolean): Boolean {
        return when (this) {
            XOR -> input1 xor input2
            AND -> input1 and input2
            OR -> input1 or input2
        }
    }
}

/**
 * Gate which performs an operation on two input and sends state to output.
 *
 * Output can be swapped (which is why it is a var)
 */
private data class Gate(val input1: Int, val input2: Int, var output: Int, val operation: Operation)


/**
 * Class representing the entires state in an optimized manner:
 *
 * x are the first bitCount bytes
 * y are the second bitCount bytes
 * z are the third bitCount bytes
 * rest of outputs follow after.
 */
private class State(encodedGates: String) {
    val bitCount: Int
    val forwardNameIndex: Map<String, Int>
    val reverseNameIndex: Map<Int, String>
    val gatesByInput: Map<Int, Set<Gate>>
    val gatesByOutput: Map<Int, Gate>
    private val gates: List<Gate>
    val outputCount: Int
        get() = gates.size

    init {
        val gateData = encodedGates.splitTrimmed("\n").map {
            val (input, output) = it.splitTrimmed("->")
            val (input1, operation, input2) = input.splitTrimmed(" ")
            Quadruple(input1, input2, output, Operation.valueOf(operation))
        }
        bitCount =
            gateData.map { it.third }.filter { it.startsWith("z") }.map { it.substringAfter("z").toInt() }.max() + 1
        val forward = mutableMapOf<String, Int>()
        val reverse = mutableMapOf<Int, String>()
        var j = 0
        for (prefix in listOf("x", "y", "z")) {
            for (i in 0 until bitCount) {
                val output = "$prefix%02d".format(i)
                forward[output] = j
                reverse[j] = output
                j++
            }
        }
        for (output in gateData.map { it.third }.filter { !it.startsWith("z") }) {
            forward[output] = j
            reverse[j] = output
            j++
        }
        forwardNameIndex = forward
        reverseNameIndex = reverse
        gates = gateData.map { (input1, input2, output, operation) ->
            Gate(
                forward[input1]!!,
                forward[input2]!!,
                forward[output]!!,
                operation
            )
        }
        gatesByInput = gates.fold(mutableMapOf<Int, MutableSet<Gate>>()) { map, gate ->
            map.getOrPut(gate.input1) { mutableSetOf() }.add(gate)
            map.getOrPut(gate.input2) { mutableSetOf() }.add(gate)
            map
        }
        gatesByOutput = gates.associateBy { it.output }
    }

    private val data = Array(bitCount * 2 + outputCount) { if (it < bitCount * 2) false else null }

    /**
     * Resets the entire state to initial values
     */
    fun reset() {
        data.fill(false, 0, bitCount * 2)
        data.fill(null, bitCount * 2, data.size)
    }

    /**
     * Sets the inputs x and y to the specified values
     */
    fun setInputs(x: Long, y: Long) {
        reset()
        for (bit in 0 until bitCount) {
            val mask = 1L shl bit
            if (x and mask == mask) {
                data[bit] = true
            }
            if (y and mask == mask) {
                data[bitCount + bit] = true
            }
        }
    }

    /**
     * Sets specified bit of the x input to the specified value
     */
    fun setX(bit: Int, value: Boolean) {
        data[bit] = value
    }

    /**
     * Sets specified bit of the y input to the specified value
     */
    fun setY(bit: Int, value: Boolean) {
        data[bitCount + bit] = value
    }

    /**
     * Gets specified bit of the z output
     */
    fun getZ(bit: Int): Boolean {
        return data[bitCount * 2 + bit] == true
    }

    /**
     * Gets the state for the item at the specified index
     */
    operator fun get(index: Int): Boolean? {
        return data[index]
    }

    /**
     * Sets the state for the item at the specified index
     */
    operator fun set(index: Int, value: Boolean) {
        data[index] = value
    }

    val output: Long
        get() {
            var result = 0L
            val offset = bitCount * 2
            for (bit in 0 until bitCount) {
                if (data[offset + bit]!!) {
                    // Set this bit
                    result = result or (1L shl bit)
                }
            }
            return result
        }

    fun outputs(predicate: (Int, Boolean?) -> Boolean): List<Int> {
        val offset = bitCount * 2
        val result = mutableListOf<Int>()
        for (index in offset until data.size) {
            val value = data[index]
            if (predicate.invoke(index, value)) {
                result += index
            }
        }
        return result
    }

    fun compute() {
        val state = this
        // Process all switches for which two inputs are available
        val pending = ArrayDeque<Gate>()
        for (i in 0 until state.bitCount * 2) {
            for (switch in (gatesByInput[i] ?: emptySet())) {
                if (state[switch.input1] != null && state[switch.input2] != null) {
                    pending.add(switch)
                }
            }
        }
        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            val value1 = state[current.input1] ?: error("No state found for ${current.input1}")
            val value2 = state[current.input2] ?: error("No state found for ${current.input2}")
            val value = current.operation.invoke(value1, value2)
            state[current.output] = value
            for (next in gatesByInput[current.output] ?: emptySet()) {
                if (state[next.input1] != null && state[next.input2] != null) {
                    pending.add(next)
                }
            }
        }
    }

    fun compute(value1: Long, value2: Long): Long {
        setInputs(value1, value2)
        compute()
        return output
    }
}

private const val debug = false

fun main() {
    val (first, second) = read("/2024/day24.txt").splitTrimmed("\n\n")

    val inputs = first.splitTrimmed("\n").map {
        val (port, value) = it.splitTrimmed(":")
        port to (value.toInt() == 1)
    }.toMap()
    val bitCount = inputs.size / 2 + 1
    val (x, y) = parseInputs(inputs, bitCount)

    val state = State(second)
    state.setInputs(x, y)
    state.compute()

    // Part 1
    println(state.output)

    // Part 2
    val (candidatePairs, operations) = state.findErrors()
    val result = state.trySwaps(candidatePairs, operations)
    println(state.formattedOutputs(result))
}

private fun State.formattedOutputs(pairs: List<Pair<Int, Int>>): String {
    return pairs.asSequence().map { listOf(it.first, it.second) }.flatten().map { reverseNameIndex[it]!! }.sorted().joinToString(",")
}

private data class BitOperation(val bit: Int, val index: Int)

private fun parseInputs(inputs: Map<String, Boolean>, bitCount: Int): Pair<Long, Long> {
    var x = 0L
    var y = 0L
    for (bit in 0 until bitCount) {
        val mask = 1L shl bit
        if (inputs["x%02d".format(bit)] == true) {
            x = x or mask
        }
        if (inputs["y%02d".format(bit)] == true) {
            y = y or mask
        }
    }
    return x to y
}

private fun swapOutputs(gate1: Gate, gate2: Gate) {
    val output1 = gate1.output
    val output2 = gate2.output
    gate1.output = output2
    gate2.output = output1
}

private fun State.testAllBits(): Boolean {
    for (bit in 0 until bitCount - 1) {
        for (operationIndex in 0..3) {
            if (!test(bit, operationIndex)) {
                return false
            }
        }
    }
    return true
}

private fun State.testRandomAdditions(): Boolean {
    val mask = (1L shl bitCount - 1) - 1L
    val random = Random(0)
    for (i in 0 until 100) {
        val i1 = random.nextLong() and mask
        val i2 = random.nextLong() and mask
        val i3 = compute(i1, i2)
        if (i1 + i2 != i3) return false
    }
    return true
}

private fun State.test(bit: Int, operationIndex: Int): Boolean {
    if (operationIndex == 0) {
        this.reset()
        compute()
        if (this.getZ(bit)) {
            return false
        }
    } else if (operationIndex == 1) {
        this.reset()
        this.setX(bit, true)
        this.setY(bit, false)
        compute()
        if (!this.getZ(bit)) {
            return false
        }
    } else if (operationIndex == 2) {
        this.reset()
        this.setX(bit, false)
        this.setY(bit, true)
        compute()
        if (!this.getZ(bit)) {
            return false
        }
    } else if (operationIndex == 3) {
        this.reset()
        this.setX(bit, true)
        this.setY(bit, true)
        compute()
        if (!this.getZ(bit + 1)) {
            return false
        }
    } else {
        error("Invalid operation index: $operationIndex")
    }
    return true
}

private enum class ReachableDirection {
    Left, Right;
}

private fun State.reachableOutputs(start: Int, direction: ReachableDirection): Set<Int> {
    val seen = mutableSetOf<Int>()
    val pending = ArrayDeque<Gate>()
    if (direction == ReachableDirection.Left) {
        pending.add(gatesByOutput[start]!!)
    } else {
        pending.addAll(gatesByInput[start] ?: emptySet())
    }
    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()
        if (!seen.add(current.output)) continue

        if (direction == ReachableDirection.Left) {
            gatesByOutput[current.input1]?.let { pending.add(it) }
            gatesByOutput[current.input2]?.let { pending.add(it) }
        } else {
            for (next in gatesByInput[current.output] ?: emptySet()) {
                pending.add(next)
            }
        }
    }
    return seen
}

private inline fun <T> State.swappingOutputs(outputs: Collection<Pair<Int, Int>>, perform: State.() -> T): T {
    val firstGates = outputs.map { gatesByOutput[it.first] ?: error("Invalid output: ${it.first}") }
    val secondGates = outputs.map { gatesByOutput[it.second] ?: error("Invalid output: ${it.second}") }
    try {
        for (i in firstGates.indices) {
            val first = firstGates[i]
            val second = secondGates[i]
            swapOutputs(first, second)
        }
        return perform.invoke(this)
    } finally {
        for (i in firstGates.indices) {
            val first = firstGates[i]
            val second = secondGates[i]
            swapOutputs(first, second)
        }
    }
}

private inline fun <T> State.swappingOutputs(output1: Int, output2: Int, perform: State.() -> T): T {
    return swappingOutputs(listOf(output1 to output2), perform)
}

private fun State.findErrors(): Pair<List<Pair<Int, Int>>, List<BitOperation>> {
    val operations = mutableListOf<BitOperation>()
    val candidatePairs = mutableSetOf<Set<Int>>()
    val currentErrorCount = countErrors()
    for (bit in 0 until bitCount - 1) {
        val zIndex = bitCount * 2 + bit
        for (operationIndex in 1..3) {
            if (!test(bit, operationIndex)) {
                // Get all outputs that were set to true, at least one of those must be wrongly connected
                val switchedOutputs = outputs { _, value -> value == true }
                val finish = if (operationIndex == 3) zIndex + 1 else zIndex
                val reachableOutputsFromFinish = reachableOutputs(finish, ReachableDirection.Left)

                if (debug) {
                    println("Faulty operation bit=$bit, operation=$operationIndex, output: $zIndex")
                    println("Involved: $switchedOutputs")
                    println("Reachable inputs: $reachableOutputsFromFinish")
                }

                // Try to swap pairs of reachable inputs and switched outputs
                for (output1 in reachableOutputsFromFinish) {
                    for (output2 in switchedOutputs) {
                        if (output1 == output2) continue
                        swappingOutputs(output1, output2) {
                            val errorCount = countErrors()
                            // Count as valid swap if the error count did not increase and the current test case is successful
                            val valid = errorCount <= currentErrorCount && this.test(bit, operationIndex)
                            if (valid) {
                                candidatePairs.add(setOf(output1, output2))
                            }
                        }
                    }
                }
                operations += BitOperation(bit, operationIndex)
            }
        }
    }
    return candidatePairs.map { it.first() to it.last() } to operations
}

private fun State.countErrors(): Int {
    var errorCount = 0
    for (bit in 0 until bitCount - 1) {
        for (operationIndex in 1..3) {
            if (!this.test(bit, operationIndex)) {
                errorCount++
            }
        }
    }
    return errorCount
}

private fun State.trySwaps(candidatePairs: List<Pair<Int, Int>>, operations: List<BitOperation>): List<Pair<Int, Int>> {
    return candidatePairs.permute(maxSize = 4) { pairs ->
        val allOutputs = pairs.fold(mutableSetOf<Int>()) { s, value ->
            s += value.first
            s += value.second
            s
        }
        val valid = (allOutputs.size == 8) && swappingOutputs(outputs = pairs) {
            operations.all {
                test(it.bit, it.index)
            } && testAllBits() && testRandomAdditions()
        }
        if (valid) {
            pairs.toList()
        } else {
            null
        }
    } ?: error("No result found")
}