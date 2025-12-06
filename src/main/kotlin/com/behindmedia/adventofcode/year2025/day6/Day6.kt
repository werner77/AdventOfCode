package com.behindmedia.adventofcode.year2025.day6

import com.behindmedia.adventofcode.common.*
import com.behindmedia.adventofcode.common.maxX
import com.behindmedia.adventofcode.common.maxY
import kotlin.collections.get
import kotlin.math.*
import kotlin.toString

private fun calculateTotal(operands: List<Long>, operation: String): Long {
    return when (operation) {
        "+" -> operands.sum()
        "*" -> operands.product()
        else -> error("Unknown operation: $operation")
    }
}

private fun part1(fileName: String):  Long {
    val lines = parseLines("/2025/$fileName") { line ->
        line.splitNonEmpty(" ")
    }
    val operations = lines.last()
    val data = lines.dropLast(1)
    val columnCount = data.first().size
    val rowCount = data.size
    return (0 until columnCount).sumOf { col ->
        val operands = (0 until rowCount).map { row -> data[row][col].toLong() }
        calculateTotal(operands, operations[col])
    }
}

private fun parseInput2(fileName: String): Pair<List<List<String>>, List<String>> {
    val grid = CharGrid.invoke(read("/2025/$fileName"))
    val stringBuilder = StringBuilder()
    for (row in 0 until grid.maxY) {
        for (col in 0..grid.maxX) {
            if ((0 until grid.maxY).all { grid[Coordinate(col, it)] == ' ' }) {
                stringBuilder.append(",")
            } else {
                stringBuilder.append(grid[Coordinate(col, row)])
            }
        }
        stringBuilder.append("\n")
    }
    val operations = mutableListOf<String>()
    for (col in 0..grid.maxX) {
        val value = grid[Coordinate(col, grid.maxY)]
        if (value in listOf('+', '*')) {
            operations.add(value.toString())
        }
    }
    val data = stringBuilder.toString().trim().split("\n").map { line ->
        line.split(",")
    }
    return data to operations
}

private fun part2(fileName: String): Long {
    val (data, operations) = parseInput2(fileName)
    require(data.isNotEmpty())
    val columnCount = data.first().size
    val rowCount = data.size
    return (0 until columnCount).sumOf { col ->
        val operands = mutableListOf<Long>()
        var digitIndex = 0
        while (true) {
            var columnValue = ""
            for (row in 0 until rowCount) {
                val digitValue = data[row][col]
                val c = digitValue.getOrNull(digitValue.length - 1 - digitIndex).takeIf { it != ' ' } ?: continue
                columnValue += c
            }
            if (columnValue == "") break
            operands.add(columnValue.toLong())
            digitIndex++
        }
        calculateTotal(operands, operations[col])
    }
}

private fun solve(fileName: String, part: Int) {
    println(if (part == 1) part1(fileName) else part2(fileName))
}

fun main() {
    for (part in 1..2) {
        solve("day6-sample1.txt", part)
        solve("day6.txt", part)
    }
}
