package com.behindmedia.adventofcode.year2018

class Day3 {

    class IntArray2D(n: Int, m: Int) {
        val data = Array(n, {IntArray(m)})

        fun get(x: Int, y: Int): Int {
            val array = data[x]
            return array[y]
        }

        fun set(x: Int, y: Int, value: Int) {
            val array = data[x]
            array[y] = value
        }

        fun increment(x: Int, y: Int, increment: Int = 1): Int {
            val currentValue = get(x, y)
            val newValue = currentValue + increment
            set(x, y, newValue)
            return newValue
        }

        fun checkRange(x: Int, y: Int, width: Int, height: Int, predicate: (Int) -> Boolean) : Boolean {
            var ret = true

            forEach(x, y, width, height) { _, _, value ->
                ret = predicate(value)
                ret
            }

            return ret
        }

        fun forEach(x: Int, y: Int, width: Int, height: Int, block: (Int, Int, Int) -> Boolean) {
            for (i in x until x + width) {
                for (j in y until y + height) {
                    val value = get(i, j)
                    if (!block(i, j, value)) {
                        return
                    }
                }
            }
        }
    }

    fun IntArray2D.forEach(claim: Claim, block: (Int, Int, Int) -> Boolean) {
        return this.forEach(claim.minX, claim.minY, claim.width, claim.height, block)
    }

    fun IntArray2D.checkRange(claim: Claim, predicate: (Int) -> Boolean) : Boolean {
        return this.checkRange(claim.minX, claim.minY, claim.width, claim.height, predicate)
    }

    data class Claim(val id: Int, val minX: Int, val minY: Int, val width: Int, val height: Int) {
        companion object {
            fun fromString(s: String): Claim {
                val components = s.split("#", "@", ",", "x", ":", " ").filter { !it.isEmpty() }
                if (components.count() == 5) {
                    return Claim(components[0].toInt(), components[1].toInt(), components[2].toInt(), components[3].toInt(), components[4].toInt())
                } else {
                    throw IllegalArgumentException("Unparsable string: ${s}")
                }
            }
        }

        var maxX: Int = minX + width
        var maxY: Int = minY + height
    }

    fun calculateOverlap(claims: List<Claim>) : Int {

        var overlap = 0
        var maxX = 0
        var maxY = 0
        for (claim in claims) {
            maxX = Math.max(maxX, claim.maxX)
            maxY = Math.max(maxY, claim.maxY)
        }

        val data = IntArray2D(maxX, maxY)
        for (claim in claims) {
            data.forEach(claim) { i, j, _ ->
                val value = data.increment(i, j)
                if (value == 2) {
                    overlap++
                }
                true
            }
        }

        return overlap
    }

    fun getNonOverlappingClaim(claims: List<Claim>) : Claim? {

        var maxX = 0
        var maxY = 0
        for (claim in claims) {
            maxX = Math.max(maxX, claim.maxX)
            maxY = Math.max(maxY, claim.maxY)
        }

        val data = IntArray2D(maxX, maxY)
        for (claim in claims) {
            data.forEach(claim) { i, j, _ ->
                data.increment(i, j)
                true
            }
        }

        for (claim in claims) {
            if (data.checkRange(claim) {
                    it == 1
                }) {
                return claim
            }
        }

        return null
    }
}