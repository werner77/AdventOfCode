package com.behindmedia.adventofcode.year2017.day20

import com.behindmedia.adventofcode.common.*
import kotlin.math.*

data class Particle(val coordinate: Coordinate3D, val speed: Coordinate3D, val accelaration: Coordinate3D)

// p=<-2149,5,-1355>, v=<-306,-3,-196>, a=<21,4,18>
fun main() {
    val data = parseLines("/2017/day20.txt") { line ->
        val c = line.splitNonEmptySequence(",", " ", "<", ">", "=", "p", "v", "a").map { it.toInt() }.toList()
        Particle(Coordinate3D(c[0], c[1], c[2]), Coordinate3D(c[3], c[4], c[5]), Coordinate3D(c[6], c[7], c[8]))
    }

    for (d in data) {

    }

    println(data)
}