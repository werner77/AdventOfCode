package com.behindmedia.adventofcode.year2021.day19

import com.behindmedia.adventofcode.common.*
import kotlin.math.max

private data class ScannerMatch(val scanner: Scanner, val transform: Transform, val parent: ScannerMatch? = null) {
    fun transformedCoordinates(): List<Coordinate3D> = transformCoordinates(scanner.coordinates)
    fun transformedOrigin(): Coordinate3D = transformCoordinate(Coordinate3D.origin)

    private fun transformCoordinates(coordinates: Collection<Coordinate3D>): List<Coordinate3D> {
        return coordinates.map {
            transformCoordinate(it)
        }
    }

    private fun transformCoordinate(it: Coordinate3D): Coordinate3D {
        var transformedCoordinate = it
        var current: ScannerMatch = this
        while (true) {
            transformedCoordinate = current.transform.transform(transformedCoordinate)
            current = current.parent ?: break
        }
        return transformedCoordinate
    }
}

private data class Transform(val translation: Coordinate3D, val orientation: Orientation) {
    companion object {
        val identity = Transform(Coordinate3D.origin, Orientation.identity)
    }

    fun transform(coordinate: Coordinate3D): Coordinate3D = translation + orientation.transform(coordinate)
}

private class Orientation(private val mapping: IntArray, private val sign: IntArray) {
    constructor(mapping: List<Int>, sign: List<Int>) : this(mapping.toIntArray(), sign.toIntArray())
    init {
        require(mapping.size == 3) { "Mapping size should be 3" }
        require(sign.size == 3) { "Sign size should be 3" }
    }

    companion object {
        val identity = Orientation(listOf(0, 1, 2), listOf(1, 1, 1))
        val all: List<Orientation> = mutableListOf<Orientation>().apply {
            // We generate the mappings of component (x, y, z) = (0, 1, 2) to another component. For each permutation we
            // can have +1 and -1 for each value.
            permutateUnique(setOf(0, 1, 2)) {
                // For each
                for (x in listOf(-1, 1)) for (y in listOf(-1, 1)) for (z in listOf(-1, 1)) {
                    this += Orientation(it.toList(), listOf(x, y, z))
                }
                null
            }
        }
    }

    fun transform(coordinate: Coordinate3D): Coordinate3D = Coordinate3D(
        coordinate[mapping[0]] * sign[0],
        coordinate[mapping[1]] * sign[1],
        coordinate[mapping[2]] * sign[2]
    )
}

private data class Scanner(val id: Int, val coordinates: Set<Coordinate3D>) {
    override fun equals(other: Any?): Boolean = (id == (other as? Scanner)?.id)
    override fun hashCode(): Int = id

    /**
     * Try to find a match between two scanners. Return the offset and rotation needed.
     */
    fun match(other: Scanner): Transform? {
        for (orientation in Orientation.all) {
            // Calculate the offset by using this orientation
            val mappedCoordinates = other.coordinates.map { orientation.transform(it) }.toTypedArray()
            for (c1 in coordinates) {
                for (c2 in mappedCoordinates) {
                    if (isMatch(c1 - c2, this.coordinates, mappedCoordinates)) return Transform(c1 - c2, orientation)
                }
            }
        }
        return null
    }

    private fun isMatch(offset: Coordinate3D, list1: Set<Coordinate3D>, list2: Array<Coordinate3D>): Boolean {
        var count = 0
        var remaining = list2.size
        for (j in list2.indices) {
            val c2 = list2[j] + offset
            if (list1.contains(c2)) {
                if (++count == 12) return true
            }
            if (--remaining + count < 12) return false
        }
        return false
    }
}

private fun MutableCollection<ScannerMatch>.findMatch(unresolved: Collection<Scanner>): ScannerMatch? {
    for (u in unresolved) {
        for (r in this) {
            return r.scanner.match(u)?.let { ScannerMatch(u, it, r) } ?: continue
        }
    }
    return null
}

private fun parseScannerMap(text: String): MutableMap<Int, Scanner> {
    val scannerParts = text.split("\n\n")
    val scanners = mutableMapOf<Int, Scanner>()
    for (part in scannerParts) {
        val lines = part.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        val scannerId = lines[0].split(" ").mapNotNull { it.toIntOrNull() }.single()
        val coordinates = (1 until lines.size).fold(mutableSetOf<Coordinate3D>()) { list, value ->
            val components = lines[value].split(",").map { it.toLong() }
            list += Coordinate3D(components[0], components[1], components[2])
            list
        }
        scanners[scannerId] = Scanner(scannerId, coordinates)
    }
    return scanners
}

fun main() {
    val scanners = parse("/2021/day19.txt") { text ->
        parseScannerMap(text)
    }

    timing {
        val first = scanners[0] ?: error("No scanner 0 found")
        val unresolved: MutableSet<Scanner> = scanners.values.toMutableSet()
        val resolved = mutableListOf(ScannerMatch(first, Transform.identity))
        val allBeacons = mutableSetOf<Coordinate3D>()
        val allScanners = mutableSetOf<Coordinate3D>()
        while (unresolved.isNotEmpty()) {
            val match = if (resolved.isEmpty())
                ScannerMatch(first, Transform.identity) else
                resolved.findMatch(unresolved) ?: error("Could not find match")
            allBeacons += match.transformedCoordinates()
            allScanners += match.transformedOrigin()
            unresolved -= match.scanner
            resolved += match
        }

        // Part 1
        println(allBeacons.size)

        // Part 2
        var maxDist = Long.MIN_VALUE
        permutateUnique(allScanners, 2) { (c1, c2) ->
            maxDist = max(maxDist, c1.manhattenDistance(c2))
            null
        }
        println(maxDist)
    }
}
