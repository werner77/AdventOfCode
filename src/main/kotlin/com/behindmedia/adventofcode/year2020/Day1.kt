package com.behindmedia.adventofcode.year2020

class Day1 {
    fun part1(input: List<Int>, expectedSum: Int): Int {
        for (i in input.indices) {
            for (j in i + 1 until input.size) {
                if (input[i] + input[j] == expectedSum) {
                    return input[i] * input[j]
                }
            }
        }
        error("Could not find two elements with expected sum")
    }

    fun part2(input: List<Int>, expectedSum: Int): Int {
        for (i in input.indices) {
            for (j in i + 1 until input.size) {
                for (k in j + 1 until input.size) {
                    if (input[i] + input[j] + input[k] == expectedSum) {
                        return input[i] * input[j] * input[k]
                    }
                }
            }
        }
        error("Could not find two elements with expected sum")
    }
}