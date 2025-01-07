package com.behindmedia.adventofcode.common

import kotlin.math.ceil
import kotlin.math.ln

class Primes(maxNumber: Int) {
    private val sieves = sieve(maxNumber)

    val values: List<Int> by lazy {
        val estimatedSize = ceil(maxNumber.toDouble() / ln(maxNumber.toDouble())).toInt()
        val result = ArrayList<Int>(estimatedSize)
        for (i in 2..maxNumber) {
            if (sieves[i] == 0) result += i
        }
        result
    }

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

    fun isPrime(n: Long): Boolean {
        if (n <= 1L) {
            return false
        }
        return if (n < sieves.size) {
            sieves[n.toInt()] == 0
        } else if (sieves.size > 23) {
            isPrimeMillerRabinLarge(n)
        } else {
            isPrimeMillerRabin(n)
        }
    }

    private fun isPrimeMillerRabin(n: Long): Boolean {
        // Quick checks
        if (n < 2) {
            return false
        }
        // Hardcode small prime checks
        val smallPrimes = listOf<Long>(2, 3, 5, 7, 11, 13, 17, 19, 23)
        if (smallPrimes.contains(n)) {
            return true
        }
        // If divisible by any small prime, not prime
        if (smallPrimes.any { n % it == 0L } ) {
            return false
        }

        return isPrimeMillerRabinLarge(n)
    }

    private fun isPrimeMillerRabinLarge(n: Long): Boolean {
        // Write n-1 = 2^s * d
        val d = (n - 1)
        val s = d.countTrailingZeroBits() // Kotlin standard library
        val dOdd = d shr s               // d // 2^s

        // Deterministic bases for testing 64-bit range
        val testBases = listOf(2L, 325L, 9375L, 28178L, 450775L, 9780504L, 1795265022L)

        // Check each base
        for (a in testBases) {
            if (a % n == 0L) {
                return true
            }  // base is effectively 0 mod n, skip
            if (!millerRabinCheck(n, a, dOdd, s)) {
                return false // Composite
            }
        }
        return true // Probably prime (actually certain for 64-bit n)
    }

    private fun modExp(base: Long, exp: Long, m: Long): Long {
        var result = 1L
        var b = base % m
        var e = exp
        while (e > 0) {
            if ((e and 1) == 1L) {
                result = (result * b) % m
            }
            b = (b * b) % m
            e = e shr 1
        }
        return result
    }

    /**
     * One round of Miller-Rabin with base 'a'.
     * n-1 = 2^s * d
     */
    private fun millerRabinCheck(n: Long, a: Long, d: Long, s: Int): Boolean {
        var x = modExp(a, d, n)
        if (x == 1L || x == n - 1) return true
        for (i in 1 until s) {
            x = (x * x) % n
            if (x == n - 1) return true
            if (x == 1L) return false
        }
        return false
    }
}
