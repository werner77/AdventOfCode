package com.behindmedia.adventofcode.common

import java.io.Serializable

data class UnorderedPair<out T>(val first: T, val second: T): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherPair = other as? UnorderedPair<*> ?: return false
        return (first == otherPair.first && second == otherPair.second) ||
                (first == otherPair.second && second == otherPair.first)
    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result += second?.hashCode() ?: 0
        return result
    }

    override fun toString(): String {
        return "($first, $second)"
    }
}

infix fun <A> A.with(that: A): UnorderedPair<A> = UnorderedPair(this, that)

fun <T>UnorderedPair<T>.toList(): List<T> = listOf(first, second)

fun <T>UnorderedPair<T>.toSet(): Set<T> = setOf(first, second)
