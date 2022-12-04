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

fun <T>parseNonBlankLines(resource: String, parser: (String) -> T) : List<T> {
    val reader = readerForResource(resource)
    return reader.use {
        val ret = mutableListOf<T>()
        it.forEachLine { line ->
            if (line.isNotBlank()) {
                ret.add(parser(line))
            }
        }
        ret
    }
}

fun <T>parseLinesFromString(string: String, parser: (String) -> T): List<T> {
    val ret = mutableListOf<T>()
    string.trim().split("\n").forEach { line ->
        ret.add(parser(line))
    }
    return ret
}

/**
 * Parses the lines of the specified resource as a map of values, assuming the origin is in the upper left corner.
 */
fun <T>parseMap(resource: String, converter: (Char) -> T?) : Map<Coordinate, T> = parseMapFromString(read(resource), converter)

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

fun parseMapFromString(string: String) : Map<Coordinate, Char> = parseMapFromString(string) { it }

/**
 * Parses the specified resource using the supplied parser.
 */
fun <T>parse(resource: String, parser: (String) -> T) : T {
    return parser(read(resource))
}

fun <T>parseString(string: String, parser: (String) -> T) : T {
    return parser(string)
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

fun <T> String.splitToSequenceByCharactersInString(delimiters: String, conversion: (String) -> T): Sequence<T> {
    return splitToSequence(delimiters = delimiters.toCharArray())
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

operator fun StringBuilder.plusAssign(other: CharSequence) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: String) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: Any) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: StringBuffer) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: Char) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: Int) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: Double) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: Boolean) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: Long) {
    this.append(other)
}

operator fun StringBuilder.plusAssign(other: Float) {
    this.append(other)
}
