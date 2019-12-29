package com.behindmedia.adventofcode.year2018

fun <T>parseList(resource: String, parser: (String) -> T) : List<T> {
    val fileUrl = object {}.javaClass.getResource(resource).openStream()
    val ret = mutableListOf<T>()
    fileUrl.bufferedReader().forEachLine {
        ret.add(parser(it))
    }
    return ret
}

fun <T>parse(resource: String, parser: (String) -> T) : T {
    return parser(read(resource))
}

fun read(resource: String): String {
    val fileUrl = object {}.javaClass.getResource(resource).openStream()
    return fileUrl.bufferedReader().readText()
}

fun <T>MutableCollection<T>.popFirst(): T? {
    val iterator = this.iterator()
    if (iterator.hasNext()) {
        val result = iterator.next()
        iterator.remove()
        return result
    }
    return null
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

fun <E>Collection<E>.only(): E {
    if (this.size != 1) {
        throw IllegalStateException("Expected exactly one element to be present")
    }
    return this.first()
}
