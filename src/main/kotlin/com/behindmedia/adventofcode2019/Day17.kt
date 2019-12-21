package com.behindmedia.adventofcode2019

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

    fun breakUpCommandSequence(commands: List<Command>): MovementCommands {

        // TODO: Find three common patterns in the list with algorithm and break it up

        // Discovered these by hand instead, just by putting the command list in a text editor
        return MovementCommands("A,B,A,B,C,C,B,A,C,A",
            "L,10,R,8,R,6,R,10",
            "L,12,R,8,L,12",
            "L,10,R,8,R,8")
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