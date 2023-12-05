package com.behindmedia.adventofcode.year2023.day5

import com.behindmedia.adventofcode.common.binarySearch
import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing

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
    timing {
        println(part1)
    }

    // Part2
    timing {
        val inverseAlmanac = Almanac(string = input, inverse = true)
        // Convert to long ranges
        val seedRanges = inverseAlmanac.seeds.chunked(2).map { (start, len) -> LongRange(start, start + len - 1) }

        // Find local minima and lower the upper bound every time until we find no valid result anymore
        var part2 = part1
        var findMin = true
        while (true) {
            part2 = binarySearch(lowerBound = 0, upperBound = part2 - 1, inverted = findMin) { value ->
                // Valid if any of the ranges contains the value
                seedRanges.any { it.contains(inverseAlmanac.process(value)) }
            } ?: break
            // After we found a min, we find the next max (next range below the current range)
            findMin = !findMin
        }
        println(part2)
    }
}
