package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.parseLines
import org.junit.Assert.assertEquals
import org.junit.Test

class Day7Test {

    @Test
    fun processSimple() {

        val expected = "CABDFE"

        val entries = parseLines("/2018/day7-simple.txt") {
            Day7.Entry.fromString(it)
        }

        for (i in 0 until 10) {
            val randomizedEntries = entries.shuffled()
            val nodes = Day7().process(randomizedEntries)
            assertEquals(expected, nodes.asString())
        }
    }

    @Test
    fun processFailure() {

        val expected = "CABDFE"

        val entries = listOf<Day7.Entry>(
            Day7.Entry("A", "B"),
            Day7.Entry("F", "E"),
            Day7.Entry("C", "A"),
            Day7.Entry("A", "D"),
            Day7.Entry("C", "F"),
            Day7.Entry("D", "E"),
            Day7.Entry("B", "E")
        )

        val nodes = Day7().process(entries)
        assertEquals(expected, nodes.asString())

    }

    @Test
    fun processTest() {

        val expected = "ABCDEFGHIJ"

        val entries = parseLines("/2018/day7-test.txt") {
            Day7.Entry.fromString(it)
        }

        for (i in 0 until 10) {
            val randomizedEntries = entries.shuffled()

            val nodes = Day7().process(randomizedEntries)

            assertEquals(expected, nodes.asString())
        }
    }

    @Test
    fun process() {

        val entries = parseLines("/2018/day7.txt") {
            Day7.Entry.fromString(it)
        }

        val nodes = Day7().process(entries)
        assertEquals("MNQKRSFWGXPZJCOTVYEBLAHIUD", nodes.asString())
    }

    @Test
    fun processWithWorkersSimple() {
        val entries = parseLines("/2018/day7-simple.txt") {
            Day7.Entry.fromString(it)
        }

        Day7.Node.defaultDuration = 0

        val totalDuration = Day7().processWithWorkers(entries, 2, { it.value[0] - 'A' + 1 }).second
        assertEquals(15, totalDuration)

    }

    @Test
    fun processWithWorkers() {
        val entries = parseLines("/2018/day7.txt") {
            Day7.Entry.fromString(it)
        }

        val totalDuration = Day7().processWithWorkers(entries, 5)
        println(totalDuration)

    }
}