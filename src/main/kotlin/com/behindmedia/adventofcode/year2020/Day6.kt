package com.behindmedia.adventofcode.year2020

import com.behindmedia.adventofcode.common.parseLines

class Day6 {

    fun part1(input: String): Int {
        val answers = mutableSetOf<Char>()
        var sum = 0
        parseLines(input) {
            if (it.isBlank()) {
                sum += answers.size
                answers.clear()
            } else {
                answers.addAll(it.toList())
            }
        }
        sum += answers.size
        return sum
    }

    fun part2(input: String): Int {
        val answers = mutableSetOf<Char>()
        var sum = 0
        var first = true
        parseLines(input) {
            if (it.isBlank()) {
                println(answers.size)
                sum += answers.size
                first = true
                answers.clear()
            } else {
                if (first) {
                    answers.addAll(it.toList())
                    first = false
                } else {
                    answers.retainAll(it.toSet())
                }
            }
        }
        println(answers.size)
        sum += answers.size
        return sum
    }
}