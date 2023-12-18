package com.behindmedia.adventofcode.year2019

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.leastCommonMultiple
import kotlin.math.abs

class Day12 {

    data class ComponentState(val positions: List<Long>, val velocities: List<Long>)

    private fun Coordinate3D.absSumOfComponents(): Long {
        return abs(x) + abs(y) + abs(z)
    }

    private fun applyGravity(coordinates: List<Coordinate3D>, velocities: MutableList<Coordinate3D>) {
        for (i in coordinates.indices) {
            for (j in i + 1 until coordinates.size) {
                val delta = LongArray(3)
                for (k in 0 until 3) {
                    val pos1 = coordinates[i][k]
                    val pos2 = coordinates[j][k]
                    delta[k] = when {
                        pos1 < pos2 -> 1L
                        pos1 > pos2 -> -1L
                        else -> 0L
                    }
                }
                velocities[i] = velocities[i].offset(delta[0], delta[1], delta[2])
                velocities[j] = velocities[j].offset(-delta[0], -delta[1], -delta[2])
            }
        }
    }

    private fun applyVelocity(coordinates: MutableList<Coordinate3D>, velocities: List<Coordinate3D>) {
        for (i in coordinates.indices) {
            coordinates[i] = coordinates[i].offset(velocities[i])
        }
    }

    /**
     * Gets the total energy after simulating from the supplied input state with the specified number of iterations
     */
    fun getTotalEnergy(initialCoordinates: List<Coordinate3D>,
                       initialVelocities: List<Coordinate3D>,
                       iterationCount: Int = 1000): Long {

        assert(initialCoordinates.size == initialVelocities.size)

        val coordinates = initialCoordinates.toMutableList()
        val velocities = initialVelocities.toMutableList()

        for (time in 0 until iterationCount) {
            applyGravity(coordinates, velocities)
            applyVelocity(coordinates, velocities)
        }

        var totalEnergy = 0L
        for (i in initialCoordinates.indices) {
            totalEnergy += coordinates[i].absSumOfComponents() * velocities[i].absSumOfComponents()
        }
        return totalEnergy
    }

    /**
     * Tries to find the period where the state for the specified component (x=0, y=1, z=2) for all moons matches
     * with a previous state. The minimum of this should always be the initialState assuming all moons have been in
     * a stable orbit already.
     *
     * Returns the number of iterations needed until this state repeats again.
     *
     * We can find the period per component(x,y,z) because they are completely independent.
     *
     * The full period would be the least common multiple of the three component periods.
     */
    fun findPeriod(initialCoordinates: List<Coordinate3D>,
                   initialVelocities: List<Coordinate3D>,
                   component: Int): Long {
        val coordinates = initialCoordinates.toMutableList()
        val velocities = initialVelocities.toMutableList()
        var initialState: ComponentState? = null
        var time = 0L

        while(true) {
            val state = ComponentState(coordinates.map { it[component] }, velocities.map { it[component] })
            if (initialState == null) {
                initialState = state
            } else if (state == initialState) {
                return time
            }
            applyGravity(coordinates, velocities)
            applyVelocity(coordinates, velocities)
            time++
        }
    }

    /**
     * Finds the total period for (x,y,z) together by determining the least common multiple of the
     * three component periods.
     */
    fun findPeriod(initialCoordinates: List<Coordinate3D>, initialVelocities: List<Coordinate3D>): Long {
        var result = 1L
        for (component in 0 until 3) {
            val period = findPeriod(initialCoordinates, initialVelocities, component)
            result = leastCommonMultiple(result, period)
        }
        return result
    }
}