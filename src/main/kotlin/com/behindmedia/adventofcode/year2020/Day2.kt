package com.behindmedia.adventofcode.year2020

class Day2 {
    fun part1(input: List<String>): Int {
        // 13-15 w: nwwwwwqwwwwwtww
        var count = 0
        for (line in input) {
            val components = line.split(" ");
            val fromTo = components[0].split("-")
            val from = fromTo[0].toInt()
            val to = fromTo[1].toInt()
            val c = components[1].toCharArray()[0]
            val pwd = components[2]

            if (isValid1(from, to, c, pwd)) {
                count++
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        var count = 0
        for (line in input) {
            val components = line.split(" ");
            val fromTo = components[0].split("-")
            val from = fromTo[0].toInt()
            val to = fromTo[1].toInt()
            val c = components[1].toCharArray()[0]
            val pwd = components[2]

            if (isValid2(from, to, c, pwd)) {
                count++
            }
        }
        return count
    }

    private fun isValid1(from: Int, to: Int, c: Char, pwd: String): Boolean {
        var count = 0
        for (c1 in pwd.toCharArray()) {
            if (c1 == c) {
                count++
            }
        }
        return count in from..to
    }

    private fun isValid2(from: Int, to: Int, c: Char, pwd: String): Boolean {
        val chars = pwd.toCharArray()
        var count = 0
        if (c == chars[from - 1]) count++
        if (c == chars[to - 1]) count++
        return count == 1
    }
 }