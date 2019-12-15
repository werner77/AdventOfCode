package com.behindmedia.adventofcode2019

import java.lang.StringBuilder
import kotlin.math.min

class Day15 {

    class CoordinateNode(val coordinate: Coordinate, var distance: Int)
    class VisitState(val state: State, val visitCount: Int)

    enum class State(val description: String, val relevance: Int) {
        Oxygen("O", 5), Wall("#", 4), Free(".", 3),
        Destination("X", 1), Unknown(" ", 2);
    }

    enum class Command(val rawValue: Long, val coordinate: Coordinate) {
        Up(1, Coordinate(0, -1)), Down(2, Coordinate(0, 1)),
        Left(3, Coordinate(-1, 0)), Right(4, Coordinate(1, 0));

        companion object {
            fun from(rawValue: Long): Command {
                return values().find { it.rawValue == rawValue } ?: throw IllegalArgumentException("Invalid command value: $rawValue")
            }

            fun from(direction: Coordinate): Command {
                return values().find { it.coordinate == direction } ?: throw IllegalArgumentException("Invalid direction: $direction")
            }
        }
    }

    fun fewestNumberOfMoves(input: String): Int {
        val map = discoverMap(input)

        println("Found map:")
        println(map.description)

        val destination = map.entries.find { it.value == State.Destination }?.key ?: throw IllegalStateException("Destination not found")
        return map.shortestPath(Coordinate.origin, destination) ?: throw IllegalStateException("No path found from source to destination")
    }

    fun spreadOxygen(input: String): Int {
        val map = discoverMap(input)

        println("Initial map:")
        println(map.description)

        val entry = map.entries.find { it.value == State.Destination } ?: throw IllegalStateException("Destination not found")
        val destination = entry.key

        //Populate the map until there are no free states left
        val populatedMap = map.toMutableMap()
        populatedMap[destination] = State.Oxygen
        var minutes = 0
        while(true) {
            val oxygenEntries = populatedMap.entries.filter {
                it.value == State.Oxygen
            }

            var populatedCount = 0
            for (oxygenEntry in oxygenEntries) {
                for (neighbour in oxygenEntry.key.neighbours) {
                    val neighbourState = populatedMap[neighbour]
                    if (neighbourState == State.Free) {
                        populatedMap[neighbour] = State.Oxygen
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
        println(populatedMap.description)

        return minutes
    }

    /**
     * Runs the robot program until the whole map is discovered
     */
    private fun discoverMap(program: String): Map<Coordinate, State> {
        val computer = Computer(program)
        var command = Command.Right
        val map = mutableMapOf<Coordinate, VisitState>()
        var currentPosition = Coordinate.origin

        map[currentPosition] = VisitState(State.Free, 1)

        while (computer.status != Computer.Status.Finished) {

            val currentResult = computer.process(command.rawValue)
            currentPosition = currentPosition.offset(command.coordinate)

            when (currentResult.lastOutput) {
                0L -> {
                    map[currentPosition] = VisitState(State.Wall, 1)

                    // Revert the position, we cannot go into walls
                    currentPosition = currentPosition.offset(command.coordinate.inverted())
                }
                1L -> {
                    val currentState = map[currentPosition] ?: VisitState(State.Unknown, 0)
                    map[currentPosition] = VisitState(State.Free, currentState.visitCount + 1)
                }
                2L -> {
                    map[currentPosition] = VisitState(State.Destination, 1)
                }
                else -> throw IllegalStateException("Unexpected output: ${currentResult.lastOutput}")
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

    /**
     * Dijkstra's algorithm to find the shortest path
     */
    private fun Map<Coordinate, State>.shortestPath(start: Coordinate, destination: Coordinate): Int? {
        val unvisitedNodes = this.entries.fold(mutableMapOf<Coordinate, CoordinateNode>()) { map, entry ->
            if (entry.value != State.Wall) {
                val distance = if (entry.key == start) 0 else Int.MAX_VALUE
                map[entry.key] = CoordinateNode(entry.key, distance)
            }
            map
        }

        var currentNode: CoordinateNode

        while (true) {

            currentNode = unvisitedNodes.values.minBy { it.distance } ?: break
            if (currentNode.distance == Int.MAX_VALUE) {
                break
            } else if (currentNode.coordinate == destination) {
                return currentNode.distance
            }

            unvisitedNodes.remove(currentNode.coordinate)

            for (neighbourCoordinate in currentNode.coordinate.neighbours) {
                val node = unvisitedNodes[neighbourCoordinate] ?: continue
                node.distance = min(node.distance, currentNode.distance + 1)
            }
        }
        return null
    }

    private fun Map<Coordinate, VisitState>.isFull(): Boolean {
        val unexploredEntry = this.entries.find { entry ->
            entry.value.state != State.Wall && entry.key.neighbours.any { this[it] == null }
        }
        return unexploredEntry == null
    }

    private fun Map<Coordinate, VisitState>.bestNextDirection(currentPosition: Coordinate): Coordinate? {
        // Get all the neighbours of the current position as pairs of (direction, visitState)
        val candidates: List<Pair<Coordinate, VisitState>> = currentPosition.neighbours.mapNotNull { coordinate ->
            val visitState = this[coordinate] ?: VisitState(State.Unknown, 0)
            if (visitState.state == State.Wall) null else Pair(currentPosition.vector(coordinate), visitState)
        }
        return candidates.minBy { it.second.visitCount }?.first
    }

    private val Map<Coordinate, State>.description: String
        get() {
            val range = this.keys.range()
            val buffer = StringBuilder()
            for (coordinate in range) {
                val s = when (coordinate) {
                    Coordinate.origin -> "S"
                    else -> (this[coordinate] ?: State.Wall).description
                }
                buffer.append(s)
                if (coordinate.x == range.endInclusive.x) {
                    buffer.append("\n")
                }
            }
            return buffer.toString()
        }
}
