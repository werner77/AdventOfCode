package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.popFirst
import java.util.*

class Day25 {

    fun adventure(program: String, initialInput: String, verbose: Boolean): String {

        val initialLines = LinkedList(initialInput.split('\n'))
        val computer = Computer(program)
        var input: String? = null
        var result: Computer.Result

        while (true) {
            result = computer.processAscii(input)
            if (verbose) print(result.asciiOutput)

            if (computer.status == Computer.Status.Finished) {
                return result.asciiOutput
            }

            if (initialLines.isEmpty()) {
                input = readLine()
            } else {
                input = initialLines.popFirst()
            }
        }
    }
}
