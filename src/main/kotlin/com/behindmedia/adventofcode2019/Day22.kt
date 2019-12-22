package com.behindmedia.adventofcode2019

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

        abstract fun applyOn(deckSize: Long, index: Long, inverse: Boolean): Long

        fun normalize(index: Long, deckSize: Long): Long {
            var result = index
            while (result < 0) {
                result += deckSize
            }
            while (result >= deckSize) {
                result -= deckSize
            }
            return result
        }

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

            override fun applyOn(deckSize: Long, index: Long, inverse: Boolean): Long {
                val result = if (inverse) {
                    index + amount
                } else {
                    index - amount
                }
                return normalize(result, deckSize)
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

            override fun applyOn(deckSize: Long, index: Long, inverse: Boolean): Long {
                if (inverse) {
                    var value = index
                    while (value % increment != 0L) {
                        value += deckSize
                    }
                    return value / increment
                } else {
                    return normalize(index * increment, deckSize)
                }
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

            override fun applyOn(deckSize: Long, index: Long, inverse: Boolean): Long {
                return normalize(deckSize - 1 - index, deckSize)
            }
        }
    }

    fun shuffle(deck: Deck, shuffleTechniques: List<ShuffleTechnique>) {
        shuffleTechniques.forEach {
            it.applyOn(deck)
        }
    }

    fun shuffledCard(deckSize: Long, index: Long, shuffleTechniques: List<ShuffleTechnique>, inverse: Boolean = false): Long {
        if (inverse) {
            var inverseIndex = index
            for (shuffleTechnique in shuffleTechniques.reversed()) {
                inverseIndex = shuffleTechnique.applyOn(deckSize, inverseIndex, inverse)
            }
            return inverseIndex
        } else {
            var inverseIndex = index
            for (shuffleTechnique in shuffleTechniques) {
                inverseIndex = shuffleTechnique.applyOn(deckSize, inverseIndex, inverse)
            }
            return inverseIndex
        }
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
