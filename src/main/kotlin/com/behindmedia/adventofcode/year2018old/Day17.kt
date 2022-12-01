package com.behindmedia.adventofcode.year2018old

class Day17 {

    enum class Direction {
        down, left, right
    }

    data class Coordinate(val x: Int, val y: Int) {
        fun offset(xOffset: Int, yOffset: Int): Coordinate {
            return Coordinate(x + xOffset, y + yOffset)
        }
    }

    class Ground(val clayCoordinates: Set<Coordinate>) {

        val origin = Coordinate(500, 0)
        val minCoordinate: Coordinate
        val maxCoordinate: Coordinate
        val settled = mutableSetOf<Coordinate>()
        val flowing = mutableSetOf<Coordinate>()

        init {
            var minX = Integer.MAX_VALUE
            var minY = Integer.MAX_VALUE
            var maxX = Integer.MIN_VALUE
            var maxY = Integer.MIN_VALUE
            for (coordinate in clayCoordinates) {
                minX = Math.min(minX, coordinate.x)
                minY = Math.min(minY, coordinate.y)
                maxX = Math.max(maxX, coordinate.x)
                maxY = Math.max(maxY, coordinate.y)
            }
            minCoordinate = Coordinate(minX - 1, minY)
            maxCoordinate = Coordinate(maxX + 1, maxY)
        }

        private fun fill(point: Coordinate, direction: Direction = Direction.down): Boolean {

            if (clayCoordinates.contains(point)) {
                return true
            }

            if (flowing.contains(point)) {
                return false
            }

            flowing.add(point)

            val below = point.offset(0, 1)

            if (!clayCoordinates.contains(below) && !flowing.contains(below) && below.y <= maxCoordinate.y) {
                fill(below)
            }

            if (!clayCoordinates.contains(below) && !settled.contains(below)) {
                return false
            }

            var left = point.offset(-1, 0)
            var right = point.offset(1, 0)

            val leftFilled = fill(left, Direction.left)
            val rightFilled = fill(right, Direction.right)

            if (leftFilled && rightFilled) {
                settled.add(point)

                while (flowing.contains(left)) {
                    settled.add(left)
                    left = left.offset(-1, 0)
                }

                while (flowing.contains(right)) {
                    settled.add(right)
                    right = right.offset(1, 0)
                }
            }

            return direction == Direction.left && leftFilled ||
                    direction == Direction.right && rightFilled

        }

        fun flow(): Pair<Int, Int> {
            fill(origin)

            val predicate: (Coordinate) -> Boolean = { it.y >= minCoordinate.y && it.y <= maxCoordinate.y }
            return Pair(flowing.filter(predicate).size, settled.filter(predicate).size)
        }

        override fun toString(): String {
            val buffer = StringBuilder()
            for (y in origin.y..maxCoordinate.y) {
                for (x in minCoordinate.x..maxCoordinate.x) {
                    val coordinate = Coordinate(x, y)
                    if (coordinate == origin) {
                        buffer.append("+")
                    } else if (clayCoordinates.contains(coordinate)) {
                        buffer.append("#")
                    } else if (settled.contains(coordinate)) {
                        buffer.append("~")
                    } else if (flowing.contains(coordinate)) {
                        buffer.append("|")
                    } else {
                        buffer.append(".")
                    }
                }
                buffer.append("\n")
            }
            return buffer.toString()
        }

        companion object {

            private fun parseRange(s: String): Pair<Int, Int> {
                val components = s.split("..")
                if (components.size == 2) {
                    return Pair(components[0].toInt(), components[1].toInt())
                } else if (components.size == 1) {
                    return Pair(components[0].toInt(), components[0].toInt())
                } else {
                    throw IllegalArgumentException("Unparsable range: ${s}")
                }
            }

            fun fromString(s: String): Ground {
                val lines = s.split("\n")
                val clayCoordinates = mutableSetOf<Coordinate>()

                for (line in lines) {
                    val components = line.split(" ", ",").filter { !it.isEmpty() }
                    var minX: Int? = null
                    var maxX: Int? = null
                    var minY: Int? = null
                    var maxY: Int? = null
                    for (component in components) {
                        val subComponents = component.split("=")
                        if (subComponents.size != 2) throw IllegalArgumentException("Unparsable data")

                        when(subComponents[0]) {
                            "x" -> {
                                val range = parseRange(subComponents[1])
                                minX = range.first
                                maxX = range.second
                            }
                            "y" -> {
                                val range = parseRange(subComponents[1])
                                minY = range.first
                                maxY = range.second
                            }
                            else -> throw IllegalArgumentException("Unparsable data")
                        }
                    }

                    if (minX == null || minY == null || maxX == null || maxY == null) {
                        throw IllegalArgumentException("Unparsable data")
                    }

                    for (x in minX..maxX) {
                        for (y in minY..maxY) {
                            clayCoordinates.add(Coordinate(x, y))
                        }
                    }
                }

                return Ground(clayCoordinates)
            }
        }
    }
}