package com.behindmedia.adventofcode.year2023.day5

import com.behindmedia.adventofcode.common.read
import com.behindmedia.adventofcode.common.timing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong

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
    timing {
        // Construct the inverse mappings
        val inverseAlmanac = Almanac(string = input, inverse = true)

        // The valid ranges of seeds
        val seedRanges = inverseAlmanac.seeds.chunked(2).map { (start, len) -> start until start + len }

        // Parallelize the search for the minimum location
        val coroutineCount = Runtime.getRuntime().availableProcessors()
        val location = AtomicLong(0L)
        val result = runBlocking {
            (0 until coroutineCount).map {
                async(Dispatchers.Default) {
                    var current: Long
                    while (true) {
                        current = location.getAndIncrement()
                        if (seedRanges.any { range ->
                                val seed = inverseAlmanac.process(current)
                                range.contains(seed) && almanac.process(seed) == current
                            }) break
                    }
                    current
                }
            }.awaitAll().min()
        }
        println(result)
    }
}
