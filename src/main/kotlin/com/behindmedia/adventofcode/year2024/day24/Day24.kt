package com.behindmedia.adventofcode.year2024.day24

import com.behindmedia.adventofcode.common.*
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.math.*

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

private data class UnorderedPair<T>(val first: T, val second: T) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherPair = other as? UnorderedPair<*> ?: return false
        if (first != otherPair.first && first != otherPair.second) return false
        if (second != otherPair.first && second != otherPair.second) return false
        return true
    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result += second?.hashCode() ?: 0
        return result
    }

    fun toList(): List<T> = listOf(first, second)
}

private infix fun <A> A.with(that: A): UnorderedPair<A> = UnorderedPair(this, that)

/**
 * Class representing the entires state in an optimized manner:
 *
 * x are the first bitCount bytes
 * y are the second bitCount bytes
 * z are the third bitCount bytes
 * rest of outputs follow after.
 */
private class State(val encoded: String) {
    val bitCount: Int
    val forwardNameIndex: Map<String, Int>
    val reverseNameIndex: Map<Int, String>
    val gatesByInput: Map<Int, Set<Gate>>
    val gatesByOutput: Map<Int, Gate>
    val outputCount: Int

    init {
        val gateData = encoded.splitTrimmed("\n").map {
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
        val gates = gateData.map { (input1, input2, output, operation) ->
            Gate(
                forward[input1]!!,
                forward[input2]!!,
                forward[output]!!,
                operation
            )
        }
        outputCount = gates.size
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

    fun copy(): State = State(this.encoded)
}

private const val debug = false

fun main() = timing {
    val (encodedInput, encodedGates) = read("/2024/day24.txt").splitTrimmed("\n\n")
    val inputs = encodedInput.splitTrimmed("\n").map {
        val (port, value) = it.splitTrimmed(":")
        port to (value.toInt() == 1)
    }.toMap()
    val bitCount = inputs.size / 2 + 1
    val (x, y) = parseInputs(inputs, bitCount)

    // Part 1
    val state = State(encodedGates)

    // Set inputs
    state.setInputs(x, y)

    // Compute state
    state.compute()

    // Print output
    println(state.output)

    // Part 2

    // Find candidates for swaps and the faulty operations
    val (candidatePairs, operations) = state.findErrorsParallel()

    // Try to perform swaps until successful, first trying the faulty operations and expanding with more tests if successful.
    val result = state.trySwaps(candidatePairs, operations)

    // If result found: print it
    println(state.formattedOutputs(result))
}

/**
 * Divides an int range into count number of sub ranges which are as similar in size as possible.
 */
private fun IntRange.divide(count: Int): List<IntRange> {
    val size = this.last - this.first + 1
    val result = mutableListOf<IntRange>()
    var current = this.first
    var remaining = size
    while (remaining > 0) {
        val subSize = remaining / (count - result.size).toDouble().roundToInt()
        result += current until min(current + subSize, this.last + 1)
        current += subSize
        remaining -= subSize
    }
    return result
}

/**
 * Find all errors, testing all bit operations, running on all available cores.
 */
private fun State.findErrorsParallel(): Pair<Set<UnorderedPair<Int>>, Set<BitOperation>> {
    val state = this
    return runBlocking {
        val pairs = mutableSetOf<UnorderedPair<Int>>()
        val operations = mutableSetOf<BitOperation>()
        val ranges = (0 until bitCount - 1).divide(Runtime.getRuntime().availableProcessors())
        val results = ranges.map { range ->
            // Create a copy of the state first, as state is not thread-safe
            val copy = state.copy()
            async {
                withContext(Dispatchers.Default) {
                    // Find the errors for the sub range
                    copy.findErrors(bitRange = range)
                }
            }
        }.awaitAll()

        // Join all results together
        results.fold(pairs to operations) { r, entry ->
            r.first.addAll(entry.first)
            r.second.addAll(entry.second)
            r
        }
    }
}

/**
 * Outputs the formatted pars, joining the sorted outputs to string separated by commas.
 */
private fun State.formattedOutputs(pairs: List<UnorderedPair<Int>>): String {
    return pairs.asSequence().map { it.toList() }.flatten().map { reverseNameIndex[it]!! }.sorted().joinToString(",")
}

/**
 * Class representing an operation on the nth bit with operation index equal to index.
 *
 * index=0: false + false -> false
 * index=1: true + false -> true
 * index=2: false + true -> true
 * index=3: true + true -> true for `bit + 1` (testing carry over)
 */
private data class BitOperation(val bit: Int, val index: Int)

/**
 * Parses the two input values x and y as long from the specified map and bitCount.
 */
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

/**
 * Swaps two outputs
 */
private fun swapOutputs(gate1: Gate, gate2: Gate) {
    val output1 = gate1.output
    val output2 = gate2.output
    gate1.output = output2
    gate2.output = output1
}

/**
 * Tests all bit operations. Operation 0 (all false) does not have to be tested as it will always succeed.
 */
private fun State.testAllBits(): Boolean {
    for (bit in 0 until bitCount - 1) {
        for (index in 1..3) {
            if (!test(BitOperation(bit, index))) {
                return false
            }
        }
    }
    return true
}

/**
 * Performs a set of 100 random additions, verifying the results.
 */
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

/**
 * Tests the specified bit operation against this state.
 */
private fun State.test(operation: BitOperation): Boolean {
    if (operation.index == 0) {
        reset()
        compute()
        if (getZ(operation.bit)) {
            return false
        }
    } else if (operation.index == 1) {
        reset()
        setX(operation.bit, true)
        setY(operation.bit, false)
        compute()
        if (!getZ(operation.bit)) {
            return false
        }
    } else if (operation.index == 2) {
        reset()
        setX(operation.bit, false)
        setY(operation.bit, true)
        compute()
        if (!getZ(operation.bit)) {
            return false
        }
    } else if (operation.index == 3) {
        reset()
        setX(operation.bit, true)
        setY(operation.bit, true)
        compute()
        if (!getZ(operation.bit + 1)) {
            return false
        }
    } else {
        error("Invalid operation index: ${operation.index}")
    }
    return true
}

/**
 * Reachable direction, left is going through the inputs, right is going through the outputs.
 */
private enum class ReachableDirection {
    Left, Right;
}

/**
 * Traverses the graph to find all reachable outputs from start, using the specified direction.
 */
private fun State.reachableOutputs(start: Int, direction: ReachableDirection): Set<Int> {
    val seen = mutableSetOf<Int>()
    val pending = ArrayDeque<Gate>()
    if (direction == ReachableDirection.Left) {
        pending.add(gatesByOutput[start] ?: error("No gate found for output: $start"))
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

/**
 * Performs the specified closure after swapping the specified pairs of outputs.
 *
 * After the closure has been invoked the outputs will be swapped back to their original values.
 */
private inline fun <T> State.swappingOutputs(outputs: Collection<UnorderedPair<Int>>, perform: State.() -> T): T {
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
    return swappingOutputs(listOf(output1 with output2), perform)
}

/**
 * Performs tests for the specified bit range, returning candidate pairs for swapping and faulty operations encountered.
 */
private fun State.findErrors(bitRange: IntRange = 0 until bitCount - 1): Pair<Set<UnorderedPair<Int>>, Set<BitOperation>> {
    val operations = mutableSetOf<BitOperation>()
    val candidatePairs = mutableSetOf<UnorderedPair<Int>>()
    val reachableCache = mutableMapOf<Int, Set<Int>>()
    val currentErrorCount = countErrors()
    for (bit in bitRange) {
        val zIndex = bitCount * 2 + bit
        for (operationIndex in 1..3) {
            val operation = BitOperation(bit, operationIndex)
            if (!test(operation)) {
                // Get all outputs that were set to true, at least one of those must be wrongly connected.
                // This stems from the fact that the end port should have a 1 for each test and does not. A 1 can only
                // come from another 1 in the circuit (because there are no NOT gates) -> at least one of the 1's is wrongly connected.
                val trueOutputs = outputs { _, value -> value == true }
                val finish = if (operationIndex == 3) zIndex + 1 else zIndex
                val reachableOutputsFromFinish = reachableCache.getOrPut(finish) { reachableOutputs(finish, ReachableDirection.Left) }
                if (debug) {
                    println("Faulty operation bit=$bit, operation=$operationIndex, output: $finish")
                    println("Involved: $trueOutputs")
                    println("Reachable inputs: $reachableOutputsFromFinish")
                }
                // Try to swap pairs of left reachable outputs from finish and the candidate faulty outputs
                for (output1 in reachableOutputsFromFinish) {
                    for (output2 in trueOutputs) {
                        if (output1 == output2) continue
                        swappingOutputs(output1, output2) {
                            // Count as valid swap if the error count decreased and the current test case is now successful.
                            val valid = test(operation) && countErrors() < currentErrorCount
                            if (valid) {
                                candidatePairs.add(output1 with output2)
                            }
                        }
                    }
                }
                operations += operation
            }
        }
    }
    return candidatePairs to operations
}

/**
 * Count the number of errors which were find by performing all tests on the current state.
 */
private fun State.countErrors(): Int {
    var errorCount = 0
    for (bit in 0 until bitCount - 1) {
        for (index in 1..3) {
            if (!test(BitOperation(bit, index))) {
                errorCount++
            }
        }
    }
    return errorCount
}

/**
 * Tries all permutations of distinct sets of pairs, swapping them and performing tests.
 *
 * If a valid set of swaps is found the result is returned.
 */
private fun State.trySwaps(
    candidatePairs: Set<UnorderedPair<Int>>,
    operations: Set<BitOperation>
): List<UnorderedPair<Int>> {
    return candidatePairs.permute(count = 4, mode = PermuteMode.UniqueSets) { pairs ->
        val allOutputs = pairs.fold(mutableSetOf<Int>()) { s, value ->
            s += value.first
            s += value.second
            s
        }
        val valid = (allOutputs.size == 8) && swappingOutputs(outputs = pairs) {
            operations.all { test(it) } && testAllBits() && testRandomAdditions()
        }
        if (valid) {
            pairs.toList()
        } else {
            null
        }
    } ?: error("No result found")
}