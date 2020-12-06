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
                answers.addAll(it.toCharArray().toList())
            }
        }
        sum += answers.size
        return sum
    }

    fun part2(input: String): Int {
        val answers = mutableSetOf<Char>()
        var sum = 0
        var lineCount = 0
        parseLines(input) {
            if (it.isBlank()) {
                sum += answers.size
                lineCount = 0
                answers.clear()
            } else {
                if (lineCount == 0) {
                    answers.addAll(it.toCharArray().toList())
                } else {
                    answers.retainAll(it.toCharArray().toList())
                }
                lineCount++
            }
        }
        sum += answers.size
        return sum
    }
}