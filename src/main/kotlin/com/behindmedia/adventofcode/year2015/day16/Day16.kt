package com.behindmedia.adventofcode.year2015.day16

import com.behindmedia.adventofcode.common.*

data class Sue(val id: Int, val props: Map<String, Int>) {
    companion object {
        operator fun invoke(string: String): Sue {
            val components= string.splitNonEmptySequence(" ", ":", ",").toList()
            val id = components[1].toInt()
            val props = components.subList(2, components.size).chunked(2).fold(mutableMapOf<String, Int>()) { m, v ->
                m[v.first()] = v.last().toInt()
                m
            }
            return Sue(id, props)
        }
    }
    fun matches1(knownProps: Map<String, Int>): Boolean {
        for (entry in props) {
            val known = knownProps[entry.key] ?: continue
            if (known != entry.value) return false
        }
        return true
    }

    fun matches2(knownProps: Map<String, Int>): Boolean {
        for (entry in props) {
            val known = knownProps[entry.key] ?: continue
            when (entry.key) {
                "cats", "trees" -> if (entry.value <= known) return false
                "pomeranians", "goldfish" -> if (entry.value >= known) return false
                else -> if (entry.value != known) return false
            }
        }
        return true
    }
}

private val knownPropsString = """
    children: 3
    cats: 7
    samoyeds: 2
    pomeranians: 3
    akitas: 0
    vizslas: 0
    goldfish: 5
    trees: 3
    cars: 2
    perfumes: 1
""".trimIndent()

// Sue 292: children: 1, cars: 0, vizslas: 5
fun main() {
    val data = parseLines("/2015/day16.txt") { line ->
        Sue(line)
    }

    val knownProps: Map<String, Int> = knownPropsString.trim().split("\n").fold(mutableMapOf()) { m, line ->
        val (name, value) = line.splitNonEmptySequence(" ", ":").toList()
        m[name] = value.toInt()
        m
    }

    val matching = data.filter { it.matches1(knownProps) }

    println(matching)

    val matching2 = data.filter { it.matches2(knownProps) }

    println(matching2)
}