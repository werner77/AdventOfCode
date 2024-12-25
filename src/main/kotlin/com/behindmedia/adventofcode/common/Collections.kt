package com.behindmedia.adventofcode.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

/**
 * Removes the first element if present and returns it.
 */
fun <T> MutableCollection<T>.popFirst(): T? {
    val iterator = this.iterator()
    if (iterator.hasNext()) {
        val value = iterator.next()
        iterator.remove()
        return value
    }
    return null
}

fun <T> MutableCollection<T>.popFirst(where: (T) -> Boolean): T? {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val value = iterator.next()
        if (where(value)) {
            iterator.remove()
            return value
        }
    }
    return null
}

/**
 * Convenience function to assert 0 or 1 elements in the collection
 */
fun <T> Collection<T>.onlyOrNull(): T? {
    if (this.size > 1) {
        throw IllegalStateException("More than one element found")
    } else {
        return this.firstOrNull()
    }
}

fun <T> Collection<T>.only(): T {
    if (this.size != 1) {
        throw IllegalStateException("Expected exactly one element to be present")
    }
    return this.first()
}

/**
 * Slices a list in sub lists of the specified count. The last list can be smaller if the total list size is not fully
 * divisible by the count.
 */
fun <E> List<E>.slice(count: Int): List<List<E>> {
    val sliceCount = this.size / count + (if (this.size % count == 0) 0 else 1)
    val result = List(sliceCount) {
        mutableListOf<E>()
    }
    for (i in this.indices) {
        result[i / count].add(this[i])
    }
    return result
}

fun <T> Iterable<T>.sumOfParallel(operation: (T) -> Long): Long =
    mapReduceParallel(0L, operation) { first, second -> first + second }

fun <T> Sequence<T>.sumOfParallel(operation: (T) -> Long): Long =
    mapReduceParallel(0L, operation) { first, second -> first + second }

fun <T> Iterable<T>.productOfParallel(operation: (T) -> Long): Long =
    mapReduceParallel(1L, operation) { first, second -> first * second }

fun <T> Sequence<T>.productOfParallel(operation: (T) -> Long): Long =
    mapReduceParallel(1L, operation) { first, second -> first * second }

fun <T> Iterable<T>.countParallel(operation: (T) -> Boolean): Long =
    sumOfParallel { if (operation(it)) 1L else 0L }

fun <T> Sequence<T>.countParallel(operation: (T) -> Boolean): Long =
    sumOfParallel { if (operation(it)) 1L else 0L }

fun <T, R> Sequence<T>.mapReduceParallel(initial: R, map: (T) -> R, reduce: (R, R) -> R): R {
    return mapParallel(map).fold(initial, reduce)
}

fun <T, R> Sequence<T>.mapParallel(map: (T) -> R): List<R> {
    return runBlocking {
        val deferredResults = map { chunk ->
            async(Dispatchers.Default) {
                map(chunk)
            }
        }
        deferredResults.toList().awaitAll()
    }
}

fun <T, R> Iterable<T>.mapParallel(map: (T) -> R): List<R> = this.asSequence().mapParallel(map)

fun <T, R> Iterable<T>.mapReduceParallel(initial: R, map: (T) -> R, reduce: (R, R) -> R): R =
    this.asSequence().mapReduceParallel(initial, map, reduce)

fun Iterable<Long>.product(): Long {
    return this.productOf { it }
}

fun Iterable<Int>.product(): Int {
    return this.productOf { it }
}

fun Iterable<Double>.product(): Double {
    return this.productOf { it }
}

operator fun List<Int>.times(multiplier: Int): List<Int> {
    val result = ArrayList<Int>(this.size * multiplier)
    repeat(multiplier) {
        result += this
    }
    return result
}

operator fun String.times(multiplier: Int) = this.times(multiplier, "")

fun String.times(multiplier: Int, separator: String = ""): String {
    val result = StringBuilder()
    repeat(multiplier) {
        if (result.isNotEmpty()) {
            result.append(separator)
        }
        result.append(this)
    }
    return result.toString()
}


@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@JvmName("productOfInt")
@OverloadResolutionByLambdaReturnType
inline fun <T> Iterable<T>.productOf(selector: (T) -> Int): Int {
    var product: Int = 1
    for (element in this) {
        product *= selector(element)
    }
    return product
}

@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@JvmName("productOfLong")
@OverloadResolutionByLambdaReturnType
inline fun <T> Iterable<T>.productOf(selector: (T) -> Long): Long {
    var product: Long = 1L
    for (element in this) {
        product *= selector(element)
    }
    return product
}

@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@JvmName("productOfDouble")
@OverloadResolutionByLambdaReturnType
inline fun <T> Iterable<T>.productOf(selector: (T) -> Double): Double {
    var product: Double = 1.0
    for (element in this) {
        product *= selector(element)
    }
    return product
}

inline fun <T: Any, R: Any> List<T>.processPairs(unique: Boolean = true, block: (T, T) -> R?): R? {
    for (i in 0 until this.size) {
        val startIndex = if (unique) i + 1 else 0
        for (j in startIndex until this.size) {
            if (i == j) continue
            return block.invoke(this[i], this[j]) ?: continue
        }
    }
    return null
}

inline fun <T: Any, R: Any> List<T>.processPairsIndexed(unique: Boolean = true, block: (IndexedValue<T>, IndexedValue<T>) -> R?): R? {
    for (i in 0 until this.size) {
        val startIndex = if (unique) i + 1 else 0
        for (j in startIndex until this.size) {
            if (i == j) continue
            return block.invoke(IndexedValue(i, this[i]), IndexedValue(j, this[j])) ?: continue
        }
    }
    return null
}

fun <T: Any, R: Any> Collection<T>.permute(maxSize: Int = this.size, unique: Boolean = true, perform: (List<T>) -> R?): R? {
    // Use an ArrayDeque to avoid having to reallocate a new collection each time
    // We cycle through the values remaining with removeFirst and addLast.
    fun <T> permute(list: MutableList<T>, valuesLeft: ArrayDeque<T>, perform: (List<T>) -> R?): R? {
        if (valuesLeft.isEmpty() || list.size == maxSize) return perform(list)
        for (i in 0 until valuesLeft.size) {
            val value = if (unique) valuesLeft.removeFirst() else valuesLeft[i]
            try {
                list.add(value)
                return permute(list, valuesLeft, perform) ?: continue
            } finally {
                list.removeLast()
                if (unique) valuesLeft.addLast(value)
            }
        }
        return null
    }
    return permute(ArrayList(this.size), ArrayDeque(this), perform)
}

/**
 * Permutates all possible combinations in a square matrix of the specified count, using values in the specified range.
 *
 * It performs the specified closure for each permutation. If the closure returns a non-null value, the function immediately returns.
 */
fun <T> permute(count: Int, range: IntRange, perform: (IntArray) -> T?): T? {
    fun <T> permute(list: IntArray, index: Int, range: IntRange, perform: (IntArray) -> T?): T? {
        if (index >= list.size) {
            return perform(list)
        }

        for (value in range.first..range.last) {
            list[index] = value
            val ret = permute(list, index + 1, range, perform)
            if (ret != null) {
                return ret
            }
        }
        return null
    }
    val list = IntArray(count) { 0 }
    return permute(list, 0, range, perform)
}

fun <T> permute(
    ranges: List<IntRange>,
    perform: (IntArray) -> T?
): T? {
    fun permute(
        ranges: List<IntRange>,
        dimension: Int,
        values: IntArray,
        perform: (IntArray) -> T?
    ): T? {
        if (dimension == values.size) {
            return perform(values)
        }
        for (i in ranges[dimension].first..ranges[dimension].last) {
            values[dimension] = i
            permute(ranges, dimension + 1, values, perform)?.let {
                if (it != Unit) return it
            }
        }
        return null
    }
    return permute(ranges, 0, IntArray(ranges.size), perform)
}

/**
 * Converts an Int list to a Long list
 */
fun List<Int>.toLongList(): List<Long> {
    return this.map { it.toLong() }
}

/**
 * Retains com.behindmedia.adventofcode.year2018.only the entries for which the supplied closure returns true
 */
fun <K, V> MutableMap<K, V>.retainAll(where: (Map.Entry<K, V>) -> Boolean) {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        if (!where(entry)) {
            iterator.remove()
        }
    }
}

/**
 * Returns a new com.behindmedia.adventofcode.year2018.read-com.behindmedia.adventofcode.year2018.only map which contains com.behindmedia.adventofcode.year2018.only the entries for which the supplied closure returns true
 */
fun <K, V> Map<K, V>.retainingAll(where: (Map.Entry<K, V>) -> Boolean): Map<K, V> {
    val result = this.toMutableMap()
    for (entry in this) {
        if (!where(entry)) {
            result.remove(entry.key)
        }
    }
    return result
}

/**
 * Converts a list of longs to a map where the key is the index and the value is the value.
 */
fun List<Long>.toMap(): Map<Long, Long> {
    return this.foldIndexed(mutableMapOf()) { address, map, value ->
        map[address.toLong()] = value
        map
    }
}

/**
 * Converts a list of longs to a mutable map where the key is the index and the value is the value.
 */
fun List<Long>.toMutableMap(): MutableMap<Long, Long> {
    return this.foldIndexed(mutableMapOf()) { address, map, value ->
        map[address.toLong()] = value
        map
    }
}

fun List<Int>.firstDigits(numberOfDigits: Int, offset: Int = 0): Int {
    var result = 0
    for (i in offset until offset + numberOfDigits) {
        result *= 10
        result += this[i]
    }
    return result
}

fun <E> List<E>.repeated(count: Int): List<E> {
    val list = mutableListOf<E>()
    for (i in 0 until count) {
        list.addAll(this)
    }
    return list
}

fun <E> List<E>.removingAllOccurences(sublist: List<E>): List<E> {
    val first = sublist.firstOrNull() ?: return this
    val result = mutableListOf<E>()
    var i = 0
    while (i < this.size) {
        val e = this[i]
        if (e == first) {
            // Check whether there is a complete match
            var foundMatch = true
            for (j in 1 until sublist.size) {
                if (i + j >= this.size || this[i + j] != sublist[j]) {
                    foundMatch = false
                    break
                }
            }
            if (foundMatch) {
                i += sublist.size
                continue
            }
        }
        result.add(e)
        i++
    }
    return result
}

/**
 * Reference to use for inout parameters
 */
class Reference<T>(var value: T)

class FilteredIterator<E: Any>(iterator: Iterator<E>, predicate: (E) -> Boolean) : CompactMappedIterator<E, E>(iterator, { if (predicate(it)) it else null })

class MappedIterator<E: Any, T: Any>(iterator: Iterator<E>, mapper: (E) -> T) : CompactMappedIterator<E, T>(iterator, { mapper(it) })

open class CompactMappedIterator<T: Any, R: Any>(private val iterator: Iterator<T>, private val mapper: (T) -> R?) :
    Iterator<R> {

    private var nextElement: R? = null

    private fun initNextElement() {
        if (nextElement == null) {
            while (iterator.hasNext()) {
                val element = iterator.next()
                val mapped = mapper(element) ?: continue
                nextElement = mapped
                return
            }
        }
    }

    override fun hasNext(): Boolean {
        initNextElement()
        return nextElement != null
    }

    override fun next(): R {
        try {
            initNextElement()
            return nextElement ?: throw NoSuchElementException("No elements left")
        } finally {
            nextElement = null
        }
    }
}

fun <T: Any, R: Any>Iterator<T>.mapped(mapper: (T) -> R) = CompactMappedIterator(this, mapper)
fun <T: Any>Iterator<T>.filtered(predicate: (T) -> Boolean) = CompactMappedIterator(this) { if (predicate(it)) it else null }
fun <T: Any, R: Any>Iterator<T>.compactMapped(mapper: (T) -> R?) = CompactMappedIterator(this, mapper)

class FilteredIterable<E: Any>(private val iterable: Iterable<E>, private val predicate: (E) -> Boolean) : Iterable<E> {

    override fun iterator(): Iterator<E> {
        return FilteredIterator(iterable.iterator(), predicate)
    }
}

fun Array<IntArray>.printMatrix() {
    val size = this.size
    for (i in 0 until size) {
        for (j in 0 until this[0].size) {
            if (j > 0) {
                print("\t")
            }
            print(this[i][j])
        }
        println()
    }
}

fun <T : Comparable<T>> Sequence<T>.max(): T = maxOf { it }
fun <T : Comparable<T>> Sequence<T>.min(): T = minOf { it }

fun <T : Comparable<T>> Iterable<T>.max(): T = maxOf { it }
fun <T : Comparable<T>> Iterable<T>.min(): T = minOf { it }


fun compare(vararg comparators: () -> Int): Int {
    var lastResult = 0
    for (comparator in comparators) {
        lastResult = comparator()
        if (lastResult != 0) break
    }
    return lastResult
}

fun <K, V> defaultMapOf(defaultValue: () -> V): DefaultMap<K, V> {
    val impl = mapOf<K, V>()
    return DefaultMapWrapper(impl, defaultValue)
}

fun <K, V> defaultMapOf(vararg pairs: Pair<K, V>, defaultValue: () -> V): DefaultMap<K, V> {
    val impl = mapOf(*pairs)
    return DefaultMapWrapper(impl, defaultValue)
}

fun <K, V> defaultMutableMapOf(putValueImplicitly: Boolean = false, defaultValue: () -> V): DefaultMutableMap<K, V> {
    val impl = mutableMapOf<K, V>()
    return DefaultMutableMapWrapper(impl = impl, defaultValue = defaultValue, putValueImplicitly = putValueImplicitly)
}

fun <K, V> defaultMutableMapOf(putValueImplicitly: Boolean = false, vararg pairs: Pair<K, V>, defaultValue: () -> V): DefaultMutableMap<K, V> {
    val impl = mutableMapOf(*pairs)
    return DefaultMutableMapWrapper(impl = impl, defaultValue = defaultValue, putValueImplicitly = putValueImplicitly)
}

fun <K, V>Map<K, V>.withDefaultValue(defaultValue: () -> V): DefaultMap<K, V> = DefaultMapWrapper(this, defaultValue)
fun <K, V>MutableMap<K, V>.withDefaultValue(putValueImplicitly: Boolean = false, defaultValue: () -> V): DefaultMutableMap<K, V> = DefaultMutableMapWrapper(this, defaultValue, putValueImplicitly)
fun <V> List<V>.withDefaultValue(defaultValue: () -> V): DefaultList<V> = DefaultListWrapper(this, defaultValue)

private class DefaultMutableMapWrapper<K, V>(
    private val impl: MutableMap<K, V>,
    private val defaultValue: () -> V,
    private val putValueImplicitly: Boolean = false
) : DefaultMutableMap<K, V>, MutableMap<K, V> by impl {
    override fun getOrPutDefault(key: K) = impl.getOrPut(key, defaultValue)
    override fun getOrDefault(key: K): V = impl.getOrElse(key, defaultValue)
    override operator fun get(key: K): V = if (putValueImplicitly) getOrPutDefault(key) else getOrDefault(key)

    override fun equals(other: Any?): Boolean {
        return impl == (other as? Map<*, *>)
    }

    override fun hashCode(): Int {
        return impl.hashCode()
    }

    override fun toString(): String {
        return impl.toString()
    }
}

private class DefaultMapWrapper<K, V>(
    private val impl: Map<K, V>,
    private val defaultValue: () -> V,
) : DefaultMap<K, V>, Map<K, V> by impl {
    override fun getOrDefault(key: K): V = impl.getOrElse(key, defaultValue)
    override operator fun get(key: K): V = getOrDefault(key)

    override fun equals(other: Any?): Boolean {
        return impl == (other as? Map<*, *>)
    }

    override fun hashCode(): Int {
        return impl.hashCode()
    }

    override fun toString(): String {
        return impl.toString()
    }
}

interface DefaultMap<K, V> : Map<K, V> {
    fun getOrDefault(key: K): V
    override operator fun get(key: K): V
}

interface DefaultMutableMap<K, V> : DefaultMap<K, V>, MutableMap<K, V> {
    fun getOrPutDefault(key: K): V
}

interface DefaultList<V>: List<V> {
    fun getOrDefault(index: Int): V
}

private class DefaultListWrapper<V>(private val impl: List<V>, private val defaultValue: () -> V): DefaultList<V>, List<V> by impl {
    override fun getOrDefault(index: Int): V {
        return impl.getOrNull(index) ?: defaultValue.invoke()
    }

    override fun get(index: Int): V {
        return getOrDefault(index)
    }

    override fun equals(other: Any?): Boolean {
        return this.impl == other
    }

    override fun hashCode(): Int {
        return impl.hashCode()
    }

    override fun toString(): String {
        return impl.toString()
    }
}