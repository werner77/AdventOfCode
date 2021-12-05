package com.behindmedia.adventofcode.common

import java.util.*
import kotlin.math.*


/**
 * Describes the rotation direction (left or right)
 */
enum class RotationDirection {
    Left, Right;

    companion object {}
}

/**
 * Describes a three-dimensional coordinate or vector
 */
data class Coordinate3D(val x: Int, val y: Int, val z: Int) : Comparable<Coordinate3D> {

    constructor(components: List<Int>) : this(components[0], components[1], components[2])

    companion object {
        val origin = Coordinate3D(0, 0, 0)
    }

    fun offset(vector: Coordinate3D): Coordinate3D {
        return Coordinate3D(x + vector.x, y + vector.y, z + vector.z)
    }

    fun offset(xOffset: Int, yOffset: Int, zOffset: Int): Coordinate3D {
        return Coordinate3D(x + xOffset, y + yOffset, z + zOffset)
    }

    fun manhattenDistace(other: Coordinate3D): Int {
        return abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
    }

    operator fun plus(other: Coordinate3D): Coordinate3D {
        return offset(other)
    }

    operator fun unaryMinus(): Coordinate3D {
        return Coordinate3D(-x, -y, -z)
    }

    operator fun minus(other: Coordinate3D): Coordinate3D {
        return offset(-other)
    }

    operator fun get(index: Int): Int {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IllegalArgumentException("Invalid index supplied")
        }
    }

    override fun compareTo(other: Coordinate3D): Int {
        return compare(
            { this[0].compareTo(other[0]) },
            { this[1].compareTo(other[1]) },
            { this[2].compareTo(other[2]) })
    }
}

/**
 * Describes a two-dimensional coordinate or vector.
 */
@JvmInline
value class Coordinate private constructor(private val value: Long) : Comparable<Coordinate> {

    constructor(x: Int, y: Int) : this((y.toLong() shl 32) or (x.toLong() and MASK))

    val x: Int
        get() = (value and MASK).toInt()

    val y: Int
        get() = (value shr 32).toInt()

    companion object {
        private const val MASK = 0xFFFFFFFFL

        val origin = Coordinate(0, 0)
        val up = Coordinate(0, -1)
        val down = Coordinate(0, 1)
        val left = Coordinate(-1, 0)
        val right = Coordinate(1, 0)
        val upLeft = up + left
        val downLeft = down + left
        val upRight = up + right
        val downRight = down + right
        val directNeighbourDirections = listOf(up, left, right, down)
        val indirectNeighbourDirections = listOf(upLeft, downLeft, upRight, downRight)
        val allNeighbourDirections = directNeighbourDirections + indirectNeighbourDirections
    }

    /**
     * Comparison, ordering from top-left to bottom-right
     */
    override fun compareTo(other: Coordinate): Int {
        return compare(
            { this.y.compareTo(other.y) },
            { this.x.compareTo(other.x) }
        )
    }

    fun offset(xOffset: Int, yOffset: Int): Coordinate {
        return Coordinate(x + xOffset, y + yOffset)
    }

    fun offset(vector: Coordinate): Coordinate {
        return offset(vector.x, vector.y)
    }

    /**
     * Returns the diff with the supplied coordinate as a new coordinate (representing the vector)
     */
    fun vector(to: Coordinate): Coordinate {
        return Coordinate(to.x - this.x, to.y - this.y)
    }

    /**
     * Returns the Manhatten distance to the specified coordinate
     */
    fun manhattenDistance(to: Coordinate): Int {
        return abs(x - to.x) + abs(y - to.y)
    }

    /**
     * Returns the shortest Euclidean distance to the specified coordinate
     */
    fun distance(to: Coordinate): Double {
        val deltaX = (x - to.x).toDouble()
        val deltaY = (y - to.y).toDouble()
        return sqrt(deltaX * deltaX + deltaY * deltaY)
    }

    /**
     * The invers
     */
    fun inverted(): Coordinate {
        return Coordinate(-x, -y)
    }

    /**
     * Normalizes the coordinate by dividing both x and y by their greatest common divisor
     */
    fun normalized(): Coordinate {
        val factor = greatestCommonDivisor(abs(this.x.toLong()), abs(this.y.toLong())).toInt()
        return Coordinate(this.x / factor, this.y / factor)
    }

    /**
     * Rotates this coordinate (representing a vector) using the specified rotation direction
     */
    fun rotate(direction: RotationDirection): Coordinate {
        return when (direction) {
            RotationDirection.Left -> Coordinate(this.y, -this.x)
            RotationDirection.Right -> Coordinate(-this.y, this.x)
        }
    }

    /**
     * Optionally rotates this coordinate (representing a vector). Does nothing if the supplied direction is null.
     */
    fun optionalRotate(direction: RotationDirection?): Coordinate {
        return when (direction) {
            null -> this
            else -> rotate(direction)
        }
    }

    inline fun forDirectNeighbours(perform: (Coordinate) -> Unit) {
        for (i in 0 until 4) {
            val xOffset = i % 2
            val sign = if (i < 1 || i > 2) -1 else 1
            val yOffset = (i + 1) % 2
            perform(offset(sign * xOffset, sign * yOffset))
        }
    }

    /**
     * Returns the direct neighbours of this coordinate
     */
    val directNeighbours: List<Coordinate>
        get() {
            return List<Coordinate>(4) {
                val xOffset = it % 2
                val sign = if (it < 1 || it > 2) -1 else 1
                val yOffset = (it + 1) % 2
                offset(sign * xOffset, sign * yOffset)
            }
        }

    /**
     * Returns the indirect neighbours of this coordinate, defined as the diagonal neighbours
     */
    val indirectNeighbours: List<Coordinate>
        get() {
            return List<Coordinate>(4) {
                val xOffset = if (it == 0 || it == 1) 1 else -1
                val yOffset = if (it == 0 || it == 2) 1 else -1
                offset(xOffset, yOffset)
            }
        }

    val allNeighbours: List<Coordinate>
        get() = directNeighbours + indirectNeighbours

    /**
     * Returns the angle between 0 and 2 * PI relative to the specified vector
     */
    fun angle(to: Coordinate): Double {
        val a = to.x.toDouble()
        val b = to.y.toDouble()
        val c = this.x.toDouble()
        val d = this.y.toDouble()

        val atanA = atan2(a, b)
        val atanB = atan2(c, d)

        val angle = atanA - atanB

        return if (angle < 0) angle + 2 * PI else angle
    }

    /**
     * Breadth first search to find the shortest path to all reachable coordinates in a single sweep
     */
    inline fun <T> reachableCoordinates(reachable: (Coordinate) -> Boolean, process: (CoordinatePath) -> T?): T? {
        val list = ArrayDeque<CoordinatePath>()
        val visited = mutableSetOf<Coordinate>()
        list.add(CoordinatePath(this, 0))
        var start = true
        while (true) {
            val current = list.pollFirst() ?: return null
            if (start) {
                start = false
            } else {
                process(current)?.let {
                    return it
                }
            }
            visited.add(current.coordinate)
            current.coordinate.forDirectNeighbours { neighbour ->
                if (!visited.contains(neighbour) && reachable(neighbour)) {
                    list.add(CoordinatePath(neighbour, current.pathLength + 1))
                }
            }
        }
    }

    operator fun get(index: Int): Int {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IllegalArgumentException("Invalid index supplied")
        }
    }

    operator fun plus(other: Coordinate): Coordinate {
        return offset(other)
    }

    operator fun unaryMinus(): Coordinate {
        return inverted()
    }

    operator fun minus(other: Coordinate): Coordinate {
        return offset(other.inverted())
    }

    operator fun times(other: Coordinate): Coordinate {
        return Coordinate(this.x * other.x, this.y * other.y)
    }

    operator fun times(scalar: Int): Coordinate {
        return Coordinate(this.x * scalar, this.y * scalar)
    }

    operator fun rangeTo(other: Coordinate): CoordinateRange {
        return CoordinateRange(this, other)
    }

    operator fun component1(): Int = x
    operator fun component2(): Int = y

    fun copy(x: Int = this.x, y: Int = this.y): Coordinate = Coordinate(x, y)

    override fun toString(): String {
        return "($x,$y)"
    }
}

/**
 * Function to compare doubles with an allowed fractional difference
 */
fun Double.isAlmostEqual(other: Double, allowedDifference: Double = 0.000001): Boolean {
    return abs(this - other) < allowedDifference
}

/**
 * Returns the number of 10-based digits (excluding leading 0-s) of this Long
 */
val Long.numberOfDigits: Int
    get() {
        var value = this
        var digitCount = 0
        while (value != 0L) {
            value /= 10
            digitCount++
        }
        return digitCount
    }

/**
 * Computes the greatest common divisor of two integers.
 */
tailrec fun greatestCommonDivisor(a: Long, b: Long): Long {
    if (b == 0L) {
        return a
    }
    return greatestCommonDivisor(b, a % b)
}

fun leastCommonMultiple(a: Long, b: Long): Long {
    return if (a == 0L || b == 0L) 0 else {
        val gcd = greatestCommonDivisor(a, b)
        abs(a * b) / gcd
    }
}

inline fun Long.forBits(range: IntRange, perform: (Boolean) -> Unit) {
    for (i in range) {
        val mask = 1L shl i
        val bit = (this and mask) == mask
        perform.invoke(bit)
    }
}

fun Long.divideCeil(other: Long): Long {
    return (this + other - 1) / other
}

fun Long.times(other: Long, modulo: Long, ensurePositive: Boolean): Long {
    var a = this
    var b = other

    var result: Long = 0
    a %= modulo
    while (b > 0) { // If b is odd, add 'a' to result
        if (b % 2L == 1L) {
            result = (result + a) % modulo
        }
        // Multiply 'a' with 2
        a = a * 2 % modulo
        // Divide b by 2
        b /= 2
    }
    result %= modulo
    if (ensurePositive && result < 0) {
        result += modulo
    }
    return result
}

/**
 * Range to enumerate coordinates between the (minx, miny) and (maxx, maxy) found in a list of coordinates.
 */
class CoordinateRange(private val minMaxCoordinate: Pair<Coordinate, Coordinate>) : Iterable<Coordinate>,
    ClosedRange<Coordinate> {

    constructor(minCoordinate: Coordinate, width: Int, height: Int) : this(
        Pair(
            minCoordinate,
            minCoordinate + Coordinate(width - 1, height - 1)
        )
    )

    constructor(minCoordinate: Coordinate, maxCoordinate: Coordinate) : this(Pair(minCoordinate, maxCoordinate))
    constructor(collection: Collection<Coordinate>) : this(collection.minMaxCoordinate())

    companion object {
        private fun Collection<Coordinate>.minMaxCoordinate(): Pair<Coordinate, Coordinate> {
            var minX = Integer.MAX_VALUE
            var minY = Integer.MAX_VALUE
            var maxX = Integer.MIN_VALUE
            var maxY = Integer.MIN_VALUE
            for (coordinate in this) {
                minX = min(minX, coordinate.x)
                minY = min(minY, coordinate.y)
                maxX = max(maxX, coordinate.x)
                maxY = max(maxY, coordinate.y)
            }
            return Pair(Coordinate(minX, minY), Coordinate(maxX, maxY))
        }
    }

    private class CoordinateIterator(val minCoordinate: Coordinate, val maxCoordinate: Coordinate) :
        Iterator<Coordinate> {
        private var nextCoordinate: Coordinate? = if (minCoordinate <= maxCoordinate) minCoordinate else null

        override fun hasNext(): Boolean {
            return nextCoordinate != null
        }

        override fun next(): Coordinate {
            val next = nextCoordinate
                ?: throw IllegalStateException("Next called on iterator while there are no more elements to iterate over")
            nextCoordinate = when {
                next.x < maxCoordinate.x -> next.offset(1, 0)
                next.y < maxCoordinate.y -> Coordinate(minCoordinate.x, next.y + 1)
                else -> null
            }
            return next
        }
    }

    override fun iterator(): Iterator<Coordinate> = CoordinateIterator(minMaxCoordinate.first, minMaxCoordinate.second)

    override val endInclusive: Coordinate
        get() = minMaxCoordinate.second
    override val start: Coordinate
        get() = minMaxCoordinate.first
}

fun Collection<Coordinate>.range(): CoordinateRange = CoordinateRange(this)

fun <E> Map<Coordinate, E>.printMap(default: E) {
    val range = this.keys.range()
    for (c in range) {
        print(this[c] ?: default)
        if (c.x == range.endInclusive.x) {
            println()
        }
    }
}

class Path<N>(val destination: N, val pathLength: Int) : Comparable<Path<N>> {
    override fun compareTo(other: Path<N>): Int {
        return this.pathLength.compareTo(other.pathLength)
    }
}

class CoordinatePath(val coordinate: Coordinate, val pathLength: Int) : Comparable<CoordinatePath> {
    override fun compareTo(other: CoordinatePath): Int {
        return this.pathLength.compareTo(other.pathLength)
    }
}

/**
 * Breadth first search algorithm to find the shortest paths between unweighted nodes.
 */
inline fun <reified N, T> reachableNodes(
    from: N,
    neighbours: (N) -> Iterable<N>,
    reachable: (N) -> Boolean,
    process: (Path<N>) -> T?
): T? {
    val list = ArrayDeque<Path<N>>()
    val visited = mutableSetOf<N>()
    list.add(Path(from, 0))
    visited.add(from)
    while (true) {
        val current = list.pollFirst() ?: return null
        process(current)?.let {
            return it
        }
        for (neighbour in neighbours(current.destination)) {
            if (reachable(neighbour) && !visited.contains(neighbour)) {
                visited.add(neighbour)
                list.add(Path(neighbour, current.pathLength + 1))
            }
        }
    }
}

/**
 * Dijkstra's algorithm to find the shortest path between weighted nodes.
 */
inline fun <reified N, T> reachableNodes(
    from: N,
    neighbours: (N) -> Iterable<Pair<N, Int>>,
    process: (Path<N>) -> T?
): T? {
    val pending = PriorityQueue<Path<N>>()
    pending.add(Path(from, 0))
    val settled = mutableSetOf<N>()
    while (true) {
        val current = pending.poll() ?: break
        if (settled.contains(current.destination)) continue
        process(current)?.let {
            return it
        }
        val currentNode = current.destination
        settled.add(currentNode)
        for ((neighbour, neighbourWeight) in neighbours(currentNode)) {
            if (!settled.contains(neighbour)) {
                val newDistance = current.pathLength + neighbourWeight
                pending.add(Path(neighbour, newDistance))
            }
        }
    }
    return null
}

inline fun binarySearch(
    lowerBound: Long,
    upperBound: Long,
    targetValue: Long,
    inverted: Boolean = false,
    evaluation: (Long) -> Long
): Long? {
    var begin = lowerBound
    var end = upperBound
    var result: Long? = null
    while (begin <= end) {
        val mid = (begin + end) / 2L
        if (evaluation(mid) <= targetValue) {
            result = mid
            begin = if (inverted) mid - 1 else mid + 1
        } else {
            end = if (inverted) mid + 1 else mid - 1
        }
    }
    return result
}

fun optimumSearch(startValue: Long, inverted: Boolean = false, process: (Long) -> Long): Long {
    fun compareEval(first: Long, second: Long): Int {
        return if (inverted) second.compareTo(first) else first.compareTo(second)
    }

    var exceededOptimum = false
    var delta = 1
    var currentValue = startValue
    var bestEvaluation = process(currentValue)
    while (true) {
        if (exceededOptimum) {
            val leftEvaluation = process(currentValue - delta)
            val rightEvaluation = process(currentValue + delta)
            val comparisonResult = compareEval(leftEvaluation, rightEvaluation)
            val (currentEvaluation, sign) = if (comparisonResult > 0) {
                Pair(leftEvaluation, -1)
            } else {
                Pair(rightEvaluation, 1)
            }

            if (compareEval(currentEvaluation, bestEvaluation) > 0) {
                currentValue += sign * delta
                bestEvaluation = currentEvaluation
            } else if (delta == 1) {
                process(currentValue)
                return currentValue
            }
            delta = max(1, delta / 2)
        } else {
            // First increase delta by powers of two until the evaluation gets worse
            val currentEvaluation = process(currentValue + delta)
            val comparisonResult = compareEval(currentEvaluation, bestEvaluation)
            if (comparisonResult > 0) {
                currentValue += delta
                delta *= 2
                bestEvaluation = currentEvaluation
            } else {
                exceededOptimum = true
                delta = max(1, delta / 2)
            }
        }
    }
}