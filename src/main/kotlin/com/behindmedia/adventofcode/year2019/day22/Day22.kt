package com.behindmedia.adventofcode.year2019.day22

import com.behindmedia.adventofcode.common.parseLines
import java.math.BigInteger

private sealed class Shuffle {
    companion object {
        operator fun invoke(string: String): Shuffle {
            return when {
                string.startsWith("cut") -> Cut(string.split(" ").last().toBigInteger())
                string.startsWith("deal with increment") -> DealWithIncrement(string.split(" ").last().toBigInteger())
                string.startsWith("deal into new stack") -> DealIntoNewStack
                else -> error("Invalid string: $string")
            }
        }
    }

    abstract fun transform(function: LinearFunction, deckSize: BigInteger, inverse: Boolean): LinearFunction

    data class Cut(val value: BigInteger) : Shuffle() {
        override fun transform(function: LinearFunction, deckSize: BigInteger, inverse: Boolean): LinearFunction {
            val (_, b) = function
            return if (inverse) {
                function.copy(b = (b + value).mod(deckSize))
            } else {
                function.copy(b = (b - value).mod(deckSize))
            }
        }
    }

    data class DealWithIncrement(val value: BigInteger) : Shuffle() {
        override fun transform(function: LinearFunction, deckSize: BigInteger, inverse: Boolean): LinearFunction {
            val (a, b) = function
            return if (inverse) {
                val inverseValue = value.modInverse(deckSize)
                function.copy(a = (a * inverseValue).mod(deckSize), b = (b * inverseValue).mod(deckSize))
            } else {
                function.copy(a = (a * value).mod(deckSize), b = (b * value).mod(deckSize))
            }
        }
    }

    data object DealIntoNewStack : Shuffle() {
        override fun transform(function: LinearFunction, deckSize: BigInteger, inverse: Boolean): LinearFunction {
            val (a, b) = function
            return function.copy(a = -a.mod(deckSize), b = (-b - BigInteger.ONE).mod(deckSize))
        }
    }
}

private fun List<Shuffle>.transform(index: BigInteger, deckSize: BigInteger, inverse: Boolean, times: Long = 1L): BigInteger {
    return condense(deckSize, inverse).multiply(times = times, modulo = deckSize).apply(index, deckSize)
}

private fun List<Shuffle>.condense(deckSize: BigInteger, inverse: Boolean): LinearFunction {
    var function = LinearFunction(BigInteger.ONE, BigInteger.ZERO)
    for (shuffle in if (inverse) this.reversed() else this) {
        function = shuffle.transform(function, deckSize, inverse)
    }
    return function
}

/**
 * y = a * x + b
 */
private data class LinearFunction(val a: BigInteger, val b: BigInteger) {

    companion object {
        val IDENTITY: LinearFunction = LinearFunction(a = BigInteger.ONE, b = BigInteger.ZERO)
    }

    fun apply(x: BigInteger, modulo: BigInteger): BigInteger {
        return (a * x + b).mod(modulo)
    }

    fun apply(on: LinearFunction, modulo: BigInteger): LinearFunction {
        return LinearFunction(a = (this.a * on.a).mod(modulo), b = (this.a * on.b + this.b).mod(modulo))
    }

    fun multiply(times: Long, modulo: BigInteger): LinearFunction {
        var i = 1L
        var mask = 1L
        var squaredFunction = this
        var effectiveFunction = IDENTITY
        while (i <= times) {
            if (times and mask == mask) {
                effectiveFunction = squaredFunction.apply(on = effectiveFunction, modulo = modulo)
            }
            i *= 2L
            mask = mask shl 1
            squaredFunction = squaredFunction.apply(on = squaredFunction, modulo = modulo)
        }
        return effectiveFunction
    }
}

fun main() {
    val data = parseLines("/2019/day22.txt") { line ->
        Shuffle(line)
    }
    println(data.transform(BigInteger.valueOf(2019L), BigInteger.valueOf(10007L), false))
    println(data.transform(BigInteger.valueOf(2020L), BigInteger.valueOf(119315717514047L), true, times = 101741582076661L))
}