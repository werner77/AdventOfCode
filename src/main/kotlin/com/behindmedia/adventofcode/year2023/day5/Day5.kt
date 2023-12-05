package com.behindmedia.adventofcode.year2023.day5

import com.behindmedia.adventofcode.common.binarySearch
import com.behindmedia.adventofcode.common.read

data class Almanac(val seeds: List<Long>, val mappings: Map<String, MappingGroup>) {

    private val categories: List<String> by lazy {
        mappings.keys.toList()
    }

    companion object {
        operator fun invoke(string: String, inverse: Boolean = false): Almanac {
            val sections = string.split("\n\n")
            val seeds = mutableListOf<Long>()
            val mappings = LinkedHashMap<String, MappingGroup>()
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
                        val (dest, source, length) = line.split(" ").map { it.toLong() }
                        mappingList += Mapping(
                            source = if (inverse) dest else source,
                            dest = if (inverse) source else dest,
                            size = length,
                        )
                    }
                    mappings[if (inverse) sourceCategory else targetCategory] = MappingGroup(
                        sourceCategory = if (inverse) targetCategory else sourceCategory,
                        destCategory = if (inverse) sourceCategory else targetCategory,
                        mappings = mappingList
                    )
                }
            }
            return Almanac(seeds = seeds, mappings = if (inverse) mappings.reversed() else mappings)
        }
    }

    fun process(value: Long, categoryIndex: Int): Long {
        if (categoryIndex == categories.size) {
            return value
        }
        val targetCategory = categories[categoryIndex]
        val mapping = mappings[targetCategory] ?: error("No mapping found")
        val mappedValue = mapping.mappedValue(value)
        return process(mappedValue, categoryIndex + 1)
    }
}

private fun <K, V>Map<K, V>.reversed(): Map<K, V> = this.entries.reversed().fold(LinkedHashMap<K, V>()) { map, entry ->
    map.apply {
        put(entry.key, entry.value)
    }
}

data class Mapping(val source: Long, val dest: Long, val size: Long) {
    fun mappedValue(value: Long): Long? {
        return if (value >= source && value - source < size) dest + (value - source) else null
    }
}

data class MappingGroup(val sourceCategory: String, val destCategory: String, val mappings: List<Mapping>) {
    fun mappedValue(value: Long): Long {
        return mappings.firstNotNullOfOrNull { it.mappedValue(value) } ?: value
    }
}

fun main() {
    val input = read("/2023/day5.txt")

    val almanac1 = Almanac(string = input)
    val part1 = almanac1.seeds.minOf { seed -> almanac1.process(seed, 0) }

    // Part 1
    println(part1)

    // Part2
    val almanac2 = Almanac(string = input, inverse = true)
    val seedRanges = almanac2.seeds.chunked(2).map { (start, len) -> LongRange(start, start + len - 1) }

    var part2 = part1
    while (true) {
        part2 = binarySearch(lowerBound = 0, upperBound = part2 - 1, inverted = true) { value ->
            seedRanges.any { it.contains(almanac2.process(value, 0)) }
        } ?: break
    }
    println(part2)
}
