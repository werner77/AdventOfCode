package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.range
import com.behindmedia.adventofcode.common.reachableCoordinates

class Day15 {

    class CoordinateNode(val coordinate: Coordinate, var distance: Int)
    class VisitState(val state: State, val visitCount: Int)

    enum class State(val description: String, val relevance: Int, val isFree: Boolean = true) {
        Oxygen("O", 5), Wall("#", 4, false), Free(".", 3),
        Destination("X", 2), Unknown(" ", 1);
    }

    enum class Command(val value: Long, val coordinate: Coordinate) {
        Up(1, Coordinate(0, -1)), Down(2, Coordinate(0, 1)),
        Left(3, Coordinate(-1, 0)), Right(4, Coordinate(1, 0));

        companion object {
            fun from(direction: Coordinate): Command {
                return entries.find { it.coordinate == direction } ?:
                    throw IllegalArgumentException("Invalid direction: $direction")
            }
        }
    }

    fun fewestNumberOfMoves(input: String, onUpdate: ((Map<Coordinate, State>) -> Unit)? = null): Int {
        val map = discoverMap(input, onUpdate)

        println("Found map:")
        println(descriptionFor(map))

        val destination: Coordinate = map.entries.find { it.value == State.Destination }?.key ?:
            throw IllegalStateException("Destination not found")

        return map.shortestPath(Coordinate.origin, destination) ?:
            throw IllegalStateException("No path found from source to destination")
    }

    fun spreadOxygen(input: String): Int {
        val map = discoverMap(input)

        println("Initial map:")
        println(descriptionFor(map))

        val entry = map.entries.find { it.value == State.Destination } ?: throw IllegalStateException("Destination not found")
        val destination = entry.key

        //Populate the map until there are no free states left
        val populatedMap = map.toMutableMap()
        populatedMap[destination] = State.Oxygen

        val oxygenCoordinates = mutableListOf<Coordinate>()
        oxygenCoordinates.add(destination)

        var minutes = 0
        while(true) {
            var populatedCount = 0
            val iterator = oxygenCoordinates.listIterator()

            iterator.forEach { oxygenCoordinate ->
                for (neighbour in oxygenCoordinate.directNeighbours) {
                    val neighbourState = populatedMap[neighbour]
                    if (neighbourState == State.Free) {
                        populatedMap[neighbour] = State.Oxygen
                        iterator.add(neighbour)
                        populatedCount++
                    }
                }
            }

            if(populatedCount == 0) {
                break
            }

            minutes++
        }

        println("Final map:")
        println(descriptionFor(populatedMap))

        return minutes
    }

    /**
     * Runs the robot program until the whole map is discovered
     */
    private fun discoverMap(program: String, onUpdate: ((Map<Coordinate, State>) -> Unit)? = null): Map<Coordinate, State> {
        val computer = Computer(program)
        var command = Command.Up
        val map = mutableMapOf<Coordinate, VisitState>()
        var currentPosition = Coordinate.origin

        map[currentPosition] = VisitState(State.Free, 1)

        while (computer.status != Computer.Status.Finished) {

            val currentResult = computer.process(command.value)
            currentPosition = currentPosition.offset(command.coordinate)

            val currentState = map[currentPosition] ?: VisitState(State.Unknown, 0)

            when (currentResult.lastOutput) {
                0L -> {
                    map[currentPosition] = VisitState(State.Wall, currentState.visitCount + 1)

                    // Revert the position, we cannot go into walls
                    currentPosition = currentPosition.offset(command.coordinate.inverted())
                }
                1L -> {
                    map[currentPosition] = VisitState(State.Free, currentState.visitCount + 1)
                }
                2L -> {
                    map[currentPosition] = VisitState(State.Destination, currentState.visitCount + 1)
                }
                else -> throw IllegalStateException("Unexpected output: ${currentResult.lastOutput}")
            }

            if (onUpdate != null) {
                onUpdate(map.mapValues { it.value.state })
            }

            if (map.isFull()) {
                // Exit if we have nowhere to go anymore
                break
            }

            val nextDirection = map.bestNextDirection(currentPosition) ?: throw IllegalStateException("Stuck")
            command = Command.from(nextDirection)
        }

        return map.mapValues { it.value.state }
    }

    private fun Map<Coordinate, State>.shortestPath(start: Coordinate, destination: Coordinate): Int? {
        return start.reachableCoordinates(
            reachable = { coordinate -> this[coordinate]?.isFree ?: false },
            process = { coordinatePath -> if (coordinatePath.coordinate == destination) coordinatePath.pathLength else null }
        )
    }

    private fun Map<Coordinate, VisitState>.isFull(): Boolean {
        return !this.entries.any { entry ->
            entry.value.state != State.Wall && entry.key.directNeighbours.any { this[it] == null }
        }
    }

    private fun Map<Coordinate, VisitState>.bestNextDirection(currentPosition: Coordinate): Coordinate? {
        // Get all the neighbours of the current position as pairs of (direction, visitState)
        val candidates: List<Pair<Coordinate, VisitState>> = currentPosition.directNeighbours.mapNotNull { coordinate ->
            val visitState = this[coordinate] ?: VisitState(State.Unknown, 0)
            if (visitState.state == State.Wall) null else Pair(currentPosition.vector(coordinate), visitState)
        }
        return candidates.minByOrNull { it.second.visitCount }?.first
    }

    fun descriptionFor(map: Map<Coordinate, State>): String {
        val range = map.keys.range()
        val buffer = StringBuilder()
        for (coordinate in range) {
            val s = when (coordinate) {
                Coordinate.origin -> "S"
                else -> (map[coordinate] ?: State.Unknown).description
            }
            buffer.append(s)
            if (coordinate.x == range.endInclusive.x) {
                buffer.append("\n")
            }
        }
        return buffer.toString()
    }
}
