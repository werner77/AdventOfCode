package com.behindmedia.adventofcode2019

class Day11 {

    enum class Color(val rawValue: Long) {
        Black(0), White(1);

        companion object {
            fun from(rawValue: Long): Color {
                return values().find { it.rawValue == rawValue } ?: throw IllegalArgumentException("Unknown color value: $rawValue")
            }
        }
    }

    private fun RotationDirection.Companion.from(rawValue: Long): RotationDirection {
        return when(rawValue) {
            0L -> RotationDirection.Left
            1L -> RotationDirection.Right
            else -> throw IllegalArgumentException("Unknown rotation direction value: $rawValue")
        }
    }

    fun paintLicensePlate(encodedProgram: String, startColor: Color? = null): Map<Coordinate, Color> {
        val map = mutableMapOf<Coordinate, Color>()
        val computer = Computer(encodedProgram)

        var currentPosition = Coordinate.origin

        if (startColor != null) {
            map[currentPosition] = startColor
        }

        var currentDirection = Coordinate(0, -1)

        while (computer.status != Computer.Status.Finished) {
            val paintedColor = map[currentPosition]
            val currentColor = paintedColor ?: Color.Black

            val result = computer.process(listOf(currentColor.rawValue))
            assert(result.outputs.size == 2)

            val newPaintedColor = Color.from(result.outputs[0])
            val turnDirection = RotationDirection.from(result.outputs[1])

            map[currentPosition] = newPaintedColor

            currentDirection = currentDirection.rotate(turnDirection)
            currentPosition = currentPosition.offset(currentDirection)
        }
        return map
    }

}