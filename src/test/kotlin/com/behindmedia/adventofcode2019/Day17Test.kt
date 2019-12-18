package com.behindmedia.adventofcode2019

import org.junit.Test
import kotlin.test.assertEquals

class Day17Test {

    enum class State(val rawValue: Char) {
        Left('<'), Right('>'), Up('^'), Down('v'), Scaffold('#'),
        Empty('.'), Fallen('X');

        companion object {
            fun from(ascii: Char): State {
                return values().find { it.rawValue == ascii } ?: throw IllegalStateException("Illegal ascii supplied: $ascii")
            }
        }
    }

    data class Command(val rotationDirection: RotationDirection, val stride: Int) {
        override fun toString(): String {
            val direction = if (rotationDirection == RotationDirection.Right) "R" else "L"
            return direction + stride.toString()
        }
    }

    @Test
    fun puzzle1() {
        val day17 = Day17()
        val program = Computer.parseEncodedState(read("/day17.txt"))
        val result = day17.getNumberOfCrossings(program)
        println(result)
        assertEquals(7328, result)
    }

    fun getInitialMap(): Map<Coordinate, State> {
        val encoded = read("/day17.txt")
        val map = mutableMapOf<Coordinate, State>()
        val computer = Computer(encoded)
        var currentCoordinate = Coordinate.origin

        while (computer.status != Computer.Status.Finished) {

            val result = computer.process()

            for (output in result.outputs) {
                val ascii = output.toChar()
                if (ascii == '\n') {
                    currentCoordinate = Coordinate(0, currentCoordinate.y + 1)
                } else {
                    val state = State.from(ascii)
                    map[currentCoordinate] = state
                    currentCoordinate = currentCoordinate.offset(1, 0)
                }
            }
        }
        return map
    }

    fun String.toAsciiInput(): List<Long> {
        val ret = mutableListOf<Long>()
        for (c in this) {
            ret.add(c.toLong())
        }
        ret.add('\n'.toLong())
        return ret
    }

    @Test
    fun puzzle2() {
        val encoded = read("/day17.txt")
        val state = Computer.parseEncodedState(encoded).toMutableList()
        state[0] = 2
        val computer = Computer(state)

        var result = computer.process()

        println(result)

        result = computer.process("A,B,A,B,C,C,B,A,C,A".toAsciiInput())

        println(result)

        result = computer.process("L,10,R,8,R,6,R,10".toAsciiInput())

        println(result)

        result = computer.process("L,12,R,8,L,12".toAsciiInput())

        println(result)

        result = computer.process("L,10,R,8,R,8".toAsciiInput())

        println(result)

        result = computer.process("n".toAsciiInput())

        println(result)


    }

    @Test
    fun execute2() {
//        val encoded = read("/day17.txt")

//        val state = Computer.parseEncodedState(encoded).toMutableList()
//        state[0] = 2
//        val computer = Computer(state)
//
//        val mapStates = setOf<State>(State.Up, State.Down, State.Left, State.Right, State.Scaffold)
        val robotStates = setOf<State>(State.Up, State.Down, State.Left, State.Right)
        val map = getInitialMap()

        printMap(map)

        val robotEntry = map.entries.find { robotStates.contains(it.value) } ?: throw IllegalStateException("Robot not found")

        var currentDirection = when(robotEntry.value) {
            State.Up -> Coordinate(0, -1)
            State.Right -> Coordinate(1, 0)
            State.Down -> Coordinate(0, 1)
            State.Left ->  Coordinate(-1, 0)
            else -> throw IllegalStateException("Invalid robot entry")
        }

        var currentPosition = robotEntry.key
        var lastRotationDirection: RotationDirection? = null
        var currentStride = 0

        val commands = mutableListOf<Command>()

        val untouchedMap = map.toMutableMap()
        untouchedMap.remove(currentPosition)

        val iterator = untouchedMap.iterator()

        while(iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value != State.Scaffold) {
                iterator.remove()
            }
        }

        while (!untouchedMap.isEmpty()) {
            val populatedNeighbours = currentPosition.neighbours.filter {  map[it] == State.Scaffold }
            val straightPosition = currentPosition.offset(currentDirection)

            if (populatedNeighbours.isEmpty()) {
                break
            }

            if (populatedNeighbours.contains(straightPosition)) {
                currentStride++
                currentPosition = straightPosition
            } else {
                if (currentStride > 0) {
                    // Add last command
                    commands.add(Command(lastRotationDirection!!, currentStride))
                }

                val rightNeighbour = currentPosition.offset(currentDirection.rotate(RotationDirection.Right))
                val leftNeighbour = currentPosition.offset(currentDirection.rotate(RotationDirection.Left))

                if (populatedNeighbours.contains(rightNeighbour)) {
                    lastRotationDirection = RotationDirection.Right
                    currentPosition = rightNeighbour
                } else if (populatedNeighbours.contains(leftNeighbour)) {
                    lastRotationDirection = RotationDirection.Left
                    currentPosition = leftNeighbour
                } else {
                    throw IllegalStateException("No left or right neighbour found")
                }

                currentDirection = currentDirection.rotate(lastRotationDirection)
                currentStride = 1
            }

            untouchedMap.remove(currentPosition)
        }

        if (currentStride > 0) {
            // Add last command
            commands.add(Command(lastRotationDirection!!, currentStride))
        }

        println(commands)
    }

    fun printMap(map: Map<Coordinate, State>) {
        val range = map.keys.range()
        for (coordinate in range) {
            val state = map[coordinate]
            print(state!!.rawValue)

            if (coordinate.x == range.endInclusive.x) {
                println()
            }
        }
    }
}