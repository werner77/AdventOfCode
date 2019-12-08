package com.behindmedia.adventofcode2019

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

        val minimumLayer = layers.minBy {
            it[0] ?: 0
        } ?: throw IllegalStateException("Found no minimum layer")

        return (minimumLayer[1] ?: 0) * (minimumLayer[2] ?: 0)
    }

    fun decodeMessage(width: Int, height: Int, input: List<Int>): String {
        val numberOfPixelsPerLayer = width * height
        assert(input.size % numberOfPixelsPerLayer == 0)

        val visibleLayer = MutableList(numberOfPixelsPerLayer) { 2 }
        for ((index, value) in input.withIndex()) {
            val pixelIndex = index % numberOfPixelsPerLayer
            if (visibleLayer[pixelIndex] == 2) {
                visibleLayer[pixelIndex] = value
            }
        }

        val result = StringBuilder()
        var index = 0
        for(j in 0 until height) {
            var line = ""
            for (i in 0 until width) {
                line += visibleLayer[index]
                index++
            }
            result.append(line).append("\n")
        }
        return result.toString()
    }
}