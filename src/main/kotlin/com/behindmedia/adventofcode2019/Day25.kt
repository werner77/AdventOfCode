package com.behindmedia.adventofcode2019

import java.util.*

class Day25 {

    fun List<Long>.toAscii(): String {
        val buffer = StringBuilder()
        for (l in this) {
            val c = l.toChar()
            buffer.append(c)
        }
        buffer.append('\n')
        return buffer.toString()
    }

    fun adventure(program: String, input: String, verbose: Boolean): String {

        val initialLines = LinkedList(input.split('\n'))
        val computer = Computer(program)
        var input: String? = null
        var result: Computer.Result

        while (true) {
            result = computer.process(input?.toAsciiInput() ?: emptyList())
            if (verbose) print(result.outputs.toAscii())

            if (computer.status == Computer.Status.Finished) {
                return result.outputs.toAscii()
            }

            if (initialLines.isEmpty()) {
                input = readLine()
            } else {
                input = initialLines.popFirst()
            }
        }
    }
}
