package com.behindmedia.adventofcode2019

class Day21 {

    fun List<String>.toAsciiInput(): List<Long> {
        val result = mutableListOf<Long>()
        for (s in this) {
            result.addAll(s.toAsciiInput())
        }
        return result
    }

    fun walk(program: String): Long {

        val computer = Computer(program)

        /*
        - Always jump if A == 0 (no ground immediately in front)
        - D has to be 1 to jump (ground 4 away)
        - if B == 0 or C == 0 and D == 1 then jump
         */

        val instructionText = """
        NOT A J
        NOT B T
        OR T J
        NOT C T
        OR T J
        AND D J
        WALK
        """.trimIndent()

        val instructions = instructionText.split('\n')

        var result = computer.process()

        printAsciiOutput(result.outputs)

        result = computer.process(instructions.toAsciiInput())

        printAsciiOutput(result.outputs)

        return 0L
    }

    fun run(program: String): Long {

        val computer = Computer(program)

        /*
        Additional checks:

        - H or E should either be 1
         */

        val instructionText = """
        NOT A J
        NOT B T
        OR T J
        NOT C T
        OR T J
        AND D J
        NOT H T
        NOT T T
        OR E T
        AND T J
        RUN
        """.trimIndent()

        val instructions = instructionText.split('\n')

        var result = computer.process()

        printAsciiOutput(result.outputs)

        result = computer.process(instructions.toAsciiInput())

        printAsciiOutput(result.outputs)

        return 0L
    }

    fun printAsciiOutput(outputs: List<Long>) {
        for (output in outputs) {
            if (output > Char.MAX_VALUE.toLong()) {
                print(output)
            } else {
                val c = output.toChar()
                print("$c")
            }
        }
        println()
    }
}