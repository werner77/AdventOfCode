package com.behindmedia.adventofcode.year2018.day4

import com.behindmedia.adventofcode.common.parseLines
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/*
    [1518-11-01 00:00] Guard #10 begins shift
    [1518-11-01 00:05] falls asleep
    [1518-11-01 00:25] wakes up
    [1518-11-01 00:30] falls asleep
    [1518-11-01 00:55] wakes up
     */
private sealed class Event {
    class Begin(val guardId: Int) : Event()
    object Sleep : Event()
    object WakeUp : Event()

    companion object {
        private val beginRegex = Regex("Guard #([0-9]+) begins shift")

        fun fromString(s: String) : Event {
            val matchResult = beginRegex.find(s)
            return if (matchResult != null) {
                val guardId = matchResult.groups[1]?.value?.toInt() ?: throw IllegalArgumentException("Could not determine guardId")
                Begin(guardId)
            } else {
                when(s) {
                    "falls asleep" -> Sleep
                    "wakes up" -> WakeUp
                    else -> throw IllegalArgumentException("Could not parse string: $s")
                }
            }
        }
    }
}

private data class Entry(val event: Event, val date: Date) {
    companion object {

        private val dateFormat: DateFormat by lazy {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
            df
        }

        fun fromString(s: String) : Entry {
            val components = s.split("[", "]").filter { !it.isEmpty() }.map { it.trim() }

            if (components.count() != 2) {
                throw IllegalArgumentException("Invalid string: ${s}")
            }

            val dateString = components[0]
            val actionString = components[1]

            val date = dateFormat.parse(dateString)
            val event = Event.fromString(actionString)

            return Entry(event, date)
        }
    }
}

data class GuardContext(val guardId: Int) {
    private val minutes = Array<Int>(60) { 0 }

    var sleepCount: Int = 0
        private set

    var maxMinute: Int = 0
        private set

    val maxMinuteCount: Int
        get() {
            return minutes[maxMinute]
        }

    fun increment(fromMinute: Int, untilMinute: Int) {
        for (i in fromMinute until untilMinute) {
            minutes[i]++
            sleepCount++
            if (minutes[i] > maxMinuteCount) {
                maxMinute = i
            }
        }
    }
}

private fun analyze(entries: List<Entry>, maxGuardSelector: (GuardContext, GuardContext) -> GuardContext) : Int? {
    val sortedEntries = entries.sortedBy(Entry::date)
    var sleeping = false
    var lastMinute = 0
    val map = mutableMapOf<Int, GuardContext>()
    var currentGuardContext: GuardContext? = null
    var maxGuardContext: GuardContext? = null

    for (entry in sortedEntries) {

        val currentDate = entry.date
        var sameDay = true

        val currentMinute = when {
            currentDate.hours == 0 -> currentDate.minutes
            currentDate.hours < 12 -> 60
            else -> {
                sameDay = false
                0
            }
        }

        if (sleeping) {
            if (currentGuardContext == null) {
                throw IllegalStateException("Guard context should not be null")
            }
            val untilMinute = if (sameDay) currentMinute else 60

            currentGuardContext.increment(lastMinute, untilMinute)

            maxGuardContext = maxGuardSelector(currentGuardContext, maxGuardContext ?: currentGuardContext)
        }

        when(entry.event) {
            is Event.Begin -> {
                sleeping = false
                currentGuardContext = map.getOrPut(entry.event.guardId) {
                    GuardContext(entry.event.guardId)
                }
            }
            is Event.Sleep -> sleeping = true
            is Event.WakeUp -> sleeping = false
        }

        lastMinute = currentMinute
    }

    if (maxGuardContext == null) {
        return null
    }

    return maxGuardContext.maxMinute * maxGuardContext.guardId
}

private fun part1(entries: List<Entry>) {
    val ans = analyze(entries) { context1, context2 ->
        if (context1.sleepCount > context2.sleepCount) context1 else context2
    } ?: error("No answer found")
    println(ans)
}

private fun part2(entries: List<Entry>) {
    val ans = analyze(entries) { context1, context2 ->
        if (context1.maxMinuteCount > context2.maxMinuteCount) context1 else context2
    } ?: error("No answer found")
    println(ans)
}

fun main() {
    val entries = parseLines("/2018/day4.txt") {
        Entry.fromString(it)
    }
    part1(entries)
    part2(entries)
}