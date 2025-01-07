package com.behindmedia.adventofcode.common

inline fun binarySearch(
    lowerBound: Int,
    upperBound: Int,
    targetValue: Int,
    inverted: Boolean = false,
    evaluation: (Int) -> Int
): Int? {
    return binarySearch(lowerBound, upperBound, inverted) { mid ->
        evaluation(mid) <= targetValue
    }
}

inline fun binarySearch(
    lowerBound: Long,
    upperBound: Long,
    targetValue: Long,
    inverted: Boolean = false,
    evaluation: (Long) -> Long
): Long? {
    return binarySearch(lowerBound, upperBound, inverted) { mid ->
        evaluation(mid) <= targetValue
    }
}

inline fun binarySearch(
    lowerBound: Int,
    upperBound: Int,
    inverted: Boolean = false,
    evaluation: (Int) -> Boolean
): Int? = binarySearch(lowerBound.toLong(), upperBound.toLong(), inverted, { evaluation.invoke(it.toInt()) })?.toInt()

inline fun binarySearch(
    lowerBound: Long,
    upperBound: Long,
    inverted: Boolean = false,
    evaluation: (Long) -> Boolean
): Long? {
    var begin = lowerBound
    var end = upperBound
    var result: Long? = null
    while (begin <= end) {
        val mid = (begin + end) / 2L
        if (evaluation(mid)) {
            result = mid
            if (inverted) {
                end = mid - 1
            } else {
                begin = mid + 1
            }
        } else {
            if (inverted) {
                begin = mid + 1
            } else {
                end = mid - 1
            }
        }
    }
    return result
}

enum class ComparisonResult {
    Less, LessOrEqual, GreaterOrEqual, Greater;
}

inline fun <T : Comparable<T>> count(
    size: Int,
    value: T,
    comparisonResult: ComparisonResult,
    range: IntRange = 0 until size,
    crossinline valueForIndex: (Int) -> T
): Int {
    val inverse = comparisonResult == ComparisonResult.Less || comparisonResult == ComparisonResult.LessOrEqual
    val index = binarySearch(lowerBound = range.first, upperBound = range.last, inverted = inverse) { i ->
        val valueAtIndex = valueForIndex(i)
        when (comparisonResult) {
            ComparisonResult.Less -> valueAtIndex >= value
            ComparisonResult.LessOrEqual -> valueAtIndex > value
            ComparisonResult.GreaterOrEqual -> valueAtIndex < value
            ComparisonResult.Greater -> valueAtIndex <= value
        }
    }
    return if (inverse) {
        index ?: size
    } else {
        if (index == null) size else size - index - 1
    }
}

fun <T : Comparable<T>> Array<T>.count(
    value: T,
    comparisonResult: ComparisonResult,
    range: IntRange = indices
): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

fun LongArray.count(value: Long, comparisonResult: ComparisonResult, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}

fun IntArray.count(value: Int, comparisonResult: ComparisonResult, range: IntRange = indices): Int {
    return count(size = this.size, value = value, comparisonResult = comparisonResult, range = range) { this[it] }
}