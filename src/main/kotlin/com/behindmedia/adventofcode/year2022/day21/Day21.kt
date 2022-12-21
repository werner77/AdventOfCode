package com.behindmedia.adventofcode.year2022.day21

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.year2022.day21.Formula.Evaluation
import com.behindmedia.adventofcode.year2022.day21.Formula.Operation
import com.behindmedia.adventofcode.year2022.day21.Formula.Constant
import com.behindmedia.adventofcode.year2022.day21.OperationType.Div
import com.behindmedia.adventofcode.year2022.day21.OperationType.Minus
import com.behindmedia.adventofcode.year2022.day21.OperationType.Mult
import com.behindmedia.adventofcode.year2022.day21.OperationType.Plus
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import java.util.concurrent.atomic.AtomicLong

private enum class OperationType {
    Div, Mult, Plus, Minus;

    companion object {
        fun fromString(string: String): OperationType {
            return when (string) {
                "*" -> Mult
                "-" -> Minus
                "+" -> Plus
                "/" -> Div
                else -> error("Unknown operation: $string")
            }
        }
    }
}

private sealed class Formula {

    abstract val id: String

    abstract fun compute(data: Map<String, Formula>): Long

    data class Constant(override val id: String, val value: Long) : Formula() {
        override fun compute(data: Map<String, Formula>): Long {
            return value
        }
    }

    data class Operation(override val id: String, val left: String, val right: String, val type: OperationType) : Formula() {
        override fun compute(data: Map<String, Formula>): Long {
            val l = data[left] ?: error("Could not find: $left")
            val r = data[right] ?: error("Could not find: $right")
            val leftValue = l.compute(data)
            val rightValue = r.compute(data)
            return when (type) {
                Div -> leftValue / rightValue
                Mult -> leftValue * rightValue
                Plus -> leftValue + rightValue
                Minus -> leftValue - rightValue
            }
        }
    }

    data class Evaluation(override val id: String, val evaluator: () -> Long) : Formula() {
        override fun compute(data: Map<String, Formula>): Long {
            return evaluator.invoke()
        }
    }
}

fun main() {
    val data: Map<String, Formula> = parseLines("/2022/day21.txt") { line ->
        val components = line.split(":").map { it.trim() }
        val monkey = components[0]
        val literal = components[1].toLongOrNull()
        if (literal != null) {
            Constant(monkey, literal)
        } else {
            val (left, type, right) = components[1].split(" ").map { it.trim() }
            Operation(monkey, left, right, OperationType.fromString(type))
        }
    }.fold(mutableMapOf()) { map, op ->
        map.apply {
            put(op.id, op)
        }
    }

    val root: Operation = data["root"] as? Operation ?: error("Root not found")

    timing {
        // Part 1
        part1(root, data)
    }
    timing {
        // Part 2
        part2(data, root)
    }
}

private fun part1(
    root: Operation,
    data: Map<String, Formula>
) {
    println(root.compute(data))
}

private fun part2(
    formulas: Map<String, Formula>,
    root: Operation
) {
    require(formulas["humn"] != null)
    val f1 = formulas[root.left] ?: error("Root has no left value")
    val f2 = formulas[root.right] ?: error("Root has no right value")
    val x = AtomicLong(0)
    val evaluation = Evaluation("humn") {
        x.get()
    }
    val modifiedFormulas = formulas + (evaluation.id to evaluation)
    val f: (Long) -> Long = { input ->
        x.set(input)
        f1.compute(modifiedFormulas) - f2.compute(modifiedFormulas)
    }

    // Find all ranges where sign(f(min)) != sign(f(max))
    val ranges = findRangesWithOppositeSign(f)
    for ((min, max) in ranges) {
        // First find a [x1, x2] such that F(x) has opposite signs for x1 and x2
        // Then half the interval on both sides and take the side where the above is still true
        val ans = bisect(min, max) {
            f(it)
        } ?: continue
        println(ans)
        break
    }
}

/**
 * Find all ranges [x1, x2] where sign(f(x1)) != sign(f(x2)), sorted by size of the range in ascending order.
 * Note that some bigger numbers may actually produce overflow.
 */
private fun findRangesWithOppositeSign(f: (Long) -> Long): MutableList<Pair<Long, Long>> {
    val ranges = mutableListOf<Pair<Long, Long>>()
    for (startValue in listOf(Long.MIN_VALUE, Long.MAX_VALUE)) {
        var value = startValue
        var lastY: Long? = null
        while (true) {
            val y = f(value)
            if (lastY != null && lastY.sign != y.sign) {
                val a = value
                val b = 2 * value
                ranges.add(Pair(min(a, b), max(a, b)))
            }
            if (value == 0L) break
            lastY = y
            value /= 2
        }
    }
    // Sort by the size of the range, to evaluate the cheapest first
    ranges.sortBy { abs(it.first - it.second) }
    return ranges
}

/*
    Bisect method to find the roots a function f(x).
    We need an interval [min,max] where:

        (1): sign(f(min)) != sign(f(max))

    This means that the root of the function lies in that interval.
    Then we will half the interval and stop evaluating if condition (1) is not true anymore.
    Recurse into ever smaller halves until min == max and f(min) == 0
 */
private fun bisect(min: Long, max: Long, f: (Long) -> Long): Long? {
    // Exit conditions
    if (min > max) {
        return null
    } else if (min == max) {
        return if (f(min) == 0L) {
            // Found root
            min
        } else {
            null
        }
    }

    val minValue = f(min)
    val maxValue = f(max)

    if (minValue.sign == maxValue.sign) {
        // Same sign, exit
        return null
    }

    // bisect this interval into two halves
    val mid = (min + max) / 2
    return bisect(min, mid, f) ?: bisect(mid + 1, max, f)
}