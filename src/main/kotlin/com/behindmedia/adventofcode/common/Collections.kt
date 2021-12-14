package com.behindmedia.adventofcode.common

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

/**
 * Permutates all possible combinations in values without duplicates and calls the perform closure for each such permutation
 * until a non-null result is returned
 */
fun <T, R> permutateUnique(values: Set<T>, perform: (List<T>) -> R?): R? {
    // Use an ArrayDeque to avoid having to reallocate a new collection each time
    // We cycle through the values remaining with removeFirst and addLast.
    fun <T> permutate(list: MutableList<T>, valuesLeft: ArrayDeque<T>, perform: (List<T>) -> R?): R? {
        if (valuesLeft.isEmpty()) return perform(list)
        for (i in valuesLeft.indices) {
            val value = valuesLeft.removeFirst()
            try {
                list.add(value)
                return permutate(list, valuesLeft, perform) ?: continue
            } finally {
                list.removeLast()
                valuesLeft.addLast(value)
            }
        }
        return null
    }
    return permutate(ArrayList(values.size), ArrayDeque(values), perform)
}

/**
 * Permutates all possible combinations in a square matrix of the specified count, using values in the specified range.
 *
 * It performs the specified closure for each permutation. If the closure returns a non-null value, the function immediately returns.
 */
fun <T> permutate(count: Int, range: IntRange, perform: (List<Int>) -> T?): T? {
    fun <T> permutate(list: MutableList<Int>, index: Int, range: IntRange, perform: (List<Int>) -> T?): T? {
        if (index >= list.size) {
            return perform(list)
        }

        for (value in range.first..range.last) {
            list[index] = value
            val ret = permutate(list, index + 1, range, perform)
            if (ret != null) {
                return ret
            }
        }
        return null
    }

    val list = MutableList(count) { 0 }
    return permutate(list, 0, range, perform)
}

fun <T> permutate(
    ranges: List<IntRange>,
    perform: (IntArray) -> T?
): T? {
    fun permutate(
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
            permutate(ranges, dimension + 1, values, perform)?.let {
                if (it != Unit) return it
            }
        }
        return null
    }
    return permutate(ranges, 0, IntArray(ranges.size), perform)
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

class FilteredIterable<E>(private val iterable: Iterable<E>, private val predicate: (E) -> Boolean) : Iterable<E> {

    private class FilteredIterator<E>(private val iterator: Iterator<E>, private val predicate: (E) -> Boolean) :
        Iterator<E> {

        private var nextElement: E? = null

        private fun initNextElement() {
            if (nextElement == null) {
                while (iterator.hasNext()) {
                    val element = iterator.next()
                    if (predicate(element)) {
                        nextElement = element
                        return
                    }
                }
            }
        }

        override fun hasNext(): Boolean {
            initNextElement()
            return nextElement != null
        }

        override fun next(): E {
            try {
                initNextElement()
                return nextElement ?: throw NoSuchElementException("No elements left")
            } finally {
                nextElement = null
            }
        }
    }

    override fun iterator(): Iterator<E> {
        return FilteredIterator(iterable.iterator(), predicate)
    }
}

fun Array<IntArray>.printMatrix() {
    val size = this.size
    for (i in 0 until size) {
        for (j in 0 until size) {
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

fun <K, V> defaultMutableMapOf(defaultValue: () -> V): DefaultMutableMap<K, V> {
    val impl = mutableMapOf<K, V>()
    return DefaultMutableMapWrapper(impl, defaultValue)
}

fun <K, V> defaultMutableMapOf(vararg pairs: Pair<K, V>, defaultValue: () -> V): DefaultMutableMap<K, V> {
    val impl = mutableMapOf(*pairs)
    return DefaultMutableMapWrapper(impl, defaultValue)
}

fun <K, V>Map<K, V>.toDefaultMap(defaultValue: () -> V): DefaultMap<K, V> = DefaultMapWrapper(this.toMap(), defaultValue)
fun <K, V>Map<K, V>.toDefaultMutableMap(defaultValue: () -> V): DefaultMutableMap<K, V> = DefaultMutableMapWrapper(this.toMutableMap(), defaultValue)

private class DefaultMutableMapWrapper<K, V>(
    private val impl: MutableMap<K, V>,
    private val defaultValue: () -> V,
) : DefaultMutableMap<K, V>, MutableMap<K, V> by impl {
    override fun getOrPutDefault(key: K) = impl.getOrPut(key, defaultValue)
    override fun getOrDefault(key: K): V = impl.getOrElse(key, defaultValue)
    override operator fun get(key: K): V = getOrPutDefault(key)
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
