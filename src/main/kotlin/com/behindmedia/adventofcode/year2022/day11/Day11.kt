package com.behindmedia.adventofcode.year2022.day11

import com.behindmedia.adventofcode.common.product
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.splitNonEmptySequence
import com.behindmedia.adventofcode.common.timing
import com.behindmedia.adventofcode.common.whenNotNull

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
            val targetMonkey =
                monkeyMap[targetMonkeyIdentifier] ?: error("No monkey found for identifier: $targetMonkeyIdentifier")
            require(targetMonkeyIdentifier != identifier)
            targetMonkey.items += newItem
            this.inspectedItemCount++
        }
        items.clear()
    }
}

private fun parseMonkey(text: String): Monkey {
    val monkeyPattern = """Monkey (\d+):""".toRegex()
    val startingItemsPattern = """Starting items: (.*)""".toRegex()
    val operationPattern = """Operation: new = (.*)""".toRegex()
    val testPattern = """Test: divisible by (\d+)""".toRegex()
    val ifTruePattern = """If true: throw to monkey (\d+)""".toRegex()
    val ifFalsePattern = """If false: throw to monkey (\d+)""".toRegex()
    val operationPattern1 = """old \* old""".toRegex()
    val operationPattern2 = """old \* (\d+)""".toRegex()
    val operationPattern3 = """old \+ (\d+)""".toRegex()
    var currentMonkeyIdentifier: Int? = null
    var currentStartItems: List<Long>? = null
    var currentOperationFunction: OperationFunction? = null
    var currentDivisor: Long? = null
    var trueMonkey: Int? = null
    var falseMonkey: Int? = null
    text.split("\n").map { it.trim() }.filter { it.isNotBlank() }.forEach { line ->
        whenNotNull(monkeyPattern.matchEntire(line)?.destructured) { (identifier) ->
            currentMonkeyIdentifier = identifier.toInt()
        } ?: whenNotNull(startingItemsPattern.matchEntire(line)?.destructured) { (itemValues) ->
            val startItems = itemValues.splitNonEmptySequence(" ", ",").map { it.toLong() }.toList()
            currentStartItems = startItems
        } ?: whenNotNull(operationPattern.matchEntire(line)?.destructured) { (operation) ->
            whenNotNull(operationPattern1.matchEntire(operation)) {
                currentOperationFunction = { it * it }
            } ?: whenNotNull(operationPattern2.matchEntire(operation)?.destructured) { (multiplier) ->
                currentOperationFunction = { it * multiplier.toLong() }
            } ?: whenNotNull(operationPattern3.matchEntire(operation)?.destructured) { (delta) ->
                currentOperationFunction = { it + delta.toLong() }
            } ?: error("Invalid operation: $operation")
        } ?: whenNotNull(testPattern.matchEntire(line)?.destructured) { (divisor) ->
            currentDivisor = divisor.toLong()
        } ?: whenNotNull(ifTruePattern.matchEntire(line)?.destructured) { (identifier) ->
            trueMonkey = identifier.toInt()
        } ?: whenNotNull(ifFalsePattern.matchEntire(line)?.destructured) { (identifier) ->
            falseMonkey = identifier.toInt()
        } ?: error("Invalid line: '$line'")
    }
    return Monkey(
        identifier = currentMonkeyIdentifier!!,
        startItems = currentStartItems!!,
        operation = currentOperationFunction!!,
        testDivisor = currentDivisor!!,
        onTrueMonkey = trueMonkey!!,
        onFalseMonkey = falseMonkey!!
    )
}

fun main() {
    val monkeys = read("/2022/day11.txt").split("\n\n")
        .filter { it.isNotBlank() }
        .map { parseMonkey(it) }
        .fold(linkedMapOf<Int, Monkey>()) { map, monkey ->
            map.apply {
                put(monkey.identifier, monkey)
            }
        }

    timing {
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
}

private fun simulate(
    monkeys: Map<Int, Monkey>,
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
