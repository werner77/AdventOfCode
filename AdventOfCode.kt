#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}
#end
#parse("File Header.java")

import com.behindmedia.adventofcode.common.*

private fun <T> parseLines(lineParser: (String) -> T): List<T> {
    return parseLines("/${YEAR}/${NAME.toLowerCase()}.txt") {
        lineParser.invoke(it)
    }
}

private fun part1() {
    val lines = parseLines { it }
    println(lines)
}

private fun part2() {
    val lines = parseLines { it }
    println(lines)
}

fun main() {
    part1()
    part2()
}