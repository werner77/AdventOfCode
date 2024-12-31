package com.behindmedia.adventofcode.year2024.day24

import com.behindmedia.adventofcode.common.*
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.math.*

/**
 * Boolean operation
 */
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
 * Class representing the entire state in an optimized manner:
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
    val gates: Array<Gate>
    val gatesByInput: Array<out Set<Gate>>
    val gatesByOutput: Array<Gate?>
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
        gates = gateData.map { (input1, input2, output, operation) ->
            Gate(
                forward[input1]!!,
                forward[input2]!!,
                forward[output]!!,
                operation
            )
        }.toTypedArray()
        outputCount = gates.size
        gatesByInput = gates.fold(Array<MutableSet<Gate>>(j) { mutableSetOf() }) { map, gate ->
            map[gate.input1].add(gate)
            map[gate.input2].add(gate)
            map
        }
        val lookupMap = gates.associateBy { it.output }
        gatesByOutput = Array(j) { index ->
            lookupMap[index]
        }
    }

    private val data = Array(bitCount * 2 + outputCount) { if (it < bitCount * 2) 0 else -1 }

    /**
     * Resets the entire state to initial values
     */
    fun reset() {
        data.fill(0, 0, bitCount * 2)
        data.fill(-1, bitCount * 2, data.size)
    }

    /**
     * Sets the inputs x and y to the specified values
     */
    fun setInputs(x: Long, y: Long) {
        reset()
        for (bit in 0 until bitCount) {
            val mask = 1L shl bit
            if (x and mask == mask) {
                data[bit] = 1
            }
            if (y and mask == mask) {
                data[bitCount + bit] = 1
            }
        }
    }

    /**
     * Sets specified bit of the x input to the specified value
     */
    fun setX(bit: Int, value: Boolean) {
        data[bit] = if (value) 1 else 0
    }

    /**
     * Sets specified bit of the y input to the specified value
     */
    fun setY(bit: Int, value: Boolean) {
        data[bitCount + bit] = if (value) 1 else 0
    }

    /**
     * Gets specified bit of the z output
     */
    fun getZ(bit: Int): Boolean {
        return data[bitCount * 2 + bit] == 1
    }

    /**
     * Gets the state for the item at the specified index
     */
    operator fun get(index: Int): Int {
        return data[index]
    }

    /**
     * Sets the state for the item at the specified index
     */
    operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    /**
     * Gets the z value as long
     */
    val output: Long
        get() {
            var result = 0L
            val offset = bitCount * 2
            for (bit in 0 until bitCount) {
                if (data[offset + bit] == 1) {
                    // Set this bit
                    result = result or (1L shl bit)
                }
            }
            return result
        }

    fun outputs(predicate: (Int, Int) -> Boolean): List<Int> {
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
        fun compute(output: Int): Boolean {
            val current = state[output]
            if (current >= 0) {
                return current == 1
            } else if (current == -2) {
                throw IllegalStateException("Cycle")
            } else {
                require(current == -1)
                state[output] = -2
                val gate = gatesByOutput[output] ?: error("No gate found for output: $output")
                val result = gate.operation.invoke(compute(gate.input1), compute(gate.input2))
                state[gate.output] = if (result) 1 else 0
                return result
            }
        }
        for (i in 0 until bitCount) {
            compute(bitCount * 2 + i)
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
private fun State.swapOutputs(gate1: Gate, gate2: Gate) {
    val output1 = gate1.output
    val output2 = gate2.output
    gatesByOutput[output1] = gate2
    gatesByOutput[output2] = gate1
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
    val seen = HashSet<Int>(outputCount * 2)
    fun dfs(gate: Gate) {
        if (seen.add(gate.output)) {
            if (direction == ReachableDirection.Left) {
                gatesByOutput[gate.input1]?.let { dfs(it) }
                gatesByOutput[gate.input2]?.let { dfs(it) }
            } else {
                for (next in gatesByInput[gate.output]) {
                    dfs(next)
                }
            }
        }
    }
    if (direction == ReachableDirection.Left) {
        dfs(gatesByOutput[start] ?: error("No gate found for output: $start"))
    } else {
        gatesByInput[start].forEach { dfs(it) }
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
                // An error occurred for this test:
                //
                // Get all outputs that were set to true, at least one of those must be wrongly connected.
                // This stems from the fact that the end port should have a 1 for each test and does not. A 1 can only
                // come from another 1 in the circuit (because there are no NOT gates) -> at least one of the 1's is wrongly connected.
                //
                // Proof out of contradiction:
                //
                // Assume all true outputs are correctly connected while any output value is false (which should in fact be true).
                //
                // Any gate XOR, AND, OR would emit the correct value if all true outputs were correctly connected:
                //
                // -> AND is easy to see, because it only emits true it both inputs are true and both are correctly connected so the output should be correct as well.
                // -> OR will only emit true if at least one output is true. If all true outputs are correctly connected then all OR gates will also emit the correct value.
                // -> XOR will only emit true if exactly one output is true. But all true outputs are correctly connected,
                // so we cannot swap a true port from another output to the input of this OR. If we just swap false ports the XOR output will remain the same. (false, false) -> false
                // and (false, true) -> true.
                val trueOutputs = outputs { _, value -> value == 1 }
                val finish = if (operationIndex == 3) zIndex + 1 else zIndex

                // Get any node which is reachable from the output node for which the test case failed.
                // At least one node in this reachable graph must be wrongly connected.
                val reachableOutputsFromFinish =
                    reachableCache.getOrPut(finish) { reachableOutputs(finish, ReachableDirection.Left) }
                if (debug) {
                    println("Faulty operation bit=$bit, operation=$operationIndex, output: $finish")
                    println("Involved: $trueOutputs")
                    println("Reachable inputs: $reachableOutputsFromFinish")
                }
                // Try to swap pairs of left reachable outputs from finish and the candidate faulty outputs.
                for (output1 in reachableOutputsFromFinish) {
                    for (output2 in trueOutputs) {
                        if (output1 == output2) continue
                        swappingOutputs(output1, output2) {
                            // Count as valid swap if the error count decreased and the current test case is now successful.
                            // We could test explicitly for cycles as well, but the test would never succeed if a cycle is created.
                            // This condition assumes all "good" swaps are fully independent. The condition can be relaxed with
                            // assuming that only the total error count decreases or even allow it to stay the same.
                            // The run time will be longer, but the same result will be found.
                            val valid = try {
                                test(operation) && countErrors() < currentErrorCount
                            } catch (e: IllegalStateException) {
                                // Created a cycle
                                false
                            }
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

private fun State.hasCycles(): Boolean {
    // Try to topologically sort the graph. If this fails, then there are cycles
    val inDegrees = defaultMutableMapOf<Int, Int> { 0 }
    for (gate in gates) {
        require(gate.input1 != gate.input2)
        if (gate.input1 == gate.output) return true
        if (gate.input2 == gate.output) return true
        inDegrees[gate.output] += 2
    }
    val pending = ArrayDeque<Int>()
    (0 until 2 * bitCount).forEach {
        pending.add(it)
    }
    val seen = mutableSetOf<Int>()
    while (pending.isNotEmpty()) {
        val next = pending.removeFirst()
        if (!seen.add(next)) {
            return true
        }
        val gates = gatesByInput[next] ?: emptySet()
        for (gate in gates) {
            val newValue = inDegrees[gate.output] - 1
            require(newValue >= 0)
            inDegrees[gate.output] = newValue
            if (newValue == 0) {
                pending.add(gate.output)
            }
        }
    }
    return false
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
            // First test only the failed operations (for speed purposes)
            // Then test all bits
            // Finally test 100 random additions (should not be strictly necessary)
            try {
                operations.all { test(it) } && testAllBits() && testRandomAdditions()
            } catch (e: IllegalStateException) {
                // Created a cycle
                false
            }
        }
        if (valid) {
            pairs.toList()
        } else {
            null
        }
    } ?: error("No result found")
}