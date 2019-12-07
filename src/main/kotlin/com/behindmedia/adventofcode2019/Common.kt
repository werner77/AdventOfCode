package com.behindmedia.adventofcode2019

import java.io.BufferedReader
import java.io.FileNotFoundException
import kotlin.math.abs
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

fun <T>permutate(count: Int, range: IntRange, perform: (List<Int>) -> T?): T? {
    val list = MutableList(count) {
        range.first
    }
    return permutate(list, 0, range, perform)
}

private fun <T>permutate(list: MutableList<Int>, index: Int, range: IntRange, perform: (List<Int>) -> T?): T? {
    if (index >= list.size) {
        return perform(list)
    }

    for (value in range.start..range.endInclusive) {
        list[index] = value
        val ret = permutate(list, index + 1, range, perform)
        if (ret != null) {
            return ret
        }
    }
    return null
}

class Reference<T>(var value: T)

data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {

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

    fun manhattenDistance(to: Coordinate): Int {
        return abs(x - to.x) + abs(y - to.y)
    }

    fun distance(to: Coordinate): Double {
        val deltaX = (x - to.x).toDouble()
        val deltaY = (y - to.y).toDouble()
        return sqrt( deltaX * deltaX + deltaY * deltaY)
    }

    companion object {
        val origin = Coordinate(0, 0)
    }
}
