package com.behindmedia.adventofcode.common

import java.io.BufferedReader
import java.io.FileNotFoundException

/**
 * Parses the lines of the specified resource using the supplied parser and returns a list of each parsed line.
 */
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

/**
 * Parses the lines of the specified resource as a map of values, assuming the origin is in the upper left corner.
 */
fun <T>parseMap(resource: String, converter: (Char) -> T?) : Map<Coordinate, T> {
    var y = 0
    val result = mutableMapOf<Coordinate, T>()
    parseLines(resource) {
        var x = 0
        for (c in it) {
            converter(c)?.let {
                result[Coordinate(x, y)] = it
            }
            x += 1
        }
        y += 1
    }
    return result
}

fun <T>parseMapFromString(string: String, converter: (Char) -> T?) : Map<Coordinate, T> {
    var y = 0
    val result = mutableMapOf<Coordinate, T>()
    string.split("\n").forEach {
        var x = 0
        for (c in it) {
            converter(c)?.let {
                result[Coordinate(x, y)] = it
            }
            x += 1
        }
        y += 1
    }
    return result
}

/**
 * Parses the specified resource using the supplied parser.
 */
fun <T>parse(resource: String, parser: (String) -> T) : T {
    return parser(read(resource))
}

/**
 * Reads the specified resource as string from the classpath
 */
fun read(resource: String): String {
    return readerForResource(resource).use {
        it.readText()
    }
}

fun String.splitNonEmptySequence(vararg delimiters: String): Sequence<String> {
    return splitNonEmptySequence(delimiters = delimiters) { it }
}

fun <T> String.splitNonEmptySequence(vararg delimiters: String, conversion: (String) -> T): Sequence<T> {
    return splitToSequence(delimiters = delimiters)
        .filter { it.isNotEmpty() }
        .map { conversion.invoke(it) }
}

fun <T>timing(block: () -> T) {
    val start = System.currentTimeMillis()
    try {
        block.invoke()
    } finally {
        val end = System.currentTimeMillis()
        println("Took ${end - start} ms.")
    }
}

private fun readerForResource(resource: String): BufferedReader {
    val url = object {}.javaClass.getResource(resource)
        ?: throw FileNotFoundException("Resource with name $resource could not be found")

    val stream = url.openStream()
    return stream.bufferedReader()
}
