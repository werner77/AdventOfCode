package com.behindmedia.adventofcode.year2019

class Day8 {

    fun checksum(width: Int, height: Int, input: List<Int>): Int {
        val numberOfPixelsPerLayer = width * height
        val layerCount = input.size / numberOfPixelsPerLayer
        assert(input.size % numberOfPixelsPerLayer == 0)

        val layers = List(layerCount) {
            mutableMapOf<Int, Int>()
        }

        for ((index, value) in input.withIndex()) {
            val layerIndex = index / numberOfPixelsPerLayer
            val layerMap = layers[layerIndex]
            layerMap[value] = (layerMap[value] ?: 0) + 1
        }

        val minimumLayer = layers.minByOrNull {
            it[0] ?: 0
        } ?: throw IllegalStateException("Found no minimum layer")

        return (minimumLayer[1] ?: 0) * (minimumLayer[2] ?: 0)
    }

    fun decodeMessage(width: Int, height: Int, input: List<Int>): String {
        val numberOfPixelsPerLayer = width * height
        assert(input.size % numberOfPixelsPerLayer == 0)

        // The visible layer, default to all transparent pixels
        val visibleLayer = MutableList(numberOfPixelsPerLayer) { 2 }
        for ((index, value) in input.withIndex()) {
            val pixelIndex = index % numberOfPixelsPerLayer
            if (visibleLayer[pixelIndex] == 2) {
                visibleLayer[pixelIndex] = value
            }
        }

        return visibleLayer.foldIndexed(StringBuilder()) { index, buffer, value ->
            if (index > 0 && index % width == 0) {
                buffer.append("\n")
            }
            buffer.append(if (value == 1) "*" else " ")
            buffer
        }.toString()
    }
}