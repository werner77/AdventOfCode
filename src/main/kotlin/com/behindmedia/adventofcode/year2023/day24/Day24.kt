package com.behindmedia.adventofcode.year2023.day24

import com.behindmedia.adventofcode.common.Coordinate3D
import com.behindmedia.adventofcode.common.parseLines
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import java.math.BigInteger

fun main() {
    val stones = parseLines("/2023/day24.txt") { line ->
        val (first, second) = line.split("@")
        val (x, y, z) = first.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        val (vx, vy, vz) = second.split(",", " ").filter { it.isNotEmpty() }.map { it.toLong() }
        Stone(Coordinate3D(x, y, z), Coordinate3D(vx, vy, vz))
    }

    // Part 1, binary search
    val range = 200000000000000L.toDouble()..400000000000000L.toDouble()
    var ans = 0L
    for (i in 0 until stones.size) {
        for (j in i + 1 until stones.size) {
            if (findBox(range to range, stones[i], stones[j], 0.1) != null) {
                ans++
            }
        }
    }
    println(ans)

    // Part 2, credits to Roman Elizarov
    val x = checkTimes(stones.map { it.position.x }, stones.map { it.velocity.x })
    val y = checkTimes(stones.map { it.position.y }, stones.map { it.velocity.y })
    val z = checkTimes(stones.map { it.position.z }, stones.map { it.velocity.z })
    println(x + y + z)
}

private fun findBox(
    box: Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>>,
    first: Stone,
    second: Stone,
    precision: Double
): Pair<Double, Double>? {
    val (xRange, yRange) = box

    if (xRange.isEmpty() || yRange.isEmpty()) return null

    if (!isValidBox(box, first, second)) return null

    if (xRange.endInclusive - xRange.start < precision) {
        return ((xRange.start + xRange.endInclusive) / 2.0 to (yRange.start + yRange.endInclusive) / 2.0)
    }

    // Divide current box in 4 boxes
    val midX = (xRange.start + xRange.endInclusive) / 2
    val midY = (yRange.start + yRange.endInclusive) / 2

    val xRange1 = xRange.start..midX
    val xRange2 = midX..xRange.endInclusive
    val yRange1 = yRange.start..midY
    val yRange2 = midY..yRange.endInclusive

    for (xr in listOf(xRange1, xRange2)) {
        for (yr in listOf(yRange1, yRange2)) {
            return findBox(xr to yr, first, second, precision) ?: continue
        }
    }
    return null
}

/**
 * If both lines cross through the box it is valid
 */
private fun isValidBox(
    box: Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>>,
    first: Stone,
    second: Stone
): Boolean {
    val firstValid = isWithinInFuture(box, first.position, first.velocity)
    val secondValid = isWithinInFuture(box, second.position, second.velocity)
    return firstValid && secondValid
}

private fun isWithinInFuture(
    box: Pair<ClosedFloatingPointRange<Double>, ClosedFloatingPointRange<Double>>,
    pos: Coordinate3D,
    velocity: Coordinate3D
): Boolean {
    val xRangeWithin = timeRangeWithin(pos.x, velocity.x, box.first)
    val yRangeWithin = timeRangeWithin(pos.y, velocity.y, box.second)
    val intersectionRange = xRangeWithin.intersection(yRangeWithin)
    return !intersectionRange.isEmpty() && intersectionRange.endInclusive >= 0.0
}

private fun ClosedFloatingPointRange<Double>.intersection(other: ClosedFloatingPointRange<Double>): ClosedFloatingPointRange<Double> {
    return max(this.start, other.start)..min(this.endInclusive, other.endInclusive)
}

private fun timeRangeWithin(
    position: Long,
    velocity: Long,
    range: ClosedFloatingPointRange<Double>
): ClosedFloatingPointRange<Double> {
    val t1 = (range.start - position) / velocity.toDouble()
    val t2 = (range.endInclusive - position) / velocity.toDouble()
    return min(t1, t2)..max(t1, t2)
}

private data class Stone(val position: Coordinate3D, val velocity: Coordinate3D) {
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
