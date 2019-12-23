package com.behindmedia.adventofcode2019

import java.math.BigInteger

private fun normalize(index: Long, deckSize: Long): Long {
    var result = index % deckSize
    while (result < 0) {
        result += deckSize
    }
    return result
}

private fun normalize(index: BigInteger, deckSize: BigInteger): BigInteger {
    var result = index % deckSize
    while (result < 0.toBigInteger()) {
        result += deckSize
    }
    return result
}

class Day22 {

    class Deck(val list: MutableList<Long>, var startIndex: Long) {

        constructor(size: Int) : this(MutableList<Long>(size) { it.toLong() }, 0L)

        private fun realIndex(index: Long): Long {
            return normalize(startIndex + index, this.size)
        }

        operator fun get(index: Long): Long {
            return list[realIndex(index).toInt()]
        }

        operator fun set(index: Long, value: Long) {
            list[realIndex(index).toInt()] = value
        }

        fun copy(): Deck {
            return Deck(list.toMutableList(), startIndex)
        }

        fun positionOf(card: Long): Long {
            for (i in 0L until size) {
                if (card == this[i]) {
                    return i
                }
            }
            throw IllegalArgumentException("Card $card was not found")
        }

        val size: Long
            get() = list.size.toLong()

        override fun toString(): String {
            val buffer = StringBuilder()
            for (i in 0 until size) {
                if (i != 0L) {
                    buffer.append(" ")
                }
                buffer.append(this[i].toString())
            }
            return buffer.toString()
        }
    }

    sealed class ShuffleTechnique() {

        abstract fun applyOn(deck: Deck)

        abstract fun apply(deckSize: BigInteger, state: LinearOperation, inverse: Boolean): LinearOperation

        companion object {
            fun from(input: String): ShuffleTechnique {
                val cutPrefix = "cut "
                val dealWithIncrementPrefix = "deal with increment "
                if (input.startsWith(cutPrefix)) {
                    val amount = input.substring(cutPrefix.length).toBigInteger()
                    return Cut(amount)
                } else if (input == "deal into new stack") {
                    return DealIntoNewStack()
                } else if (input.startsWith(dealWithIncrementPrefix)) {
                    val increment = input.substring(dealWithIncrementPrefix.length).toBigInteger()
                    return DealWithIncrement(increment)
                } else {
                    throw IllegalStateException("Unknown input: $input")
                }
            }
        }

        class Cut(val amount: BigInteger): ShuffleTechnique() {
            override fun applyOn(deck: Deck) {
                val startIndex = deck.startIndex + amount.toLong()
                deck.startIndex = normalize(startIndex, deck.size)
            }

            override fun apply(deckSize: BigInteger, state: LinearOperation, inverse: Boolean): LinearOperation {
                return if (inverse) {
                    LinearOperation(state.a, normalize(state.b + amount, deckSize))
                } else {
                    LinearOperation(state.a, normalize(state.b - amount, deckSize))
                }
            }
        }

        class DealWithIncrement(val increment: BigInteger): ShuffleTechnique() {
            override fun applyOn(deck: Deck) {
                val original = deck.copy()
                val longIncrement = increment.toLong()
                var i = 0L
                var j = 0L
                while(j < deck.size) {
                    deck[i] = original[j++]
                    i = normalize(i + longIncrement, deck.size)
                }
            }

            override fun apply(deckSize: BigInteger, state: LinearOperation, inverse: Boolean): LinearOperation {
                var a = state.a
                var b = state.b
                if (inverse) {
                    // Until it is divisible: add deckSize
                    while (a % increment != 0.toBigInteger()) {
                        a += deckSize
                    }

                    while (b % increment != 0.toBigInteger()) {
                        b += deckSize
                    }

                    a /= increment
                    b /= increment
                } else {
                    a *= increment
                    b *= increment
                }
                return LinearOperation(normalize(a, deckSize), normalize(b, deckSize))
            }
        }

        class DealIntoNewStack(): ShuffleTechnique() {
            override fun applyOn(deck: Deck) {
                val deckSize = deck.size
                for (i in 0 until deckSize /2) {
                    val j = deckSize - 1 - i
                    deck[i] = deck[j].also { deck[j] = deck[i] }
                }
            }

            override fun apply(deckSize: BigInteger, state: LinearOperation, inverse: Boolean): LinearOperation {
                // The inverse of this operation is the operation itself, so just ignore the inverse parameter
                val result = DealWithIncrement(deckSize - 1.toBigInteger()).apply(deckSize, state, false)
                return Cut(1.toBigInteger()).apply(deckSize, result, false)
            }
        }
    }

    fun shuffle(deck: Deck, shuffleTechniques: List<ShuffleTechnique>) {
        shuffleTechniques.forEach {
            it.applyOn(deck)
        }
    }

    /**
     * Linear operation of the form: y = a * x + b
     */
    class LinearOperation(val a: BigInteger = 1.toBigInteger(), val b: BigInteger = 0.toBigInteger()) {

        fun squared(deckSize: BigInteger): LinearOperation {
            return this.apply(this, deckSize)
        }

        fun apply(on: LinearOperation, deckSize: BigInteger): LinearOperation {
            return LinearOperation(normalize(this.a * on.a, deckSize), normalize(this.a * on.b + this.b, deckSize))
        }

        fun apply(on: BigInteger, deckSize: BigInteger): BigInteger {
            return normalize(this.a * on + this.b, deckSize)
        }
    }

    fun List<ShuffleTechnique>.resultingLinearOperation(deckSize: BigInteger, inverse: Boolean): LinearOperation {
        var state = LinearOperation()
        val techniques = if (inverse) this.reversed() else this
        for (shuffleTechnique in techniques) {
            state = shuffleTechnique.apply(deckSize, state, inverse)
        }
        return state
    }

    fun shuffledCard(deckSize: Long, index: Long, shuffleTechniques: List<ShuffleTechnique>, inverse: Boolean = false, multiplier: Long = 1L, optimize: Boolean = true): Long {
        val bigDeckSize = deckSize.toBigInteger()
        val state = shuffleTechniques.resultingLinearOperation(bigDeckSize, inverse)

        var finalState = LinearOperation()

        if (optimize) {
            var remainingTimes = multiplier
            while (remainingTimes > 0) {

                // Square the state until we cannot square anymore, then we have a number left.
                // For that number we repeat the process of squaring again, etc

                var repeat = 1L
                var squaredState = state
                while (remainingTimes >= (repeat * 2L)) {
                    squaredState = squaredState.squared(bigDeckSize)
                    repeat *= 2L
                }

                // Apply the squaredState on the finalState
                finalState = squaredState.apply(finalState, bigDeckSize)

                remainingTimes -= repeat
            }
        } else {
            // Just perform brute force
            for (i in 0 until multiplier) {
                finalState = state.apply(finalState, bigDeckSize)
            }
        }
        return finalState.apply(index.toBigInteger(), bigDeckSize).toLong()
    }

    /**
    cut -7812
    deal with increment 55
    cut -3909
    deal with increment 51
    deal into new stack
     */
    fun parseInput(input: String): List<ShuffleTechnique> {
        return input.split('\n').map { line ->
            ShuffleTechnique.from(line)
        }
    }
}
