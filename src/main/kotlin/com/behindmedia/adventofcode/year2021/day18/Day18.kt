package com.behindmedia.adventofcode.year2021.day18

import com.behindmedia.adventofcode.common.max
import com.behindmedia.adventofcode.common.parseLines
import java.util.concurrent.atomic.AtomicReference

private fun parse(
    string: String,
    startPos: Int = 0,
    lastLiteralReference: AtomicReference<Element.Literal?> = AtomicReference()
): Pair<Element, Int> {
    var i = startPos
    val elements = ArrayList<Element>(2)
    while (i < string.length) {
        when (val c = string[i++]) {
            '[' -> {
                // next pair
                val (element, pos) = parse(string, i, lastLiteralReference)
                elements += element
                i = pos
            }
            ']' -> {
                require(elements.size == 2) { "Incorrect number of elements parsed: ${elements.size}" }
                return Pair(Element.Pair(elements[0], elements[1]), i)
            }
            ',' -> {
                // ignore
            }
            ' ' -> {
                //ignore
            }
            else -> {
                var value = c.toString().toInt()
                while (i < string.length) {
                    val nextInt = string[i].toString().toIntOrNull() ?: break
                    value = value * 10 + nextInt
                    i++
                }
                val literal = Element.Literal(value)
                val lastLiteral = lastLiteralReference.get()
                literal.putAfter(lastLiteral)
                lastLiteralReference.set(literal)
                elements += literal
            }
        }
    }
    require(elements.size == 1) { "Expected exactly one element to be present" }
    return Pair(elements.single(), i)
}

sealed class Element {

    companion object {
        operator fun invoke(string: String): Element {
            return parse(string).first
        }
    }

    abstract val magnitude: Long

    private val firstLiteral: Literal
        get() = findLiteral(this, true)

    private val lastLiteral: Literal
        get() = findLiteral(this, false)

    private fun findLiteral(element: Element, takeLeft: Boolean): Literal {
        return when (element) {
            is Pair -> if (takeLeft) findLiteral(element.left, takeLeft) else findLiteral(element.right, takeLeft)
            is Literal -> element
        }
    }

    class Pair(var left: Element, var right: Element) : Element() {

        override val magnitude: Long
            get() = left.magnitude * 3 + right.magnitude * 2

        override fun toString(): String {
            return "[$left,$right]"
        }

        fun explode(): Literal {
            val leftLiteral: Literal = this.left as? Literal ?: error("Expected left to be literal")
            val rightLiteral: Literal = this.right as? Literal ?: error("Expected right to be literal")
            val newLiteral = Literal(0)
            // Linked list maintenance
            leftLiteral.previous?.let {
                it.value += leftLiteral.value
                newLiteral.putAfter(it)
            }
            rightLiteral.next?.let {
                it.value += rightLiteral.value
                newLiteral.putBefore(it)
            }
            return newLiteral
        }
    }

    class Literal(var value: Int) : Element() {

        // We keep a linked list of all literals in order, so it is easy to find previous and next
        var next: Literal? = null
            private set
        var previous: Literal? = null
            private set

        override val magnitude: Long
            get() = value.toLong()

        fun split(): Pair {
            val left = Literal(value / 2)
            val right = Literal(value / 2 + value % 2)
            // Linked list maintenance
            right.putAfter(left)
            left.putAfter(this.previous)
            right.putBefore(this.next)
            return Pair(left, right)
        }

        fun putAfter(other: Literal?) {
            other?.next = this
            this.previous = other
        }

        fun putBefore(other: Literal?) {
            other?.previous = this
            this.next = other
        }

        override fun toString(): String {
            return value.toString()
        }
    }

    private fun reduce(level: Int, allowSplit: Boolean): Element? {
        when (this) {
            is Pair -> {
                if (level == 4) {
                    return this.explode()
                } else {
                    val reducedLeft = left.reduce(level + 1, allowSplit)
                    if (reducedLeft != null) {
                        left = reducedLeft
                        return this
                    }
                    val reducedRight = right.reduce(level + 1, allowSplit)
                    if (reducedRight != null) {
                        right = reducedRight
                        return this
                    }
                    return null
                }
            }
            is Literal -> {
                return if (allowSplit && this.value >= 10) {
                    this.split()
                } else {
                    null
                }
            }
        }
    }

    fun copy(): Element = Element(this.toString())

    fun reduce(): Element {
        while (true) {
            while (true) {
                reduce(level = 0, allowSplit = false) ?: break
            }
            reduce(level = 0, allowSplit = true) ?: break
        }
        return this
    }

    operator fun plus(other: Element): Element {
        lastLiteral.putBefore(other.firstLiteral)
        return Pair(this, other).reduce()
    }
}

fun main() {
    val data = parseLines("/2021/day18.txt") { line ->
        Element(line)
    }

    // Part 1
    var currentElement = data[0].copy()
    for (i in 1 until data.size) {
        currentElement += data[i].copy()
    }
    println(currentElement.magnitude)

    // Part 2
    val maxSum = sequence {
        for (i in data.indices) {
            for (j in data.indices) {
                if (i == j) continue
                val sum = data[i].copy() + data[j].copy()
                yield(sum.magnitude)
            }
        }
    }.max()
    println(maxSum)
}