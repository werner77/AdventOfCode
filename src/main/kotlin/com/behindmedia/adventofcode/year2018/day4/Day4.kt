package com.behindmedia.adventofcode.year2018.day4

import com.behindmedia.adventofcode.common.*
import java.time.format.DateTimeFormatter

/*
[1518-11-01 00:00] Guard #10 begins shift
[1518-11-01 00:05] falls asleep
[1518-11-01 00:25] wakes up
[1518-11-01 00:30] falls asleep
[1518-11-01 00:55] wakes up
[1518-11-01 23:58] Guard #99 begins shift
[1518-11-02 00:40] falls asleep
[1518-11-02 00:50] wakes up
[1518-11-03 00:05] Guard #10 begins shift
[1518-11-03 00:24] falls asleep
[1518-11-03 00:29] wakes up
[1518-11-04 00:02] Guard #99 begins shift
[1518-11-04 00:36] falls asleep
[1518-11-04 00:46] wakes up
[1518-11-05 00:03] Guard #99 begins shift
[1518-11-05 00:45] falls asleep
[1518-11-05 00:55] wakes up
 */


fun main() {
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val regex = """\[(\d\d\d\d-\d\d-\d\d \d\d:\d\d)\] (.*)""".toRegex()

    var activeGuard: Int? = null

    parseLines("/2018/day4.txt") { line ->
        val match = regex.matchEntire(line) ?: error("No match")
        val (dateString, rest) = match.destructured
        val date = dateFormat.parse(dateString)
        when  (rest) {
            "wakes up" -> println("wake up")
            "falls asleep" -> println("sleep")
            else -> {
                val guardIdString = rest.split(" ").get(1)
                require(guardIdString.first() == '#')
                val guardId = guardIdString.substring(1).toInt()
                activeGuard = guardId
            }
        }
    }
}