package com.behindmedia.adventofcode2019

class Day11 {

    /**
     * Enum describing the possible colors with rawValues 0 for white and 1 for black
     */
    enum class Color(val rawValue: Long) {
        Black(0), White(1);

        companion object {
            fun from(rawValue: Long): Color {
                return values().find { it.rawValue == rawValue } ?: throw IllegalArgumentException("Unknown color value: $rawValue")
            }
        }
    }

    /**
     * A private extension to construct a RotationDirection from the rawValue: 0=left, 1=right
     */
    private fun RotationDirection.Companion.from(rawValue: Long): RotationDirection {
        return when(rawValue) {
            0L -> RotationDirection.Left
            1L -> RotationDirection.Right
            else -> throw IllegalArgumentException("Unknown rotation direction value: $rawValue")
        }
    }

    /**
     * Function to paint the license plate. Uses the Computer class with the specified encodedProgram.
     *
     * The startColor is optional (relevant for the second puzzle)
     */
    fun paintLicensePlate(encodedProgram: String, startColor: Color? = null): Map<Coordinate, Color> {
        // This describes the locations with painted colors.
        // Only the actual painted locations are present in the map
        val map = mutableMapOf<Coordinate, Color>()
        val computer = Computer(encodedProgram)

        // Start at origin
        var currentPosition = Coordinate.origin

        if (startColor != null) {
            map[currentPosition] = startColor
        }

        // Initial direction is up
        var currentDirection = Coordinate(0, -1)

        // Loop until the computer program has finished
        while (computer.status != Computer.Status.Finished) {
            // Get the current color or default to black if non is present
            val currentColor = map[currentPosition] ?: Color.Black

            // Feed the color as input to the computer
            val result = computer.process(currentColor.rawValue)

            // There should always be exactly two outputs
            assert(result.outputs.size == 2)

            // First output is the color
            val paintedColor = Color.from(result.outputs[0])

            // Second is the turn direction
            val turnDirection = RotationDirection.from(result.outputs[1])

            // Update the map with the painted color
            map[currentPosition] = paintedColor

            // Determine the new direction and position
            currentDirection = currentDirection.rotate(turnDirection)
            currentPosition = currentPosition.offset(currentDirection)
        }
        return map
    }

}