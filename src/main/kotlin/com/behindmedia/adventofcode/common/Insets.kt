package com.behindmedia.adventofcode.common

data class Insets(val left: Int, val right: Int, val top: Int, val bottom: Int) {
    companion object {
        fun square(dimension: Int): Insets {
            return Insets(dimension, dimension, dimension, dimension)
        }

        fun rectangle(horizontal: Int, vertical: Int): Insets {
            return Insets(horizontal, horizontal, vertical, vertical)
        }
    }
}