package com.behindmedia.adventofcode.year2018

import java.lang.Math.abs
import java.util.*

class Day15 {

    enum class CombatUnitType {
        goblin,
        elf;

        override fun toString(): String {
            return when(this) {
                goblin -> "G"
                elf -> "E"
            }
        }
    }

    data class CombatUnit(val type: CombatUnitType) {
        var attackPower = 3
        var hitPoints = 200

        fun attack(otherUnit: CombatUnit): Boolean {
            otherUnit.hitPoints -= attackPower
            return otherUnit.hitPoints <= 0
        }

        override fun toString(): String {
            return "${type}(${hitPoints})"
        }
    }

    data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {
        override fun compareTo(other: Coordinate): Int {
            var result = y.compareTo(other.y)
            if (result == 0) {
                result = x.compareTo(other.x)
            }
            return result
        }

        fun offset(offsetX: Int, offsetY: Int): Coordinate {
            return Coordinate(x + offsetX, y + offsetY)
        }

        fun adjacentCoordinates(): List<Coordinate> {
            return listOf(
                this.offset(0, 1), this.offset(0, -1),
                this.offset(1, 0), this.offset(-1, 0))
        }
    }

    data class Square(val coordinate: Coordinate): Comparable<Square> {
        var occupant: CombatUnit? = null
        val neighbors = TreeSet<Square>()

        fun addNeighbor(neighbor: Square) {
            neighbors.add(neighbor)
        }

        override fun compareTo(other: Square): Int {
            return coordinate.compareTo(other.coordinate)
        }

        fun minDistanceTo(target: Square): Int {
            return abs(coordinate.x - target.coordinate.x) + abs(coordinate.y - target.coordinate.y)
        }

        fun shortestPathTo(target: Square, maxDistance: Int): List<Square>? {

            val openSet: Queue<Pair<Square, Int>> = LinkedList<Pair<Square, Int>>()
            val closedSet = mutableSetOf<Square>()
            val navigationMap = mutableMapOf<Square, Square>()

            openSet.add(Pair(this, 0))

            while (!openSet.isEmpty()) {
                val pair = openSet.remove()
                val node = pair.first
                val level = pair.second

                if (level > maxDistance) {
                    return null
                }

                if (node == target) {
                    return constructPath(node, navigationMap)
                }

                for (neighbor in node.neighbors) {
                    if (closedSet.contains(neighbor) || neighbor.occupant != null) {
                        continue
                    }

                    // Save the first parent which leads to neighbor (others can be ignored, since they are lower in precedence)
                    if (navigationMap[neighbor] == null) {
                        navigationMap[neighbor] = node
                        openSet.add(Pair(neighbor, level + 1))
                    }
                }

                closedSet.add(node)
            }

            // No path found
            return null
        }

        private fun constructPath(target: Square, navigationMap: Map<Square, Square>): List<Square> {
            val path = LinkedList<Square>()
            var currentSquare: Square? = target
            while (currentSquare != null && currentSquare != this) {
                path.addFirst(currentSquare)
                currentSquare = navigationMap[currentSquare]
            }
            return path
        }
    }

    class Cave(squares: List<Square>, val maxX: Int, val maxY: Int) {
        private var squareMap = mutableMapOf<Coordinate, Square>()

        init {
            for (square in squares) {
                squareMap[square.coordinate] = square
            }

            // Initialize neighbors
            for (square in squares) {
                val squareCoordinate = square.coordinate
                for (neighborCoordinate in squareCoordinate.adjacentCoordinates()) {

                    //Potentially 4 neighbours for each square
                    squareMap[neighborCoordinate]?.let {
                        it.addNeighbor(square)
                        square.addNeighbor(it)
                    }
                }
            }
        }

        fun targetSquaresFor(occupant: CombatUnit, square: Square): List<Square> {
            return square.coordinate.adjacentCoordinates().mapNotNull {
                squareMap[it]?.takeIf { it.occupant == null || it.occupant == occupant }
            }
        }

        val orderedOccupiedSquares: List<Square>
            get() = squareMap.values.filter { it.occupant != null }.sorted()


        fun performTurn(): Boolean {
            val occupiedSquares = orderedOccupiedSquares

            for (occupiedSquare in occupiedSquares) {
                val occupant = occupiedSquare.occupant ?: continue

                val goblins = squareMap.values.filter { it.occupant?.type == CombatUnitType.goblin }
                val elves = squareMap.values.filter { it.occupant?.type == CombatUnitType.elf }

                val targets = if (occupant.type == CombatUnitType.elf) goblins else elves

                if (targets.isEmpty()) {
                    return false
                }

                val targetSquares = targets.flatMap { targetSquaresFor(occupant, it) }.sortedWith(kotlin.Comparator { square1, square2 ->
                    square1.minDistanceTo(occupiedSquare).compareTo(square2.minDistanceTo(occupiedSquare))
                })

                var maxDistance = Int.MAX_VALUE
                val squareCandidates = TreeSet<Square>()
                for (targetSquare in targetSquares) {
                    occupiedSquare.shortestPathTo(targetSquare, maxDistance)?.let {
                        assert(it.size <= maxDistance)
                        if (it.size < maxDistance) {
                            maxDistance = it.size
                            squareCandidates.clear()
                        }
                        it.firstOrNull()?.let { firstSquare ->
                            squareCandidates.add(firstSquare)
                        }
                    }
                }
                val chosenTargetSquare = squareCandidates.firstOrNull() ?: occupiedSquare
                occupiedSquare.occupant = null
                chosenTargetSquare.occupant = occupant

                //Now check whether the unit can attack
                val targetUnitSquare = chosenTargetSquare.neighbors.filter { it.occupant != null && it.occupant!!.type != occupant.type }.sortedWith(kotlin.Comparator { square1, square2 ->
                    var result = square1.occupant!!.hitPoints.compareTo(square2.occupant!!.hitPoints)
                    if (result == 0) {
                        result = square1.compareTo(square2)
                    }
                    result
                }).firstOrNull()
                if (targetUnitSquare != null) {
                    if (occupant.attack(targetUnitSquare.occupant!!)) {
                        //Target Unit died: Remove it from the map
                        targetUnitSquare.occupant = null
                    }
                }
            }
            return true
        }

        fun copy(): Cave {
            val copy  = Cave(emptyList(), maxX, maxY)
            copy.squareMap = squareMap.toMutableMap()
            return copy
        }

        fun processCombat(elveAttackPower: Int = 3): Int {
            var turns = 0
            orderedOccupiedSquares.filter { it.occupant?.type == CombatUnitType.elf }.forEach { it.occupant!!.attackPower = elveAttackPower }
            while (true) {
                if (!performTurn()) {
                    break
                }
                turns++
            }
            val totalHitPoints = orderedOccupiedSquares.map { it.occupant!!.hitPoints }.reduce { value1, value2 -> value1 + value2 }
            return totalHitPoints * turns
        }

        fun numberOf(type: CombatUnitType): Int {
            return squareMap.values.filter { it.occupant?.type == type }.size
        }

        fun findMinElfPower(): Pair<Int, Int> {
            var lastFailedValue = 3
            var lastSuccessValue = 0
            var currentValue = lastFailedValue * 2
            val originalNumberOfElves = numberOf(CombatUnitType.elf)

            while(true) {
                val caveCopy = copy()
                val score = caveCopy.processCombat(currentValue)
                val numberOfElvesRemaining = numberOf(CombatUnitType.elf)
                val success = numberOfElvesRemaining == originalNumberOfElves

                if (success) {
                    if (currentValue == lastFailedValue + 1) {
                        //Done
                        return Pair(currentValue, score)
                    } else {
                        lastSuccessValue = currentValue
                        currentValue = lastFailedValue + (currentValue - lastFailedValue) / 2
                    }
                } else {
                    if (currentValue == lastSuccessValue - 1) {
                        return Pair(currentValue, score)
                    }
                    //Increase by factor 2
                    lastFailedValue = currentValue
                    currentValue *= 2
                }
            }
        }

        override fun toString(): String {
            return asString(false)
        }

        fun asString(includeUnits: Boolean): String {
            val buffer = StringBuilder()
            for (y in 0 until maxY) {
                val units = mutableListOf<CombatUnit>()
                for (x in 0 until maxX) {
                    val coordinate = Coordinate(x, y)
                    val square = squareMap[coordinate]
                    if (square == null) {
                        buffer.append("#")
                    } else {
                        val occupant = square.occupant
                        if (occupant == null) {
                            buffer.append(".")
                        } else {
                            buffer.append(occupant.type.toString())
                            units.add(occupant)
                        }
                    }
                }
                if (!units.isEmpty() && includeUnits) {
                    buffer.append("   ")
                    var first = true
                    for (unit in units) {
                        if (first) {
                            first = false
                        } else {
                            buffer.append(", ")
                        }
                        buffer.append(unit.toString())
                    }
                }

                buffer.append("\n")
            }
            return buffer.toString()
        }
    }

    fun parse(input: String): Cave {
        val lines = input.split("\n")
        val squares = mutableListOf<Square>()
        var y = 0
        var x = 0
        for (line in lines) {
            val trimmedLine = line.trim()
            if (!trimmedLine.isEmpty()) {
                x = 0
                for (c in trimmedLine.toCharArray()) {
                    if (c != '#') {
                        val square = Square(Coordinate(x, y))
                        square.occupant = when(c) {
                            'G' -> CombatUnit(CombatUnitType.goblin)
                            'E' -> CombatUnit(CombatUnitType.elf)
                            else -> null
                        }
                        squares.add(square)
                    }
                    x++
                }
                y++
            }
        }
        return Cave(squares, x, y)
    }

}