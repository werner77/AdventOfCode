package com.behindmedia.adventofcode.year2018

private var currentId = 0

private fun newId(): Int {
    return ++currentId
}

class Day6 {

    data class Point(val x: Int, val y: Int) {

        val id = newId()

        companion object {
            fun fromString(s: String): Point {
                val components = s.split(",", " ").filter { !it.isEmpty() }

                if (components.size != 2) {
                    throw IllegalArgumentException("Invalid input string: ${s}")
                }

                return Point(components[0].toInt(), components[1].toInt())
            }
        }

        fun distance(other: Point): Int {
            return Math.abs(x - other.x) + Math.abs(y - other.y)
        }

        fun isOnEdge(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
            return x == minX || x == maxX || y == minY || y == maxY
        }
    }

    private fun <U, V>MutableMap<U, V>.removeAll(predicate: (U, V) -> Boolean) {
        val iterator = this.iterator()
        while(iterator.hasNext()) {
            val entry = iterator.next()

            if (predicate(entry.key, entry.value)) {
                iterator.remove()
            }
        }
    }

    fun largestArea2(points: List<Point>, limit: Int): Int {
        // Find boundaries of area
        var minX = Integer.MAX_VALUE
        var minY = Integer.MAX_VALUE
        var maxX = Integer.MIN_VALUE
        var maxY = Integer.MIN_VALUE

        for (point in points) {
            minX = Math.min(point.x, minX)
            minY = Math.min(point.y, minY)
            maxX = Math.max(point.x, maxX)
            maxY = Math.max(point.y, maxY)
        }

        var totalRegion = 0

        for (x in minX..maxX) {

            val edgeX = (x == minX || x == maxY)

            for (y in minY..maxY) {

                val edgeY = y == minY || y == maxY
                val edge = edgeX || edgeY
                var sumDistance = 0
                for (point in points) {
                    val distance = point.distance(Point(x, y))
                    sumDistance += distance
                }

                if (sumDistance < limit) {

                    if (edge) {
                        println("edge point found")
                    }

                    totalRegion++
                }

            }
        }

        return totalRegion
    }


    fun largestArea1(points: List<Point>): Int {

        // Find boundaries of area
        var minX = Integer.MAX_VALUE
        var minY = Integer.MAX_VALUE
        var maxX = Integer.MIN_VALUE
        var maxY = Integer.MIN_VALUE

        for (point in points) {
            minX = Math.min(point.x, minX)
            minY = Math.min(point.y, minY)
            maxX = Math.max(point.x, maxX)
            maxY = Math.max(point.y, maxY)
        }

        val counter = mutableMapOf<Int, Int>()
        val blacklist = mutableSetOf<Int>()

        for (x in minX..maxX) {

            val edgeX = (x == minX || x == maxY)

            for (y in minY..maxY) {

                val edgeY = y == minY || y == maxY
                val edge = edgeX || edgeY

                var minPoint: Point? = null
                var minDistance: Int? = null
                var duplicate = false

                for (point in points) {
                    val distance = point.distance(Point(x, y))

                    if (minDistance == null || distance < minDistance) {
                        minDistance = distance
                        minPoint = point
                        duplicate = false
                    } else if (distance == minDistance) {
                        duplicate = true
                    }
                }

                if (!duplicate && minPoint != null) {
                    if (edge) {
                        blacklist.add(minPoint.id)
                    }
                    val newCount = (counter[minPoint.id] ?: 0) + 1
                    counter[minPoint.id] = newCount
                }
            }
        }

        counter.removeAll { key, _ ->
            blacklist.contains(key)
        }

        return counter.values.sorted().last()
    }

}