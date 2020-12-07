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
        traverse(map, startBag, found)
        return found.size
    }

    fun part2(input: String, startBag: String = "shiny gold"): Int {
        return 0
    }

    private fun traverse(map: Map<String, List<Pair<String, Int>>>, bag: String, found: MutableSet<String>) {
        val list = map[bag] ?: return
        for (containingBag in list) {
            if (!found.contains(containingBag.first)) {
                found.add(containingBag.first)
                traverse(map, containingBag.first, found)
            }
        }
    }
}

//shiny gold bags contain 5 bright maroon bags, 5 shiny aqua bags, 2 clear lime bags, 2 muted white bags.