package com.behindmedia.adventofcode.year2023.day24

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.LongCoordinate
import com.behindmedia.adventofcode.common.SafeLong
import com.behindmedia.adventofcode.common.parseLines
import com.behindmedia.adventofcode.common.safe
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong
import java.math.BigInteger

fun main() {
    val stones = parseLines("/2023/day24.txt") { line ->
        val (first, second) = line.split("@")
        val (x, y, z) = first.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        val (vx, vy, vz) = second.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        Stone(Coordinate3D(x, y, z), Coordinate3D(vx, vy, vz))
    }

    // Part 1
    val range = 200000000000000L.toDouble()..400000000000000L.toDouble()
    var ans = 0L
    for (i in 0 until stones.size) {
        for (j in i + 1 until stones.size) {
            val (x, y) = solve(stones[i], stones[j]) ?: continue
            if (x !in range || y !in range) continue
            if (listOf(stones[i], stones[j]).any {(y - it.position.y) / it.velocity.y < 0.0 }) continue
            ans++
        }
    }
    println(ans)

    // Part 2, credits to Roman Elizarov
    val x = checkTimes(stones.map { it.position.x }, stones.map { it.velocity.x })
    val y = checkTimes(stones.map { it.position.y }, stones.map { it.velocity.y })
    val z = checkTimes(stones.map { it.position.z }, stones.map { it.velocity.z })
    println(x + y + z)
}

private fun solve(first: Stone, second: Stone): Pair<Double, Double>? {
    return solve(first.a, first.b, first.c, second.a, second.b, second.c)
}

private fun solve(a1: BigInteger, b1: BigInteger, c1: BigInteger, a2: BigInteger, b2: BigInteger, c2: BigInteger): Pair<Double, Double>? {
    val d = b2 * a1 - b1 * a2
    if (d == BigInteger.ZERO) return null
    val x = (b2 * c1 - b1 * c2).toDouble() / d.toDouble()
    val y = (c2 * a1 - c1 * a2).toDouble() / d.toDouble()
    return x to y
}

private data class Stone(val position: Coordinate3D, val velocity: Coordinate3D) {
    val a: BigInteger = velocity.y.toBigInteger()
    val b: BigInteger = -velocity.x.toBigInteger()
    val c: BigInteger = (velocity.y.toBigInteger() * position.x.toBigInteger() - velocity.x.toBigInteger() * position.y.toBigInteger())

    fun addingVelocity(delta: Coordinate3D): Stone {
        return copy(velocity = velocity + delta)
    }

    override fun toString(): String = "$position @ $velocity"
}

private data class Range(val pi: LongRange, val velocity: Long)

private fun checkTimes(ps: List<Long>, vs: List<Long>): Long {
    val n = ps.size
    check(vs.size == n)

    val minV = -1000L
    val maxV = 1000L
    val minP = 0L
    val maxP = 1_000_000_000_000_000L
    check(minV < vs.min() && maxV > vs.max())
    check(minP < ps.min() && maxP > ps.max())
    val vss = vs.zip(ps).groupBy { it.first }.mapValues { e -> e.value.map { it.second }.toSet() }
    val rs = ArrayList<Range>()

    vloop@ for (v in minV..maxV) {
        val p1 = vs.withIndex().filter { v < it.value }.maxOfOrNull { ps[it.index] } ?: minP
        val p2 = vs.withIndex().filter { v > it.value }.minOfOrNull { ps[it.index] } ?: maxP
        if (p1 > p2) continue
        var pmod = BigInteger.ONE
        var prem = BigInteger.ZERO
        var p1r = p1
        var p2r = p2
        for (i in 0..<n) {
            val pi = ps[i]
            val vi = vs[i]
            if (v == vi) {
                val p0 = vss[v]?.singleOrNull() ?: continue@vloop
                if (p0 !in p1r..p2r) continue@vloop
                p1r = p0
                p2r = p0
                continue
            }
            // t_meet = (p - pi) / (vi - v)
            val d = abs(vi - v).toBigInteger()
            val r = pi.mod(d)
            val pmod2 = lcm(pmod, d)
            var prem2 = prem
            while (prem2 < pmod2) {
                if (prem2.remainder(d) == r) break
                prem2 += pmod
                if (prem2 >= pmod2) continue@vloop
                if (prem2 > p2r.toBigInteger()) continue@vloop
            }
            pmod = pmod2
            prem = prem2
            val p1n = modRoundUp(p1r, pmod, prem)
            val p2n = modRoundDn(p2r, pmod, prem)
            if (p1n > p2n) continue@vloop
            check(p1n >= p1r.toBigInteger())
            check(p2n <= p2r.toBigInteger())
            p1r = p1n.toLong()
            p2r = p2n.toLong()
        }
        rs += Range(p1r..p2r, v)
    }
    return rs.single().pi.first
}

private tailrec fun gcd(x: BigInteger, y: BigInteger): BigInteger = if (y == BigInteger.ZERO) x else gcd(y, x % y)
private fun lcm(x: BigInteger, y: BigInteger) = x * y / gcd(x, y)
private fun BigInteger.floorDiv(d: BigInteger): BigInteger =
    if (this >= BigInteger.ZERO) divide(d) else -(-this + d - BigInteger.ONE).divide(d)

private fun Long.floorDiv(d: BigInteger): BigInteger = toBigInteger().floorDiv(d)
private fun Long.mod(d: BigInteger): BigInteger = toBigInteger().mod(d)
private fun modRoundUp(x: Long, m: BigInteger, r: BigInteger): BigInteger =
    (x.floorDiv(m) + if (x.mod(m) <= r) BigInteger.ZERO else BigInteger.ONE) * m + r

private fun modRoundDn(x: Long, m: BigInteger, r: BigInteger): BigInteger =
    (x.floorDiv(m) - if (x.mod(m) >= r) BigInteger.ZERO else BigInteger.ONE) * m + r
