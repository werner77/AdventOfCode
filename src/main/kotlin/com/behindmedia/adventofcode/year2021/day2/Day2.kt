package com.behindmedia.adventofcode.year2021.day2

import com.behindmedia.adventofcode.common.*

private fun part1() {
    var x = 0L
    var y = 0L
    parseLines("/2021/day2.txt") {
        val components = it.split(" ")
        val amount = components[1].toLong()
        when (components[0]) {
            "forward" -> {
                x += amount
            }
            "up" -> {
                y -= amount
            }
            "down" -> {
                y += amount
            }
            else -> error("Parse error")
        }
    }
    println(x * y)
}

private fun part2() {
    var aim = 0L
    var x = 0L
    var y = 0L
    parseLines("/2021/day2.txt") {
        val components = it.split(" ")
        val amount = components[1].toLong()
        when (components[0]) {
            "forward" -> {
                x += amount
                y += aim * amount
            }
            "up" -> {
                aim -= amount
            }
            "down" -> {
                aim += amount
            }
            else -> error("Parse error")
        }
    }
    println(x * y)
}

fun main() {
    part1()
    part2()
}