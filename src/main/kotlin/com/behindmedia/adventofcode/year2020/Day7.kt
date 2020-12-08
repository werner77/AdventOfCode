package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines

class Day7 {

    fun part1(input: String, startBag: String = "shiny gold"): Int {
        val map = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        parseLines(input) {
            // posh lavender bags contain 5 striped silver bags, 3 wavy beige bags, 3 dim brown bags, 5 clear indigo bags.
            val regex1 = Regex("""([a-z ]+) bag[s]? contain""")
            val regex2 = Regex("""([0-9]+) ([a-z ]+) bag[s]?""")

            val matchResult1 = regex1.find(it) ?: error("Could not find match")

            val value = matchResult1.groupValues[1].trim()

            var pos = matchResult1.range.last + 1
            while (pos < it.length) {
                val matchResult2 = regex2.find(it, pos) ?: break
                val count = matchResult2.groupValues[1].toInt()
                val key = matchResult2.groupValues[2].trim()
                map.getOrPut(key) { mutableListOf() }.add(Pair(value, count))
                pos = matchResult2.range.last + 1
            }
        }

        val found = mutableSetOf<String>()
        traverse1(map, startBag, found)
        return found.size
    }

    fun part2(input: String, startBag: String = "shiny gold"): Int {
        val map = mutableMapOf<String, MutableList<Pair<String, Int>>>()
        parseLines(input) {
            // posh lavender bags contain 5 striped silver bags, 3 wavy beige bags, 3 dim brown bags, 5 clear indigo bags.
            val regex1 = Regex("""([a-z ]+) bag[s]? contain""")
            val regex2 = Regex("""([0-9]+) ([a-z ]+) bag[s]?""")

            val matchResult1 = regex1.find(it) ?: error("Could not find match")

            val key = matchResult1.groupValues[1].trim()
            val values = map.getOrPut(key) { mutableListOf() }

            var pos = matchResult1.range.last + 1
            while (pos < it.length) {
                val matchResult2 = regex2.find(it, pos) ?: break
                val count = matchResult2.groupValues[1].toInt()
                val value = matchResult2.groupValues[2].trim()
                values.add(Pair(value, count))
                pos = matchResult2.range.last + 1
            }
        }

        val cache = mutableMapOf<String, Int>()
        return traverse2(map, startBag, cache) - 1
    }

    private fun traverse1(map: Map<String, List<Pair<String, Int>>>, bag: String, found: MutableSet<String>) {
        val list = map[bag] ?: emptyList()
        list.forEach {
            if (!found.contains(it.first)) {
                found.add(it.first)
                traverse1(map, it.first, found)
            }
        }
    }

    private fun traverse2(map: Map<String, List<Pair<String, Int>>>, bag: String, found: MutableMap<String, Int>): Int {
        val list = map[bag] ?: emptyList()
        var count = 1
        list.forEach {
            val bagCount = found.getOrPut(it.first) { traverse2(map, it.first, found) }
            count += it.second * bagCount
        }
        return count
    }
}
