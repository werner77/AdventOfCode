package com.behindmedia.adventofcode2019

import java.io.BufferedReader
import java.io.FileNotFoundException
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sqrt


fun <T>parseLines(resource: String, parser: (String) -> T) : List<T> {
    val reader = readerForResource(resource)
    return reader.use {
        val ret = mutableListOf<T>()
        it.forEachLine { line ->
            ret.add(parser(line))
        }
        ret
    }
}

fun <T>parse(resource: String, parser: (String) -> T) : T {
    return parser(read(resource))
}

fun read(resource: String): String {
    return readerForResource(resource).use {
        it.readText()
    }
}

private fun readerForResource(resource: String): BufferedReader {
    val url = object {}.javaClass.getResource(resource)
        ?: throw FileNotFoundException("Resource with name $resource could not be found")

    val stream = url.openStream()
    return stream.bufferedReader()
}

fun <T>MutableCollection<T>.popFirst(): T? {
    val firstElement = firstOrNull()
    if (firstElement != null) {
        remove(firstElement)
    }
    return firstElement
}

fun <T>MutableSet<T>.update(value: T) {
    this.remove(value)
    this.add(value)
}

fun <T>Collection<T>.onlyOrNull(): T? {
    if (this.size > 1) {
        throw IllegalStateException("More than one element found")
    } else {
        return this.firstOrNull()
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
    var result = this.toMutableMap()
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

val Long.numberOfDigits: Int
    get() = numberOfDigits(this)

private fun numberOfDigits(number: Long): Int {
    var value = number
    var digitCount = 0
    while (value != 0L) {
        value /= 10
        digitCount++
    }
    return digitCount
}

class Reference<T>(var value: T)

data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {

    fun angle(): Double {
        if (y == 0) {
            return if (x > 0) PI/2.0 else 3.0 * PI/2.0
        }

        val ratio = x.toDouble() / -y.toDouble()
        var angle = -atan(ratio)

        if (x < 0) {
            if (y < 0) {

            } else {

            }
        } else {
            if (y < 0) {

            } else {

            }
        }

        return angle
    }

    override fun compareTo(other: Coordinate): Int {
        var result = this.y.compareTo(other.y)
        if (result == 0) {
            result = this.x.compareTo(other.x)
        }
        return result
    }

    fun offset(xOffset: Int, yOffset: Int): Coordinate {
        return Coordinate(x + xOffset, y + yOffset)
    }

    fun offset(vector: Coordinate): Coordinate {
        return offset(vector.x, vector.y)
    }

    fun vectorTo(coordinate: Coordinate): Coordinate {
        return Coordinate(coordinate.x - this.x, coordinate.y - this.y)
    }

    fun manhattenDistance(to: Coordinate): Int {
        return abs(x - to.x) + abs(y - to.y)
    }

    fun distance(to: Coordinate): Double {
        val deltaX = (x - to.x).toDouble()
        val deltaY = (y - to.y).toDouble()
        return sqrt( deltaX * deltaX + deltaY * deltaY)
    }

    fun normalized(): Coordinate {
        val factor = gcdBySteinsAlgorithm(abs(this.x), abs(this.y))
        return Coordinate(this.x / factor, this.y / factor)
    }

    fun normalized1(): Pair<Coordinate, Int> {
        val factor = gcdBySteinsAlgorithm(abs(this.x), abs(this.y))
        return Pair(Coordinate(this.x / factor, this.y / factor), factor)
    }

    companion object {
        val origin = Coordinate(0, 0)
    }
}

fun gcdBySteinsAlgorithm(n1: Int, n2: Int): Int {
    var n1 = n1
    var n2 = n2
    if (n1 == 0) {
        return n2
    }
    if (n2 == 0) {
        return n1
    }
    var n: Int
    n = 0
    while (n1 or n2 and 1 == 0) {
        n1 = n1 shr 1
        n2 = n2 shr 1
        n++
    }
    while (n1 and 1 == 0) {
        n1 = n1 shr 1
    }
    do {
        while (n2 and 1 == 0) {
            n2 = n2 shr 1
        }
        if (n1 > n2) {
            val temp = n1
            n1 = n2
            n2 = temp
        }
        n2 = n2 - n1
    } while (n2 != 0)
    return n1 shl n
}
