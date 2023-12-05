package com.behindmedia.adventofcode.year2023.day5

import com.behindmedia.adventofcode.common.binarySearch
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing
import kotlin.math.min

data class Almanac(val seeds: List<Long>, val mappings: List<MappingGroup>) {

    companion object {
        operator fun invoke(string: String, inverse: Boolean = false): Almanac {
            val sections = string.split("\n\n")
            val seeds = mutableListOf<Long>()
            val mappings = mutableListOf<MappingGroup>()
            for (section in sections) {
                val lines = section.split("\n")
                val header = lines[0]
                if (header.startsWith("seeds:")) {
                    seeds += header.split(": ")[1].split(" ").map { it.toLong() }
                } else {
                    val mappingName = header.split(" ")[0]
                    val (sourceCategory, targetCategory) = mappingName.split("-to-")
                    val mappingList = mutableListOf<Mapping>()
                    for (i in 1 until lines.size) {
                        val line = lines[i]
                        if (line.isEmpty()) continue
                        val (dest, source, size) = line.split(" ").map { it.toLong() }
                        mappingList += Mapping(
                            source = if (inverse) dest else source,
                            dest = if (inverse) source else dest,
                            size = size,
                        )
                    }
                    mappings += MappingGroup(
                        sourceCategory = if (inverse) targetCategory else sourceCategory,
                        destCategory = if (inverse) sourceCategory else targetCategory,
                        mappings = mappingList
                    )
                }
            }
            return Almanac(seeds = seeds, mappings = if (inverse) mappings.reversed() else mappings)
        }
    }

    fun process(value: Long): Long {
        var result = value
        for (mapping in mappings) {
            result = mapping.mappedValue(result)
        }
        return result
    }
}

data class Mapping(val source: Long, val dest: Long, val size: Long) {
    fun mappedValue(value: Long): Long? {
        return if (value in source until source + size) dest + (value - source) else null
    }
}

data class MappingGroup(val sourceCategory: String, val destCategory: String, val mappings: List<Mapping>) {
    fun mappedValue(value: Long): Long {
        return mappings.firstNotNullOfOrNull { it.mappedValue(value) } ?: value
    }
}

fun main() {
    val input = read("/2023/day5.txt")

    val almanac = Almanac(string = input)
    val part1 = almanac.seeds.minOf { seed -> almanac.process(seed) }

    // Part 1
    println(part1)

    // Part2
    // The idea is to reverse all the mappings and check whether the locations correspond to a seed which is in any of the valid ranges.
    // The locations will have ranges themselves, each range has a min and max (obviously).
    // We alternate between finding the max and min of those ranges (using binary search).
    // After the final minimum, we will not find a maximum anymore, so that gives us our result.
    timing {
        // Construct the inverse mappings
        val inverseAlmanac = Almanac(string = input, inverse = true)

        // The valid ranges of seeds
        val seedRanges = inverseAlmanac.seeds.chunked(2).map { (start, len) -> LongRange(start, start + len - 1) }

        // We can start with the best estimate based on the boundaries of the ranges
        var part2 = seedRanges.minOf { range -> min(almanac.process(range.first), almanac.process(range.last)) }

        var findMin = true
        while (true) {
            // If the binary search cannot find a result anymore we found the best result
            part2 = binarySearch(lowerBound = 0, upperBound = part2 - 1, inverted = findMin) { value ->
                // Valid if any of the ranges contains the value
                seedRanges.any { it.contains(inverseAlmanac.process(value)) }
            } ?: break
            // We alternate between finding minima and maxima to catch all ranges
            findMin = !findMin
        }
        println(part2)
    }
}
