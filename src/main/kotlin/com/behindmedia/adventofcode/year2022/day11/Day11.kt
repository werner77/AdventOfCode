package com.behindmedia.adventofcode.year2022.day11

import com.behindmedia.adventofcode.common.*

private typealias OperationFunction = (Long) -> Long

private data class Monkey(
    val identifier: Int,
    val startItems: List<Long>,
    val operation: OperationFunction,
    val testDivisor: Long,
    val onTrueMonkey: Int,
    val onFalseMonkey: Int
) {
    val items: MutableList<Long> = startItems.toMutableList()
    var inspectedItemCount: Long = 0

    fun reset() {
        inspectedItemCount = 0
        items.clear()
        items.addAll(startItems)
    }

    fun takeTurn(monkeyMap: Map<Int, Monkey>, normalizationFunction: (Long) -> Long) {
        for (item in items) {
            val newValue = operation(item)
            require(newValue > item && newValue > 0) { "Overflow occurred" }
            val newItem = normalizationFunction(newValue)
            require(newItem <= newValue) { "Expected value to be reduced" }
            val targetMonkeyIdentifier = if (newItem % testDivisor == 0L) {
                onTrueMonkey
            } else {
                onFalseMonkey
            }
            val targetMonkey = monkeyMap[targetMonkeyIdentifier] ?: error("No monkey found for identifier: $targetMonkeyIdentifier")
            require(targetMonkeyIdentifier != identifier)
            targetMonkey.items += newItem
            this.inspectedItemCount++
        }
        items.clear()
    }
}

fun main() {
    var currentMonkeyIdentifier: Int? = null
    var currentOperationFunction: OperationFunction? = null
    var currentDivisor: Long? = null
    var currentStartItems: List<Long>? = null
    var currentFalseMonkey: Int? = null
    var currentTrueMonkey: Int? = null
    val monkeys = linkedMapOf<Int, Monkey>()
    val monkeyPattern = """Monkey (\d+):""".toRegex()
    val startingItemsPattern = """Starting items: (.*)""".toRegex()
    val operationPattern = """Operation: new = (.*)""".toRegex()
    val testPattern = """Test: divisible by (\d+)""".toRegex()
    val ifTruePattern = """If true: throw to monkey (\d+)""".toRegex()
    val ifFalsePattern = """If false: throw to monkey (\d+)""".toRegex()
    val operationPattern1 = """old \* old""".toRegex()
    val operationPattern2 = """old \* (\d+)""".toRegex()
    val operationPattern3 = """old \+ (\d+)""".toRegex()

    fun addMonkey() {
        val monkey = Monkey(
            identifier = currentMonkeyIdentifier ?: error("Monkey identifier not initialized"),
            startItems = currentStartItems ?: error("Start items not initialized"),
            operation = currentOperationFunction ?: error("Operation function not initialize"),
            testDivisor = currentDivisor ?: error("Test divisor not initialized"),
            onTrueMonkey = currentTrueMonkey ?: error("True monkey not initialized"),
            onFalseMonkey = currentFalseMonkey ?: error("False monkey not initialized")
        )
        monkeys[monkey.identifier] = monkey
        currentStartItems = null
        currentOperationFunction = null
        currentDivisor = null
        currentTrueMonkey = null
        currentFalseMonkey = null
    }
    parseLines("/2022/day11.txt") { l ->
        val line = l.trim()
        whenNotNull(monkeyPattern.matchEntire(line.trim())?.destructured) { (identifier) ->
            currentMonkeyIdentifier = identifier.toInt()
        } ?: whenNotNull(startingItemsPattern.matchEntire(line)?.destructured) { (itemValues) ->
            val startItems = itemValues.splitNonEmptySequence(" ", ",").map { it.toLong() }.toList()
            currentStartItems = startItems
        } ?: whenNotNull(operationPattern.matchEntire(line)?.destructured) { (operation) ->
            whenNotNull(operationPattern1.matchEntire(operation)) {
                currentOperationFunction = { it * it }
            } ?:
            whenNotNull (operationPattern2.matchEntire(operation)?.destructured) { (multiplier) ->
                currentOperationFunction = { it * multiplier.toLong() }
            } ?:
            whenNotNull(operationPattern3.matchEntire(operation)?.destructured) { (delta) ->
                currentOperationFunction = { it + delta.toLong() }
            } ?: error("Invalid operation: $operation")
        } ?: whenNotNull(testPattern.matchEntire(line)?.destructured) { (divisor) ->
            currentDivisor = divisor.toLong()
        } ?: whenNotNull(ifTruePattern.matchEntire(line)?.destructured) { (identifier) ->
            currentTrueMonkey = identifier.toInt()
        } ?: whenNotNull(ifFalsePattern.matchEntire(line)?.destructured) { (identifier) ->
            currentFalseMonkey = identifier.toInt()
        } ?: run {
            if (line.isBlank()) {
                addMonkey()
            } else {
                error("Invalid line: $line")
            }
        }
    }
    if (currentMonkeyIdentifier != null) addMonkey()

    // Part 1
    simulate(monkeys = monkeys, numberOfRounds = 20) {
        it / 3L
    }

    monkeys.values.forEach { it.reset() }

    // Part 2
    val modulo = monkeys.values.map { it.testDivisor }.product()
    simulate(monkeys = monkeys, numberOfRounds = 10_000) {
        it % modulo
    }
}

private fun simulate(
    monkeys: LinkedHashMap<Int, Monkey>,
    numberOfRounds: Int,
    normalizationFunction: (Long) -> Long
) {
    for (round in 1..numberOfRounds) {
        performRound(monkeys, normalizationFunction)
    }
    for (monkey in monkeys.values) {
        println("Monkey ${monkey.identifier} inspected items ${monkey.inspectedItemCount} times.")
    }
    println(monkeys.values.map { it.inspectedItemCount }.sortedDescending().take(2).product())
}

private fun performRound(monkeys: Map<Int, Monkey>, normalizationFunction: (Long) -> Long) {
    for (monkey in monkeys.values) {
        monkey.takeTurn(monkeyMap = monkeys, normalizationFunction = normalizationFunction)
    }
}
