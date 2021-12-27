package com.behindmedia.adventofcode.year2015.day12

import com.behindmedia.adventofcode.common.*
import kotlinx.serialization.json.*
import kotlin.math.*

private fun walkTree(element: JsonElement, blockingValue: String? = null, perform: (JsonPrimitive) -> Unit) {
    when (element) {
        is JsonPrimitive -> {
            perform(element)
        }
        is JsonObject -> {
            if (blockingValue == null || !element.values.contains(JsonPrimitive(blockingValue))) {
                for (entry in element.entries) {
                    walkTree(entry.value, blockingValue, perform)
                }
            }
        }
        is JsonArray -> {
            for (entry in element) {
                walkTree(entry, blockingValue, perform)
            }
        }
    }
}

fun main() {
    val data = read("/2015/day12.txt").trim()
    val jsonElement = Json.parseToJsonElement(data)

    // Part 1
    println(solve(jsonElement))

    // Part 2
    println(solve(jsonElement, "red"))
}

private fun solve(jsonElement: JsonElement, blockingValue: String? = null): Long {
    var totalSum = 0L
    walkTree(jsonElement, blockingValue) { value ->
        val intValue = value.longOrNull
        if (intValue != null) totalSum += intValue
    }
    return totalSum
}