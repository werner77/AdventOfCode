package com.behindmedia.adventofcode2019

fun <T>MutableCollection<T>.popFirst(): T? {
    val iterator = this.iterator()
    if (iterator.hasNext()) {
        val value = iterator.next()
        iterator.remove()
        return value
    }
    return null
}

fun <T>Collection<T>.onlyOrNull(): T? {
    if (this.size > 1) {
        throw IllegalStateException("More than one element found")
    } else {
        return this.firstOrNull()
    }
}

fun <E>List<E>.slice(count: Int): List<List<E>> {
    val sliceCount = this.size / count + (if (this.size % count == 0) 0 else 1)
    val result = List(sliceCount) {
        mutableListOf<E>()
    }
    for (i in this.indices) {
        result[i / count].add(this[i])
    }
    return result
}

fun <T>permutate(count: Int, range: IntRange, perform: (List<Int>) -> T?): T? {
    fun <T>permutate(list: MutableList<Int>, index: Int, range: IntRange, perform: (List<Int>) -> T?): T? {
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

fun List<Int>.toLongList(): List<Long> {
    return this.map { it.toLong() }
}

fun <K, V>MutableMap<K, V>.retainAll(where: (Map.Entry<K, V>) -> Boolean) {
    val iterator = this.iterator()
    while(iterator.hasNext()) {
        val entry = iterator.next()
        if (!where(entry)) {
            iterator.remove()
        }
    }
}

fun <K, V>Map<K, V>.retainingAll(where: (Map.Entry<K, V>) -> Boolean): Map<K, V> {
    val result = this.toMutableMap()
    for (entry in this) {
        if (!where(entry)) {
            result.remove(entry.key)
        }
    }
    return result
}

fun List<Long>.toMap(): Map<Long, Long> {
    return this.foldIndexed(mutableMapOf()) { address, map, value ->
        map[address.toLong()] = value
        map
    }
}

fun List<Long>.toMutableMap(): MutableMap<Long, Long> {
    return this.foldIndexed(mutableMapOf()) { address, map, value ->
        map[address.toLong()] = value
        map
    }
}

class Reference<T>(var value: T)
