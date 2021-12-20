package com.behindmedia.adventofcode.year2021.day19

import com.behindmedia.adventofcode.common.*
import kotlin.math.max

private data class ScannerMatch(val scanner: Scanner, val transform: Transform, val parent: ScannerMatch? = null) {
    fun transformedCoordinates(): List<Coordinate3D> = transformCoordinates(scanner.coordinates)
    fun transformedOrigin(): Coordinate3D = transformCoordinates(listOf(Coordinate3D.origin))[0]

    private fun transformCoordinates(coordinates: List<Coordinate3D>): List<Coordinate3D> {
        var current: ScannerMatch = this
        var transformedCoordinates = coordinates
        while (true) {
            transformedCoordinates = transformedCoordinates.map {
                current.transform.transform(it)
            }
            current = current.parent ?: break
        }
        return transformedCoordinates
    }
}

private data class Transform(val translation: Coordinate3D, val orientation: Orientation) {
    companion object {
        val identity = Transform(Coordinate3D.origin, Orientation.identity)
    }

    fun transform(coordinate: Coordinate3D): Coordinate3D = translation + orientation.apply(coordinate)
}

private data class Orientation(private val mapping: List<Int>, private val sign: List<Int>) {
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

    fun apply(coordinate: Coordinate3D): Coordinate3D = Coordinate3D(
        coordinate[mapping[0]] * sign[0],
        coordinate[mapping[1]] * sign[1],
        coordinate[mapping[2]] * sign[2]
    )
}

private data class Scanner(val id: Int, val coordinates: List<Coordinate3D>) {
    override fun equals(other: Any?): Boolean = (id == (other as? Scanner)?.id)
    override fun hashCode(): Int = id
    /**
     * Try to find a match between two scanners. Return the offset and rotation needed.
     */
    fun match(other: Scanner): Transform? {
        for (orientation in Orientation.all) {
            // Calculate the offset by using this orientation
            val mappedCoordinates = other.coordinates.map { orientation.apply(it) }
            val m = max(coordinates.size, mappedCoordinates.size)
            for (i in 0 until m) {
                for (j in i + 1 until m) {
                    val c1 = coordinates.getOrNull(i) ?: continue
                    val c2 = mappedCoordinates.getOrNull(j) ?: continue
                    val offset = c1 - c2
                    if (isMatch(offset, coordinates, mappedCoordinates)) return Transform(offset, orientation)
                }
            }
        }
        return null
    }

    private fun isMatch(offset: Coordinate3D, list1: List<Coordinate3D>, list2: List<Coordinate3D>): Boolean {
        val count = list1.count { i ->
            list2.any { j -> i == j + offset }
        }
        return count >= 12
    }
}

private fun Collection<ScannerMatch>.findMatch(unresolved: Collection<Scanner>): ScannerMatch? {
    for (u in unresolved) {
        for (r in this) {
            val m = r.scanner.match(u)
            if (m != null) {
                return ScannerMatch(u, m, r)
            }
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
        val coordinates = (1 until lines.size).fold(mutableListOf<Coordinate3D>()) { list, value ->
            val components = lines[value].split(",").map { it.toInt() }
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
        var maxDist = Int.MIN_VALUE
        permutateUnique(allScanners, 2) {
            maxDist = max(maxDist, it[0].manhattenDistance(it[1]))
            null
        }
        println(maxDist)
    }
}
