package com.behindmedia.adventofcode2019

import java.io.BufferedReader
import java.io.FileNotFoundException

fun <T>parseLines(resource: String, parser: (String) -> T) : List<T> {
    val reader = readerForResource(resource)
    val ret = mutableListOf<T>()
    reader.forEachLine {
        ret.add(parser(it))
    }
    return ret
}

fun <T>parse(resource: String, parser: (String) -> T) : T {
    return parser(read(resource))
}

fun read(resource: String): String {
    return readerForResource(resource).readText()
}

fun readerForResource(resource: String): BufferedReader {
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

}
