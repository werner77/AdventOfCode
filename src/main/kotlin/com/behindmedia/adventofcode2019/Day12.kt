package com.behindmedia.adventofcode2019

class Day12 {

    class State(coords: List<Coordinate3D>, velos: List<Coordinate3D>) {

        val coordinates: List<Coordinate3D> =
            listOf(coords[0].minus(coords[0]), coords[1].minus(coords[0]), coords[2].minus(coords[0]), coords[3].minus(coords[0]))
        val velocities: List<Coordinate3D> = listOf(velos[0].copy(), velos[1].copy(), velos[2].copy(), velos[3].copy())

        override fun hashCode(): Int {
            var hash =  17 + 37 * this.velocities.hashCode()
            hash += 37 * this.coordinates.hashCode()
            return hash
        }

        override fun equals(other: Any?): Boolean {
            if (other is State) {
                return this.coordinates == other.coordinates && this.velocities == other.velocities
            }
            return false
        }
    }

    fun applyGravity(coordinates: MutableList<Coordinate3D>, velocities: MutableList<Coordinate3D>) {
        for (i in 0 until coordinates.size) {
            for (j in i + 1 until coordinates.size) {
                val moon1 = coordinates[i]
                val moon2 = coordinates[j]
                val velocity1 = velocities[i]
                val velocity2 = velocities[j]

                if (moon1.x < moon2.x) {
                    velocity1.x++
                    velocity2.x--
                } else if (moon1.x > moon2.x) {
                    velocity1.x--
                    velocity2.x++
                }

                if (moon1.y < moon2.y) {
                    velocity1.y++
                    velocity2.y--
                } else if (moon1.y > moon2.y) {
                    velocity1.y--
                    velocity2.y++
                }

                if (moon1.z < moon2.z) {
                    velocity1.z++
                    velocity2.z--
                } else if (moon1.z > moon2.z) {
                    velocity1.z--
                    velocity2.z++
                }
            }
        }
    }

    fun applyVelocity(coordinates: MutableList<Coordinate3D>, velocities: MutableList<Coordinate3D>) {
        for (i in coordinates.indices) {
            val coordinate = coordinates[i]
            val velocity = velocities[i]
            coordinate.offset(velocity)
        }
    }

    fun simulate(initialCoordinates: List<Coordinate3D>, initialVelocities: List<Coordinate3D>, endTime: Int = 1000): Int {

        assert(initialCoordinates.size == 4)
        assert(initialVelocities.size == 4)

        val coordinates = initialCoordinates.toMutableList()
        val velocities = initialVelocities.toMutableList()

        for (time in 0 until endTime) {
            applyGravity(coordinates, velocities)
            applyVelocity(coordinates, velocities)
        }

        var totalEnergy = 0
        for (i in 0 until 4) {
            totalEnergy += coordinates[i].sumOfComponents() * velocities[i].sumOfComponents()
        }
        return totalEnergy
    }

    fun findPeriod(initialCoordinates: List<Coordinate3D>, initialVelocities: List<Coordinate3D>): Int {
        val coordinates = initialCoordinates.toMutableList()
        val velocities = initialVelocities.toMutableList()

        val encounteredStates = mutableSetOf<State>()
        var i = 0

        while(true) {
            val state = State(coordinates, velocities)

            if (encounteredStates.contains(state)) {
                //Found duplicate relative state
                return i
            }

            encounteredStates.add(state)
            applyGravity(coordinates, velocities)
            applyVelocity(coordinates, velocities)
            i++
        }
    }
}