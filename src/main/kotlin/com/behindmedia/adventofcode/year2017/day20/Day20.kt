package com.behindmedia.adventofcode.year2017.day20

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

class Particle(val id: Int, var coordinate: Coordinate3D, var speed: Coordinate3D, var accelaration: Coordinate3D) {
    fun simulate() {
        speed += accelaration
        coordinate += speed
    }
}

private fun simulate(iterationCount: Int, particles: List<Particle>): Int {
    val destroyed = mutableSetOf<Int>()
    for (i in 0 until iterationCount) {
        val seen = mutableMapOf<Coordinate3D, Int>()
        for (p in particles) {
            if (destroyed.contains(p.id)) continue
            p.simulate()
            val otherParticle = seen[p.coordinate]
            if (otherParticle != null) {
                // Remove both particles
                destroyed += otherParticle
                destroyed += p.id
            } else {
                seen[p.coordinate] = p.id
            }
        }
    }
    return particles.size - destroyed.size
}

// p=<-2149,5,-1355>, v=<-306,-3,-196>, a=<21,4,18>
fun main() {
    var id = 0
    val data = parseLines("/2017/day20.txt") { line ->
        val c = line.splitNonEmptySequence(",", " ", "<", ">", "=", "p", "v", "a").map { it.toInt() }.toList()
        Particle(id++, Coordinate3D(c[0], c[1], c[2]), Coordinate3D(c[3], c[4], c[5]), Coordinate3D(c[6], c[7], c[8]))
    }

    val min = data.minByOrNull { it.accelaration.manhattenDistance(Coordinate3D.origin) } ?: error("No min found")
    println(min.id)
    println(simulate(100_000, data))
}