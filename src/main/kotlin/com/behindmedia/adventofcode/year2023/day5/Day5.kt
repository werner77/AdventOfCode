package com.behindmedia.adventofcode.year2023.day5

import com.behindmedia.adventofcode.common.intersection
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

    fun process(valueRange: LongRange): List<LongRange> {
        var result = listOf(valueRange)
        for (mapping in mappings) {
            val next = mutableListOf<LongRange>()
            for (range in result) {
                next += mapping.mappedValue(range)
            }
            result = next
        }
        return result
    }
}

data class Mapping(val source: Long, val dest: Long, val size: Long) {
    private val range: LongRange
        get() = source until source + size

    fun mappedValue(value: Long): Long? {
        return if (value in range) dest + (value - source) else null
    }

    fun mappedValue(valueRange: LongRange): Pair<LongRange?, List<LongRange>> {
        val intersectionRange = range.intersection(valueRange)
        return if (intersectionRange == null) {
            // No intersection: just return the current range as remaining range
            null to listOf(valueRange)
        } else {
            // Split the value range by the intersection
            val mappedRange = intersectionRange.let { matchedRange ->
                LongRange(dest + matchedRange.first - source, dest + matchedRange.last - source)
            }
            val remainingRanges = mutableListOf<LongRange>()
            if (intersectionRange.first > valueRange.first) {
                remainingRanges.add(valueRange.first..<intersectionRange.first)
            }
            if (intersectionRange.last < valueRange.last) {
                remainingRanges.add(intersectionRange.last + 1..valueRange.last)
            }
            mappedRange to remainingRanges
        }
    }
}

data class MappingGroup(val sourceCategory: String, val destCategory: String, val mappings: List<Mapping>) {
    fun mappedValue(value: Long): Long {
        return mappings.firstNotNullOfOrNull { it.mappedValue(value) } ?: value
    }

    fun mappedValue(valueRange: LongRange): List<LongRange> {
        val result = mutableListOf<LongRange>()
        val pending = ArrayDeque<Pair<Int, LongRange>>()
        pending += 0 to valueRange
        while (pending.isNotEmpty()) {
            val (mappingIndex, range) = pending.removeFirst()
            val mapping = mappings.getOrNull(mappingIndex)
            if (mapping == null) {
                result += range
            } else {
                val (matched, remaining) = mapping.mappedValue(range)
                if (matched != null) {
                    result += matched
                }
                remaining.forEach {
                    pending += mappingIndex + 1 to it
                }
            }
        }
        return result
    }
}

fun main() {
    val input = read("/2023/day5.txt")
    timing {
        val almanac = Almanac(string = input)

        // Part 1
        println(almanac.seeds.minOf { seed -> almanac.process(seed) })

        // Part 2
        val seedRanges = almanac.seeds.chunked(2).map { (start, len) -> start until start + len }
        val allRanges = seedRanges.map { almanac.process(it) }.flatten()
        println(allRanges.minOf { it.first })
    }
}
