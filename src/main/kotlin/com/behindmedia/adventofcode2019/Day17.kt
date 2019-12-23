package com.behindmedia.adventofcode2019

import kotlin.math.max
import kotlin.math.min

class Day17 {

    enum class State(val rawValue: Char, val occupied: Boolean, val direction: Coordinate? = null) {
        Left('<', true, Coordinate(-1, 0)),
        Right('>',true, Coordinate(1, 0)),
        Up('^', true, Coordinate(0, -1)),
        Down('v', true, Coordinate(0, 1)),
        Scaffold('#', true),
        Empty('.', false),
        Fallen('X', false);

        companion object {
            fun from(ascii: Char): State {
                return values().find { it.rawValue == ascii } ?: throw IllegalStateException("Illegal ascii supplied: $ascii")
            }

            val robotStates = setOf(Up, Down, Left, Right)
        }

        override fun toString(): String {
            return rawValue.toString()
        }
    }

    data class Command(val rotationDirection: RotationDirection?, val stride: Int) {
        override fun toString(): String {

            if (rotationDirection == null) {
                return stride.toString()
            }

            val directionString = when(rotationDirection) {
                RotationDirection.Right -> "R"
                RotationDirection.Left -> "L"
            }
            return "$directionString,$stride"
        }
    }

    data class MovementCommands(val functionSequence: String, val functionA: String, val functionB: String, val functionC: String) {

        fun toInputList(): List<Long> {

            val result = mutableListOf<Long>()

            result.addAll(functionSequence.toAsciiInput())
            result.addAll(functionA.toAsciiInput())
            result.addAll(functionB.toAsciiInput())
            result.addAll(functionC.toAsciiInput())

            return result
        }
    }

    fun getMap(program: List<Long>, onlyOccupied: Boolean = false): Map<Coordinate, State> {
        val map = mutableMapOf<Coordinate, State>()
        val computer = Computer(program)
        var currentCoordinate = Coordinate.origin

        while (computer.status != Computer.Status.Finished) {
            val result = computer.process()
            for (output in result.outputs) {
                val ascii = output.toChar()
                if (ascii == '\n') {
                    currentCoordinate = Coordinate(0, currentCoordinate.y + 1)
                } else {
                    val state = State.from(ascii)
                    if (state.occupied || !onlyOccupied) {
                        map[currentCoordinate] = state
                    }
                    currentCoordinate = currentCoordinate.offset(1, 0)
                }
            }
        }
        return map
    }

    fun getNumberOfCrossings(program: List<Long>): Int {
        val map = getMap(program, true)

        // Find the points in the map with have adjacent points
        var answer = 0
        for (coordinate in map.keys) {
            val numberOfNeighbours = coordinate.neighbours.filter { map[it] != null }.size
            if (numberOfNeighbours == 4) {
                answer += coordinate.x * coordinate.y
            }
        }
        return answer
    }

    fun <E>List<E>.removingAllOccurences(sublist: List<E>): List<E> {
        val first = sublist.firstOrNull() ?: return this
        val result = mutableListOf<E>()
        var i = 0
        while(i < this.size) {
            val e = this[i]
            if (e == first) {
                // Check whether there is a complete match
                var foundMatch = true
                for (j in 1 until sublist.size) {
                    if (i + j >= this.size || this[i + j] != sublist[j]) {
                        foundMatch = false
                        break
                    }
                }
                if (foundMatch) {
                    i += sublist.size
                    continue
                }
            }
            result.add(e)
            i++
        }
        return result
    }

    fun List<String>.totalSize(): Int {
        var totalSize = 0
        var first = true
        for (s in this) {
            totalSize += s.length
            if (first) {
                first = false
            } else {
                totalSize++
            }
        }
        return totalSize
    }

    fun breakupList(list: List<String>): List<List<String>>? {
        fun findElements(list: List<String>, totalSize: Int, elementCount: Int, outputList: MutableList<List<String>>): Boolean {

            if (elementCount == 3) {
                return list.isEmpty()
            } else if (elementCount > 3) {
                return false
            } else if (totalSize <= 20) {
                return true
            }

            var prefix = list.subList(0, min(10, list.size))
            var totalPrefixSize = prefix.totalSize()

            while (!prefix.isEmpty()) {
                if (totalPrefixSize <= 20) {
                    val nextList = list.removingAllOccurences(prefix)

                    val numberOfReplacements = (list.size - nextList.size) / prefix.size
                    assert((list.size - nextList.size) % prefix.size == 0)
                    assert(numberOfReplacements >= 1)

                    val nextTotalSize = totalSize - numberOfReplacements * totalPrefixSize
                    if (findElements(nextList, nextTotalSize, elementCount + 1, outputList)) {
                        outputList.add(prefix)
                        return true
                    }
                }

                // Remove last element
                totalPrefixSize -= (prefix.last().length + 1)
                prefix = list.subList(0, prefix.size - 1)
            }
            return false
        }

        val resultList = mutableListOf<List<String>>()
        val foundResult = findElements(list, list.totalSize(),0, resultList)
        return if (foundResult) resultList else null
    }



    fun breakUpCommandSequence(commands: List<Command>): MovementCommands {

        val completeCommandString = commands.joinToString(",")
        val completeList = completeCommandString.split(",")

        val resultList = breakupList(completeList) ?: throw IllegalStateException("Could not break up list in patterns")
        assert(resultList.size <= 3)

        val A = resultList.elementAtOrNull(0)?.joinToString(",") ?: ""
        val B = resultList.elementAtOrNull(1)?.joinToString(",") ?: ""
        val C = resultList.elementAtOrNull(2)?.joinToString(",") ?: ""

        var functionSequence = completeCommandString.replace(A, "A")
        functionSequence = functionSequence.replace(B, "B")
        functionSequence = functionSequence.replace(C, "C")

        return MovementCommands(functionSequence,
            A,
            B,
            C)
    }

    fun getNumberOfDustParticles(program: List<Long>): Long {
        val map = getMap(program, true)
        val commandSequence = findCommandSequence(map)
        val movementCommands = breakUpCommandSequence(commandSequence)

        val mutatedProgram = program.toMutableList()
        mutatedProgram[0] = 2

        val computer = Computer(mutatedProgram)

        computer.process(movementCommands.toInputList())

        val result = computer.process("n".toAsciiInput())
        return result.lastOutput
    }

    fun findCommandSequence(map: Map<Coordinate, State>): List<Command> {
        val robotEntry = map.entries.find { State.robotStates.contains(it.value) } ?:
            throw IllegalStateException("Robot not found")

        var currentDirection = robotEntry.value.direction ?:
            throw IllegalStateException("Expected robot to have an initial direction")

        var currentPosition = robotEntry.key

        var lastRotationDirection: RotationDirection? = null
        var currentStride = 0

        val commands = mutableListOf<Command>()

        val untouchedMap = map.toMutableMap()
        untouchedMap.remove(currentPosition)

        while (untouchedMap.isNotEmpty()) {

            val populatedNeighbours = currentPosition.neighbours.filter {
                map[it] == State.Scaffold
            }.toSet()

            var foundNextPosition = false

            // Get the most relevant neighbour, prefer going straight, then right, then left
            for (rotation in listOf(null, RotationDirection.Right, RotationDirection.Left)) {
                val rotatedDirection = currentDirection.optionalRotate(rotation)
                val nextPosition = currentPosition.offset(rotatedDirection)
                if (populatedNeighbours.contains(nextPosition)) {
                    if (rotation == null) {
                        currentStride++
                    } else {
                        if (currentStride > 0) {
                            // Add last command
                            commands.add(Command(lastRotationDirection, currentStride))
                        }
                        lastRotationDirection = rotation
                        currentStride = 1
                    }
                    foundNextPosition = true
                    currentPosition = nextPosition
                    currentDirection = rotatedDirection
                    break
                }
            }

            if (!foundNextPosition) {
                printMap(map)
                throw IllegalStateException("Found no free direction while the map has not been fully navigated")
            }

            untouchedMap.remove(currentPosition)
        }

        if (currentStride > 0) {
            // Add last command
            commands.add(Command(lastRotationDirection, currentStride))
        }

        return commands
    }

    fun printMap(map: Map<Coordinate, State>) {
        val range = map.keys.range()
        for (coordinate in range) {
            val state = map[coordinate] ?: State.Empty
            print(state.rawValue)

            if (coordinate.x == range.endInclusive.x) {
                println()
            }
        }
    }

}