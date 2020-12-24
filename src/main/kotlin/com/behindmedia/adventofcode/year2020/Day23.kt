package com.behindmedia.adventofcode.year2020

class Day23 {

    class Cup(values: List<Int>, private val size: Int = values.size) {
        private var currentValue: Int = values.first()
        private val storage: IntArray = IntArray(size + 1)
        private val minValue: Int = 1
        private val maxValue: Int = size
        private val pickedUpNodeValues = IntArray(3)

        init {
            var lastValue = currentValue
            for (i in 1 until size) {
                val value = values.getOrNull(i) ?: i + 1
                storage[lastValue] = value
                lastValue = value
            }
            storage[lastValue] = currentValue
        }

        fun toList(fromValue: Int? = null, inclusive: Boolean = true, additionalCount: Int = size - 1): List<Int> {
            return ArrayList<Int>(additionalCount + 1).apply {
                var value = fromValue ?: currentValue
                repeat(additionalCount + 1) {
                    if (it > 0 || inclusive) {
                        add(value)
                    }
                    value = storage[value]
                }
            }
        }

        fun play() {
            var nextValue = storage[currentValue]
            val firstValue = nextValue
            var lastValue = firstValue
            var targetValue = currentValue - 1
            repeat(3) { index ->
                pickedUpNodeValues[index] = nextValue
                lastValue = nextValue
                nextValue = storage[nextValue]
            }
            storage[currentValue] = nextValue
            while (targetValue < minValue || pickedUpNodeValues.contains(targetValue)) {
                if (targetValue < minValue) {
                    targetValue = maxValue
                } else {
                    targetValue--
                }
            }
            val targetNext = storage[targetValue]
            storage[targetValue] = firstValue
            storage[lastValue] = targetNext
            currentValue = storage[currentValue]
        }
    }

    fun part1(input: String): String {
        val values = input.map { it.toString().toInt() }
        val cup = Cup(values)
        repeat(100) {
            cup.play()
        }
        return cup.toList(fromValue = 1, inclusive = false).joinToString(separator = "")
    }

    fun part2(input: String): Long {
        val values = input.map { it.toString().toInt() }
        val cup = Cup(values, 1_000_000)
        repeat(10_000_000) {
            cup.play()
        }
        return cup.toList(fromValue = 1, inclusive = false, additionalCount = 2).map { it.toLong() }.reduce { acc, i -> acc * i }
    }
}