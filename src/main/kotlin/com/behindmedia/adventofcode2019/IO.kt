package com.behindmedia.adventofcode2019

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

private fun readerForResource(resource: String): BufferedReader {
    val url = object {}.javaClass.getResource(resource)
        ?: throw FileNotFoundException("Resource with name $resource could not be found")

    val stream = url.openStream()
    return stream.bufferedReader()
}
