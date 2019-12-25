package com.behindmedia.adventofcode2019

import org.junit.Test

import org.junit.Assert.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.logging.Logger

class Day25Test {

    @Test
    fun execute() {
        val initialInput = """
            take mouse
            north
            take ornament
            west
            east
            south
            east
            north
            take astronaut ice cream
            north
            east
            west
            west
            east
            south
            south
            west
            north
            west
            north
            take easter egg
            east
            take hypercube
            north
            east
            take prime number
            west
            south
            south
            north
            north
            inv
            south
            south
            north
            west
            north
            west
            north
            take wreath
            south
            west
            east
            east
            south
            south
            west
            take mug
            west
            inv
            inv
            east
            east
            east
            take ornament
            south
            take mouse
            inv
            east
            north
            north
            west
            east
            east
            west
            south
            south
            west
            north
            west
            west
            west
            drop mug
            drop astronaut ice cream
            drop ornament
            drop easter egg
            north
        """.trimIndent()

        val day25 = Day25()

        val result = day25.adventure(read("/day25.txt"), initialInput, false)

        println(result)
    }
}