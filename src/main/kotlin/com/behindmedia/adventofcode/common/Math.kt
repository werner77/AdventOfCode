package com.behindmedia.adventofcode.common

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

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

val Int.faculty: Long
    get() {
        var result = 1L
        for (i in 1..this) {
            result *= i
        }
        return result
    }

val Int.digits: List<Int>
    get() {
        if (this == 0) {
            return listOf(0)
        }
        val result = ArrayDeque<Int>(10)
        var value = abs(this)
        while (value != 0) {
            result.addFirst(value % 10)
            value /= 10
        }
        return result
    }

val Long.digits: List<Int>
    get() {
        if (this == 0L) {
            return listOf(0)
        }
        val result = ArrayDeque<Int>(20)
        var value = abs(this)
        while (value != 0L) {
            result.addFirst((value % 10L).toInt())
            value /= 10L
        }
        return result
    }

fun floorLog2(value: Int): Int {
    require(value > 0)
    return 31 - value.countLeadingZeroBits()
}

fun ceilLog2(value: Int): Int {
    require(value > 0)
    return 32 - (value - 1).countLeadingZeroBits()
}