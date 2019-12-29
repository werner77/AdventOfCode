package com.behindmedia.adventofcode.year2018
import java.util.*

class Day13 {

    enum class CartState {
        left, straight, right;

        fun next(): CartState {
            return when(this) {
                left -> straight
                straight -> right
                right -> left
            }
        }
    }

    enum class Direction(val representation: Char) {
        up('^'), down('v'), left('<'), right('>');

        fun turnLeft(): Direction {
            return when(this) {
                up -> left
                down -> right
                left -> down
                right -> up
            }
        }

        fun turnRight(): Direction {
            return turnLeft().inverted()
        }

        fun inverted(): Direction {
            return when(this) {
                up -> down
                down -> up
                left -> right
                right -> left
            }
        }

        override fun toString(): String {
            return representation.toString()
        }

        companion object {

            private val valueMap: Map<Char, Direction>

            init {
                val m = mutableMapOf<Char, Direction>()
                for (value in values()) {
                    m[value.representation] = value
                }
                valueMap = m.toMap()
            }


            fun fromRepresentation(representation: Char): Direction? {
                return valueMap[representation]
            }
        }
    }

    enum class TrackSegment(val representation: Char) {
        none(' '),
        horizontal('-'),
        vertical('|'),
        rotatePositive('/'),
        rotateNegative('\\'),
        intersection('+');

        companion object {

            private val valueMap: Map<Char, TrackSegment>

            init {
                val m = mutableMapOf<Char, TrackSegment>()
                for (value in values()) {
                    m[value.representation] = value
                }
                valueMap = m.toMap()
            }


            fun fromRepresentation(representation: Char): TrackSegment? {
                return valueMap[representation]
            }
        }
    }

    class Cart(initialDirection: Direction) {
        var state: CartState = CartState.left
        var direction: Direction = initialDirection
        val initialTrackSegment = when(initialDirection) {
                Direction.right -> TrackSegment.horizontal
                Direction.left -> TrackSegment.horizontal
                Direction.up -> TrackSegment.vertical
                Direction.down -> TrackSegment.vertical
                }

        private fun intersection() {
            direction = when (state) {
                CartState.left -> direction.turnLeft()
                CartState.right -> direction.turnRight()
                CartState.straight -> direction
            }
            state = state.next()
        }

        private fun rotate(positive: Boolean) {
            val resultingDirection = when(direction) {
                Direction.up -> Direction.right
                Direction.down -> Direction.left
                Direction.left -> Direction.down
                Direction.right -> Direction.up
            }
            direction = if (positive) {
                resultingDirection
            } else {
                resultingDirection.inverted()
            }
        }

        fun apply(trackSegment: TrackSegment) {
            when (trackSegment) {
                TrackSegment.intersection -> intersection()
                TrackSegment.rotateNegative -> rotate(false)
                TrackSegment.rotatePositive -> rotate(true)
                TrackSegment.none -> throw IllegalStateException("Should always be on track!")
                else -> {
                    // Do nothing
                }
            }
        }

        override fun toString(): String {
            return direction.toString()
        }

        companion object {
            fun fromChar(c: Char): Cart? {
                val direction = Direction.fromRepresentation(c) ?: return null
                return Cart(direction)
            }
        }
    }

    data class Coordinate(val x: Int, val y: Int): Comparable<Coordinate> {
        override fun compareTo(other: Coordinate): Int {
            var result = y.compareTo(other.y)
            if (result == 0) {
                result = x.compareTo(other.x)
            }
            return result
        }

        fun offset(xOffset: Int, yOffset: Int): Coordinate {
            return Coordinate(x + xOffset, y + yOffset)
        }
    }

    class Track(val segments: Array<Array<TrackSegment>>, val sizeX: Int, val sizeY: Int, initialCartPositions: Map<Coordinate, Cart>) {
        private var cartPositions = initialCartPositions.toMutableMap()

        companion object {
            fun fromString(string: String): Track {
                val lines = string.split("\n")
                val maxX = lines.map { it.length }.max() ?: throw IllegalArgumentException("No input found")
                val maxY = lines.size
                val initialCartPositions = mutableMapOf<Coordinate, Cart>()

                val array = Array(maxX, {Array(maxY, { TrackSegment.none })})

                for ((y, line) in lines.withIndex()) {
                    val characters = line.toCharArray()
                    for (x in 0 until maxX) {
                        val c = if (x < characters.size) characters[x] else TrackSegment.none.representation

                        val cart = Cart.fromChar(c)

                        val trackSegment: TrackSegment
                        if (cart != null) {
                            trackSegment = cart.initialTrackSegment
                            initialCartPositions[Coordinate(x, y)] = cart
                        } else {
                            trackSegment = TrackSegment.fromRepresentation(c) ?: throw IllegalArgumentException("Invalid track segment: ${c}")
                        }
                        array[x][y] = trackSegment
                    }
                }
                return Track(array, maxX, maxY, initialCartPositions)
            }
        }

        fun trackSegment(at: Coordinate): TrackSegment {
            return segments[at.x][at.y]
        }

        fun moveCart(cart: Cart, from: Coordinate): Coordinate {
            val nextCoordinate = when(cart.direction) {
                Direction.down -> from.offset(0, 1)
                Direction.up -> from.offset(0, -1)
                Direction.left -> from.offset(-1, 0)
                Direction.right -> from.offset(1, 0)
            }
            val trackSegment = trackSegment(nextCoordinate)
            cart.apply(trackSegment)
            return nextCoordinate
        }

        fun doTick(failOnCrash: Boolean = true): Coordinate? {
            for (coordinate in cartPositions.keys.sorted()) {
                val cart = cartPositions[coordinate] ?: continue
                val newCoordinate = moveCart(cart, coordinate)
                val existingCart = cartPositions[newCoordinate]
                if (existingCart != null) {
                    //Collision!
                    if (failOnCrash) {
                        return newCoordinate
                    } else {
                        //Remove both carts
                        cartPositions.remove(coordinate)
                        cartPositions.remove(newCoordinate)
                    }
                } else {
                    cartPositions.remove(coordinate)
                    cartPositions[newCoordinate] = cart
                }
            }
            if (cartPositions.size == 1) {
                //Return coordinate of last remaining cart
                return cartPositions.keys.first()
            }
            return null
        }

        override fun toString(): String {
            val buffer = StringBuilder()
            for (y in 0 until sizeY) {
                for (x in 0 until sizeX) {
                    val cart = cartPositions[Coordinate(x, y)]
                    if (cart != null) {
                        buffer.append(cart.toString())
                    } else {
                        buffer.append(segments[x][y].representation)
                    }
                }
                buffer.append("\n")
            }
            return buffer.toString()
        }
    }

    fun process(input: String, failOnCrash: Boolean = true): Coordinate {
        val track = Track.fromString(input)
        while(true) {
            //println(track)
            val collisionCoordinate = track.doTick(failOnCrash)
            if (collisionCoordinate != null) {
                return collisionCoordinate
            }
        }
    }
}