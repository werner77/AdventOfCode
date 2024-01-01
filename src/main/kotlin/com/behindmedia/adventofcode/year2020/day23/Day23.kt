package com.behindmedia.adventofcode.year2020.day23

import com.behindmedia.adventofcode.common.read

private class Cup(val value: Int) {
    var next: Cup? = null
    var previous: Cup? = null
    override fun toString(): String {
        return "$value"
    }

    // Remove from the chain
    fun remove() {
        val next = this.next
        val previous = this.previous
        previous?.next = next
        next?.previous = previous
        this.previous = null
        this.next = null
    }

    // Inserts this cup after the specified cup
    fun insertAfter(other: Cup) {
        val next = other.next
        other.next = this
        this.next = next
        this.previous = other
        next?.previous = this
    }
}

private class CupCollection(initial: Cup, size: Int) {
    private val array: Array<Cup?> = Array(size) { null }
    private var current: Cup = initial
    private val maxValue: Int = size
    private val minValue: Int = 1

    init {
        var c = initial
        do {
            array[c.value - 1] = c
            c = c.next ?: error("No next value")
        } while (c != initial)
    }

    fun play() {
        val pickedUp = List(3) {
            current.next!!.also { it.remove() }
        }
        var destination = current.value
        do {
            destination = if (destination == minValue) maxValue else destination - 1
        } while (pickedUp.any { it.value == destination })

        var destinationCup = array[destination - 1] ?: error("Destination cup not found")
        for (c in pickedUp) {
            c.insertAfter(destinationCup)
            destinationCup = c
        }
        current = current.next ?: error("No next cup")
    }

    fun order(): String {
        val builder = StringBuilder()
        var current = array[0] ?: error("No cup found with value 1")
        while (true) {
            current = current.next ?: error("No next value found")
            if (current.value == 1) break
            builder.append(current.value)
        }
        return builder.toString()
    }

    fun score(): Long {
        val one = array[0] ?: error("No cup found with value 1")
        val first = one.next!!
        val second = one.next!!.next!!
        return first.value.toLong() * second.value.toLong()
    }

    companion object {
        operator fun invoke(text: String, size: Int = 0): CupCollection {
            var first: Cup? = null
            var last: Cup? = null
            var count = 0
            for (c in text) {
                val cup = Cup(c.digitToInt())
                count++
                if (first == null) {
                    first = cup
                } else {
                    cup.insertAfter(last!!)
                }
                last = cup
            }
            while (count < size) {
                val cup = Cup(++count)
                cup.insertAfter(last!!)
                last = cup
            }
            last?.next = first
            first?.previous = last
            return CupCollection(first ?: error("No cups found"), count)
        }
    }
}

fun main() {
    val data = read("/2020/day23.txt")

    val cupCollection1 = CupCollection(data.trim())
    repeat(100) {
        cupCollection1.play()
    }
    println(cupCollection1.order())

    val cupCollection2 = CupCollection(data.trim(), 1000000)
    repeat(10000000) {
        cupCollection2.play()
    }
    println(cupCollection2.score())
}