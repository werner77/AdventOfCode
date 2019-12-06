package com.behindmedia.adventofcode2019

import kotlin.math.min

class Day6 {

    data class Star(val name: String, val orbitedBy: MutableSet<Star> = mutableSetOf(), val orbiting: MutableSet<Star> = mutableSetOf()) {
        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other is Star) {
                return name == other.name
            }
            return false
        }

        override fun toString(): String {
            return name
        }
    }

    fun numberOfTotalOrbits(encoded: List<String>): Int {

        val allStars = mapFromEncodedInput(encoded)
        val centerOfMass = allStars["COM"]

        if (centerOfMass != null) {
            val counter = Reference(0)
            traverse(centerOfMass, 0, counter)
            return counter.value
        } else {
            throw IllegalStateException("No centerOfMassFound")
        }
    }

    fun numberOfTransfers(encoded: List<String>, source: String, target: String): Int {
        val allStars = mapFromEncodedInput(encoded)

        val sourceStar = allStars[source]
        val targetStar = allStars[target]

        if (sourceStar == null || targetStar == null) {
            throw IllegalArgumentException("Invalid argument supplied")
        }

        //Find the nearest common ancestor
        val sourcePath = completePath(sourceStar)
        val targetPath = completePath(targetStar)

        var commonAncestorCount = 0

        for(i in 0 until min(sourcePath.size, targetPath.size)) {
            val star1 = sourcePath[sourcePath.size - 1 - i]
            val star2 = targetPath[targetPath.size - 1 - i]
            if(star1 == star2) {
                commonAncestorCount++
            } else {
                break
            }
        }
        return sourcePath.size + targetPath.size - 2 * commonAncestorCount
    }

    private fun completePath(star: Star): List<Star> {
        val path = mutableListOf<Star>()
        var parent: Star? = star.orbiting.onlyOrNull()

        while(parent != null) {
            path.add(parent)
            parent = parent.orbiting.onlyOrNull()
        }
        return path
    }

    private fun traverse(star: Star, depth: Int, counter: Reference<Int>) {
        counter.value += depth
        for (child in star.orbitedBy) {
            traverse(child, depth + 1, counter)
        }
    }

    private fun mapFromEncodedInput(encoded: List<String>): Map<String, Star> {
        val allStars = mutableMapOf<String, Star>()

        for (line in encoded) {
            val components = line.split(")")
            assert(components.size == 2)

            val star1 = allStars.getOrPut(components[0]) {
                Star(components[0])
            }

            val star2 = allStars.getOrPut(components[1]) {
                Star(components[1])
            }

            star1.orbitedBy.update(star2)
            star2.orbiting.update(star1)
        }
        return allStars
    }
}