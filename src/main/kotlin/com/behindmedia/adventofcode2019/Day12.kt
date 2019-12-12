package com.behindmedia.adventofcode2019

class Day12 {

    data class ComponentState(val positions: List<Int>, val velocities: List<Int>) {
    }

    private fun applyGravity(coordinates: List<Coordinate3D>, velocities: MutableList<Coordinate3D>) {
        for (i in coordinates.indices) {
            for (j in i + 1 until coordinates.size) {
                val delta = IntArray(3)
                for (k in 0 until 3) {
                    val pos1 = coordinates[i][k]
                    val pos2 = coordinates[j][k]
                    delta[k] = when {
                        pos1 < pos2 -> 1
                        pos1 > pos2 -> -1
                        else -> 0
                    }
                }
                velocities[i] = velocities[i].offset(Coordinate3D(delta[0], delta[1], delta[2]))
                velocities[j] = velocities[j].offset(Coordinate3D(-delta[0], -delta[1], -delta[2]))
            }
        }
    }

    private fun applyVelocity(coordinates: MutableList<Coordinate3D>, velocities: List<Coordinate3D>) {
        for (i in coordinates.indices) {
            coordinates[i] = coordinates[i].offset(velocities[i])
        }
    }

    fun getTotalEnergy(initialCoordinates: List<Coordinate3D>, initialVelocities: List<Coordinate3D>, endTime: Int = 1000): Int {

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
            totalEnergy += coordinates[i].absSumOfComponents() * velocities[i].absSumOfComponents()
        }
        return totalEnergy
    }

    /**
     * Tries to find the period where the state for the specified component (x=0, y=1, z=2) for all moons matches
     * with a previous state.
     *
     * A tuple is returned where the first is the initial iterations needed to reach the repeated state and the second
     * is the number of iterations needed after this state until the state repeats again.
     *
     * We can find the period per component(x,y,z) because they are completely independent.
     *
     * The full period would be the least common multiple of the three component periods.
     */
    fun findPeriod(initialCoordinates: List<Coordinate3D>, initialVelocities: List<Coordinate3D>, component: Int): Pair<Int, Int> {
        val coordinates = initialCoordinates.toMutableList()
        val velocities = initialVelocities.toMutableList()
        val encounteredStates = mutableMapOf<ComponentState, Int>()
        var i = 0

        while(true) {
            val state = ComponentState(coordinates.map { it[component] }, velocities.map { it[component] })
            val existingIndex = encounteredStates[state]
            if (existingIndex != null) {
                return Pair(existingIndex, i - existingIndex)
            }
            encounteredStates[state] = i
            applyGravity(coordinates, velocities)
            applyVelocity(coordinates, velocities)
            i++
        }
    }
}