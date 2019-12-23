package com.behindmedia.adventofcode2019

fun normalize(index: Long, deckSize: Long): Long {
    var result = index % deckSize
    while (result < 0) {
        result += deckSize
    }
    return result
}

class Day22 {

    class Deck(val list: MutableList<Long>, var startIndex: Long) {

        constructor(size: Int = 10007) : this(MutableList<Long>(size) { it.toLong() }, 0)

        private fun realIndex(index: Long): Long {
            var result = (startIndex + index)
            val size = this.size

            while (result >= size) {
                result -= size
            }
            while (result < 0) {
                result += size
            }
            return result
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
            for (i in 0 until size) {
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

        abstract fun applyOn(deckSize: Long, index: Long): Long

        abstract fun apply(deckSize: Long, state: Pair<Long, Long>, inverse: Boolean): Pair<Long, Long>

        companion object {
            fun from(input: String): ShuffleTechnique {
                val cutPrefix = "cut "
                val dealWithIncrementPrefix = "deal with increment "
                if (input.startsWith(cutPrefix)) {
                    val amount = input.substring(cutPrefix.length).toLong()
                    return Cut(amount)
                } else if (input == "deal into new stack") {
                    return DealIntoNewStack()
                } else if (input.startsWith(dealWithIncrementPrefix)) {
                    val increment = input.substring(dealWithIncrementPrefix.length).toLong()
                    return DealWithIncrement(increment)
                } else {
                    throw IllegalStateException("Unknown input: $input")
                }
            }
        }

        class Cut(val amount: Long): ShuffleTechnique() {
            override fun applyOn(deck: Deck) {
                val startIndex = deck.startIndex + amount
                deck.startIndex = normalize(startIndex, deck.size)
            }

            override fun applyOn(deckSize: Long, index: Long): Long {
                val result = index - amount
                return normalize(result, deckSize)
            }

            override fun apply(deckSize: Long, state: Pair<Long, Long>, inverse: Boolean): Pair<Long, Long> {
                return if (inverse) {
                    Pair(state.first, normalize(state.second + amount, deckSize))
                } else {
                    Pair(state.first, normalize(state.second - amount, deckSize))
                }
            }
        }

        class DealWithIncrement(val increment: Long): ShuffleTechnique() {
            override fun applyOn(deck: Deck) {
                val original = deck.copy()

                var i = 0L
                var j = 0L
                while(j < deck.size) {
                    deck[i] = original[j++]
                    i += increment
                    while (i >= deck.size) {
                        i -= deck.size
                    }
                }
            }

            override fun applyOn(deckSize: Long, index: Long): Long {
                return normalize(index * increment, deckSize)
            }

            override fun apply(deckSize: Long, state: Pair<Long, Long>, inverse: Boolean): Pair<Long, Long> {
                var multiplier = state.first
                var offset = state.second
                if (inverse) {
                    // Until it is divisible: add deckSize

                    while (multiplier % increment != 0L) {
                        multiplier += deckSize
                    }

                    while (offset % increment != 0L) {
                        offset += deckSize
                    }

                    multiplier /= increment
                    offset /= increment
                } else {
                    multiplier *= increment
                    offset *= increment
                }
                return Pair(normalize(multiplier, deckSize), normalize(offset, deckSize))
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

            override fun applyOn(deckSize: Long, index: Long): Long {
                val result = DealWithIncrement(deckSize - 1).applyOn(deckSize, index)
                return Cut(1).applyOn(deckSize, result)
            }

            override fun apply(deckSize: Long, state: Pair<Long, Long>, inverse: Boolean): Pair<Long, Long> {
                // The inverse of this operation is the operation itself, so just ignore the inverse parameter
                val result = DealWithIncrement(deckSize - 1).apply(deckSize, state, false)
                return Cut(1).apply(deckSize, result, false)
            }
        }
    }

    fun shuffle(deck: Deck, shuffleTechniques: List<ShuffleTechnique>) {
        shuffleTechniques.forEach {
            it.applyOn(deck)
        }
    }

    fun shuffledCard(deckSize: Long, index: Long, shuffleTechniques: List<ShuffleTechnique>, inverse: Boolean = false, multiplier: Long = 1L): Long {
        var state = Pair<Long, Long>(1, 0)
        val techniques = if (inverse) shuffleTechniques.reversed() else shuffleTechniques

        for (shuffleTechnique in techniques) {
            state = shuffleTechnique.apply(deckSize, state, inverse)
        }

        val a = state.first
        val b = state.second
        var result = index
        var c = 0L
        var i = 1L

        while (i <= multiplier) {
            result = normalize(a * result + b, deckSize)
            c = normalize(a * c + b, deckSize)
            val diff = normalize(result - c, deckSize)

            if (diff % index == 0L) {

                val k = diff / index
                val times = multiplier / i

                result = normalize(index * k * times, deckSize) + c
                result = normalize(result, deckSize)

                i *= times

                // y = kx + c <=> y = pow(a,i) * x + c



            }
            i++
        }
        return result
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
