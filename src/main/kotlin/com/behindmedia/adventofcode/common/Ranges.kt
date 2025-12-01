package com.behindmedia.adventofcode.common

import kotlin.math.min
import kotlin.math.roundToInt

fun IntRange.intersects(other: IntRange): Boolean {
    return other.start in this || other.last in this
}

fun LongRange.intersection(other: LongRange): LongRange? {
    return if (other.first <= this.first) {
        if (other.last >= this.last) {
            this
        } else {
            this.first..other.last
        }
    } else {
        if (this.last <= other.last) {
            other.first..this.last
        } else {
            other
        }
    }.takeIf { !it.isEmpty() }
}

fun IntRange.intersection(other: IntRange): IntRange? {
    return this.toLongRange().intersection(other.toLongRange())?.toIntRange()
}

fun IntRange.toLongRange(): LongRange {
    return this.first.toLong()..this.last.toLong()
}

fun LongRange.toIntRange(): IntRange {
    return this.first.toInt()..this.last.toInt()
}

operator fun IntRange.contains(other: IntRange): Boolean {
    return other.first in this && other.last in this
}

val IntRange.size: Int
    get() = this.last - this.first + 1

/**
 * Divides an int range into count number of sub ranges which are as similar in size as possible.
 */
fun IntRange.divide(count: Int): List<IntRange> {
    val size = this.last - this.first + 1
    val result = mutableListOf<IntRange>()
    var current = this.first
    var remaining = size
    while (remaining > 0) {
        val subSize = remaining / (count - result.size).toDouble().roundToInt()
        result += current until min(current + subSize, this.last + 1)
        current += subSize
        remaining -= subSize
    }
    return result
}
