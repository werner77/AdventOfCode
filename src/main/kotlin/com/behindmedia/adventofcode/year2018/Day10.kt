package com.behindmedia.adventofcode.year2018

class Day10 {

    data class Entry(val position: Position, val velocity: Position)

    val regex = Regex("position=<(.*)> velocity=<(.*)>")

    fun parseLine(line: String): Entry {
        val matchResult = regex.find(line)
        if (matchResult != null) {
            val first = matchResult.groups[1]?.value
            val second = matchResult.groups[2]?.value

            if (first != null && second != null) {
                return Entry(Position.fromString(first), Position.fromString(second))
            }
        }
        throw IllegalArgumentException("Could not parse string: ${line}")
    }

    data class Position(var x: Int, var y: Int) {
        companion object {
            fun fromString(s: String): Position {
                val values = s.split(" ", ",").filter { !it.isEmpty() }
                if (values.size != 2) {
                    throw IllegalArgumentException("Input string ${s} is not parsable")
                }
                return Position(values[0].toInt(), values[1].toInt())
            }
        }

        fun add(position: Position, multiplier: Int = 1) {
            x += multiplier * position.x
            y += multiplier * position.y
        }

        fun distance(position: Position): Double {
            val diffX = (position.x - x).toDouble()
            val diffY = (position.y - y).toDouble()
            return Math.sqrt(diffX * diffX + diffY * diffY)
        }
    }

    fun List<Entry>.extraPolate(steps: Int) {
        for (entry in this) {
            entry.position.add(entry.velocity, steps)
        }
    }

    fun entropy(entries: List<Entry>): Double {
        //calculate the avg minimum distance to the nearest neighbour
        var sum = 0.0
        for (i in 0 until entries.size) {
            val position1 = entries[i].position
            var minDistance = Double.MAX_VALUE
            for (j in i + 1 until entries.size) {
                val position2 = entries[j].position
                minDistance = Math.min(minDistance, position1.distance(position2))
            }
            sum += minDistance
        }
        return sum / entries.size
    }

    fun determineMessage(entries: List<Entry>) {
        var seconds = 0
        var lastEntropy = Double.MAX_VALUE
        while (true) {
            entries.extraPolate(1)
            val currentEntropy = entropy(entries)

            if (currentEntropy > lastEntropy) {
                //The last one was the message: print it
                entries.extraPolate(-1)
                print(entries.map { it.position })
                println("Waited for ${seconds} seconds")
                break
            }

            lastEntropy = currentEntropy
            seconds++
        }
    }

    fun print(positions: List<Position>) {
        val minPosition = Position(Int.MAX_VALUE, Int.MAX_VALUE)
        val maxPosition = Position(Int.MIN_VALUE, Int.MIN_VALUE)

        val positionSet = mutableSetOf<Position>()

        for (position in positions) {
            minPosition.x = Math.min(minPosition.x, position.x)
            minPosition.y = Math.min(minPosition.y, position.y)
            maxPosition.x = Math.max(maxPosition.x, position.x)
            maxPosition.y = Math.max(maxPosition.y, position.y)
            positionSet.add(position)
        }

        for (y in minPosition.y..maxPosition.y) {
            for (x in minPosition.x..maxPosition.x) {
                if (positionSet.contains(Position(x, y))) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }
}