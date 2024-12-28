package com.behindmedia.adventofcode.common

import kotlin.Comparator
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.math.*
import java.util.function.Function


/**
 * Function to compare doubles with an allowed fractional difference
 */
fun Double.isAlmostEqual(other: Double, allowedDifference: Double = 0.000001): Boolean {
    return abs(this - other) < allowedDifference
}

/**
 * Returns the number of 10-based digits (excluding leading 0-s) of this Long
 */
val Long.numberOfDigits: Int
    get() {
        var value = this
        var digitCount = 0
        while (value != 0L) {
            value /= 10
            digitCount++
        }
        return digitCount
    }

/**
 * Computes the greatest common divisor of two integers.
 */
tailrec fun greatestCommonDivisor(a: Long, b: Long): Long {
    if (b == 0L) {
        return a
    }
    return greatestCommonDivisor(b, a % b)
}

/**
 * Computes the least common multiple of two integers.
 */
fun leastCommonMultiple(a: Long, b: Long): Long {
    return if (a == 0L || b == 0L) 0L else {
        val gcd = greatestCommonDivisor(a, b)
        abs(a * b) / gcd
    }
}

fun LongRange.intersection(other: LongRange): LongRange? {
    return if (other.first <= this.first) {
        if (other.last >= this.last) {
            this
        } else {
            this.first..other.last
        }
    } else {
        if (this.last <= other.last) {
            other.first..this.last
        } else {
            other
        }
    }.takeIf { !it.isEmpty() }
}

/**
 * Takes a list of base/modulo combinations and returns the lowest number for which the states coincide such that:
 *
 * for all i: state(i) == base_state(i).
 *
 * E.g. chineseRemainder((3,4), (5,6), (2,5)) == 47
 */
fun chineseRemainder(values: List<Pair<Long, Long>>): Pair<Long, Long>? {
    if (values.isEmpty()) {
        return null
    }
    var (result, lcm) = values[0]
    outer@ for (i in 1 until values.size) {
        val (base, modulo) = values[i]
        val target = base % modulo
        for (j in 0L until modulo) {
            if (result % modulo == target) {
                lcm = leastCommonMultiple(lcm, modulo)
                continue@outer
            }
            result += lcm
        }
        return null
    }
    return result to lcm
}

fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

inline fun Long.forBits(range: IntRange, perform: (Boolean) -> Unit) {
    for (i in range) {
        val mask = 1L shl i
        val bit = (this and mask) == mask
        perform.invoke(bit)
    }
}

fun Long.divideCeil(other: Long): Long {
    return (this + other - 1) / other
}

fun Long.times(other: Long, modulo: Long, ensurePositive: Boolean): Long {
    var a = this
    var b = other

    var result: Long = 0
    a %= modulo
    while (b > 0) { // If b is odd, add 'a' to result
        if (b % 2L == 1L) {
            result = (result + a) % modulo
        }
        // Multiply 'a' with 2
        a = a * 2 % modulo
        // Divide b by 2
        b /= 2
    }
    result %= modulo
    if (ensurePositive && result < 0) {
        result += modulo
    }
    return result
}


fun <E> Map<Coordinate, E>.printMap(default: E, includeBorder: Boolean = false) {
    var range = this.keys.range()
    if (includeBorder) {
        range = range.inset(Insets.square(-1))
    }
    for (c in range) {
        print(this[c] ?: default)
        if (c.x == range.endInclusive.x) {
            println()
        }
    }
}

fun <E> Map<Coordinate, E>.printMapToString(default: E): String {
    val range = this.keys.range()
    val buffer = StringBuilder()
    for (c in range) {
        buffer.append(this[c] ?: default)
        if (c.x == range.endInclusive.x) {
            buffer.appendLine()
        }
    }
    return buffer.toString()
}

fun <E> Map<Coordinate, E>.printMap(default: (Coordinate) -> E) {
    val range = this.keys.range()
    for (c in range) {
        print(this[c] ?: default.invoke(c))
        if (c.x == range.endInclusive.x) {
            println()
        }
    }
}

fun binarySearch(
    lowerBound: Int,
    upperBound: Int,
    targetValue: Int,
    inverted: Boolean = false,
    evaluation: (Int) -> Int
): Int? {
    return binarySearch(lowerBound, upperBound, inverted) { mid ->
        evaluation(mid) <= targetValue
    }
}

fun binarySearch(
    lowerBound: Long,
    upperBound: Long,
    targetValue: Long,
    inverted: Boolean = false,
    evaluation: (Long) -> Long
): Long? {
    return binarySearch(lowerBound, upperBound, inverted) { mid ->
        evaluation(mid) <= targetValue
    }
}

fun binarySearch(
    lowerBound: Int,
    upperBound: Int,
    inverted: Boolean = false,
    evaluation: (Int) -> Boolean
): Int? = binarySearch(lowerBound.toLong(), upperBound.toLong(), inverted, { evaluation.invoke(it.toInt()) })?.toInt()

fun binarySearch(
    lowerBound: Long,
    upperBound: Long,
    inverted: Boolean = false,
    evaluation: (Long) -> Boolean
): Long? {
    var begin = lowerBound
    var end = upperBound
    var result: Long? = null
    while (begin <= end) {
        val mid = (begin + end) / 2L
        if (evaluation(mid)) {
            result = mid
            if (inverted) {
                end = mid - 1
            } else {
                begin = mid + 1
            }
        } else {
            if (inverted) {
                begin = mid + 1
            } else {
                end = mid - 1
            }
        }
    }
    return result
}


fun <C : Comparable<C>> topologicalSort(
    incomingEdges: Map<C, Collection<C>>
): List<C> {
    return topologicalSort(incomingEdges = incomingEdges, comparator = Comparator.comparing(Function.identity()))
}

fun <C> topologicalSort(
    incomingEdges: Map<C, Collection<C>>,
    comparator: Comparator<C>
): List<C> {
    // Find all nodes with have no incoming edges
    val remainingEdges = mutableMapOf<C, MutableSet<C>>()
    val pending = TreeSet<C>(comparator)
    for ((c, edges) in incomingEdges) {
        if (edges.isEmpty()) {
            // Add to pending
            pending.add(c)
        } else {
            // Add to remaining
            remainingEdges[c] = edges.toMutableSet()
        }
    }
    val result = mutableListOf<C>()
    while (pending.isNotEmpty()) {
        val next = pending.popFirst() ?: error("No element found left")
        result += next
        // Remove next from the incoming edges
        val iterator = remainingEdges.iterator()
        while (iterator.hasNext()) {
            val (c, e) = iterator.next()
            e.remove(next)
            if (e.isEmpty()) {
                pending += c
                iterator.remove()
            }
        }
    }
    if (remainingEdges.isNotEmpty()) error("Graph has a cycle")
    return result
}

fun optimumSearch(startValue: Long, inverted: Boolean = false, process: (Long) -> Long): Long {
    fun compareEval(first: Long, second: Long): Int {
        return if (inverted) second.compareTo(first) else first.compareTo(second)
    }

    var exceededOptimum = false
    var delta = 1
    var currentValue = startValue
    var bestEvaluation = process(currentValue)
    while (true) {
        if (exceededOptimum) {
            val leftEvaluation = process(currentValue - delta)
            val rightEvaluation = process(currentValue + delta)
            val comparisonResult = compareEval(leftEvaluation, rightEvaluation)
            val (currentEvaluation, sign) = if (comparisonResult > 0) {
                Pair(leftEvaluation, -1)
            } else {
                Pair(rightEvaluation, 1)
            }

            if (compareEval(currentEvaluation, bestEvaluation) > 0) {
                currentValue += sign * delta
                bestEvaluation = currentEvaluation
            } else if (delta == 1) {
                process(currentValue)
                return currentValue
            }
            delta = max(1, delta / 2)
        } else {
            // First increase delta by powers of two until the evaluation gets worse
            val currentEvaluation = process(currentValue + delta)
            val comparisonResult = compareEval(currentEvaluation, bestEvaluation)
            if (comparisonResult > 0) {
                currentValue += delta
                delta *= 2
                bestEvaluation = currentEvaluation
            } else {
                exceededOptimum = true
                delta = max(1, delta / 2)
            }
        }
    }
}

fun Int.pow(exp: Int): Int {
    var result = 1
    for (i in 0 until exp) {
        result *= this
    }
    return result
}

fun Long.pow(exp: Int): Long {
    var result = 1L
    for (i in 0 until exp) {
        result *= this
    }
    return result
}

class Primes(maxNumber: Int) {
    private val sieves = sieve(maxNumber)

    private fun sieve(n: Int): IntArray {
        val sieves = IntArray(n + 1) { 0 }
        var i = 2
        while (i * i <= n) {
            if (sieves[i] == 0) {
                var k = i * i
                while (k <= n) {
                    if (sieves[k] == 0) {
                        sieves[k] = i
                    }
                    k += i
                }
            }
            i++
        }
        return sieves
    }

    fun getPrimeFactors(n: Int): Set<Map.Entry<Int, Int>> {
        val factors = defaultMutableMapOf<Int, Int> { 0 }
        var x = n
        while (sieves[x] > 0) {
            factors[sieves[x]] += 1
            x /= sieves[x]
        }
        factors[x] += 1
        return factors.entries
    }

    fun sumOfFactors(x: Int): Int {
        if (x == 1) return 1
        var result = 1
        for ((factor, times) in getPrimeFactors(x)) {
            var sum = 0
            for (i in 0..times) {
                sum += factor.pow(i)
            }
            result *= sum
        }
        return result
    }

    fun getFactors(n: Int): List<Int> {
        var i = 1
        val result = mutableListOf<Int>()
        while (i * i <= n) {
            if (n % i == 0) {
                val d = n / i
                result += d
                if (d != i) result += i
            }
            i++
        }
        return result
    }

    fun isPrime(n: Int): Boolean {
        val factors = getPrimeFactors(n)
        return factors.size == 1 && factors.single().value == 1
    }
}

@JvmInline
value class SafeLong(val value: Long) : Comparable<SafeLong> {
    operator fun unaryPlus(): SafeLong {
        return SafeLong(value)
    }

    operator fun unaryMinus(): SafeLong {
        checkOverflow {
            value == Long.MIN_VALUE
        }
        return SafeLong(-value)
    }

    operator fun inc(): SafeLong {
        checkOverflow {
            value == Long.MAX_VALUE
        }
        return SafeLong(value + 1)
    }

    operator fun dec(): SafeLong {
        checkOverflow {
            value == Long.MIN_VALUE
        }
        return SafeLong(value - 1)
    }

    operator fun plus(b: SafeLong): SafeLong {
        val result = this.value + b.value
        checkOverflow {
            result - b.value != this.value
        }
        return SafeLong(result)
    }

    operator fun minus(b: SafeLong): SafeLong {
        val result = this.value - b.value
        checkOverflow {
            result + b.value != this.value
        }
        return SafeLong(result)
    }

    operator fun times(b: SafeLong): SafeLong {
        val result = this.value * b.value
        checkOverflow {
            this.value != result / b.value
        }
        return SafeLong(result)
    }

    operator fun div(b: SafeLong): SafeLong {
        return SafeLong(this.value / b.value)
    }

    operator fun rem(b: SafeLong): SafeLong {
        return SafeLong(this.value % (b.value))
    }

    override fun compareTo(other: SafeLong): Int {
        return value.compareTo(other.value)
    }
}

private inline fun checkOverflow(test: () -> Boolean) {
    if (test.invoke()) throw IllegalStateException("Overflow occurred")
}

@JvmInline
value class SafeInt(val value: Int) : Comparable<SafeInt> {
    operator fun unaryPlus(): SafeInt {
        return SafeInt(value)
    }

    operator fun unaryMinus(): SafeInt {
        checkOverflow {
            value == Integer.MIN_VALUE
        }
        return SafeInt(-value)
    }

    operator fun inc(): SafeInt {
        checkOverflow {
            value == Integer.MAX_VALUE
        }
        return SafeInt(value + 1)
    }

    operator fun dec(): SafeInt {
        checkOverflow {
            value == Integer.MIN_VALUE
        }
        return SafeInt(value - 1)
    }

    operator fun plus(b: SafeInt): SafeInt {
        val result = this.value + b.value
        checkOverflow {
            result - b.value != this.value
        }
        return SafeInt(result)
    }

    operator fun minus(b: SafeInt): SafeInt {
        val result = this.value - b.value
        checkOverflow {
            result + b.value != this.value
        }
        return SafeInt(result)
    }

    operator fun times(b: SafeInt): SafeInt {
        val result = this.value * b.value
        checkOverflow {
            this.value != result / b.value
        }
        return SafeInt(result)
    }

    operator fun div(b: SafeInt): SafeInt {
        return SafeInt(this.value / b.value)
    }

    operator fun rem(b: SafeInt): SafeInt {
        return SafeInt(this.value % (b.value))
    }

    override fun compareTo(other: SafeInt): Int {
        return value.compareTo(other.value)
    }
}

fun Int.safe(): SafeInt = SafeInt(this)
fun Long.safe(): SafeLong = SafeLong(this)

fun normalizedAngle(angle: Double): Double {
    var result = positiveAngle(angle)
    if (result > PI) {
        result -= 2 * PI
    }
    return result
}

fun positiveAngle(angle: Double): Double {
    var result = angle % (2 * PI)
    if (result < 0) result += 2 * PI
    return result
}

fun findQuadraticRoots(a: Long, b: Long, c: Long): List<Double> {
    val d = b * b - 4 * a * c
    return if (d >= 0L) {
        val denominator = 2.0 * a
        if (d == 0L) {
            listOf(-b / denominator)
        } else {
            listOf((-b + sqrt(d.toDouble())) / denominator, (-b - sqrt(d.toDouble())) / denominator)
        }
    } else {
        emptyList()
    }
}

infix fun Int.over(k: Int): Long {
    val n = this
    require(k in 1..n)
    var result = 1L
    for (i in n - k + 1 .. n) {
        result *= i
    }
    return result
}

fun Int.faculty(): Long {
    var result = 1L
    for (i in 1..this) {
        result *= i
    }
    return result
}
