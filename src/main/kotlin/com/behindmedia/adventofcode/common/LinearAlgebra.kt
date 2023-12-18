package com.behindmedia.adventofcode.common

typealias Matrix = Array<IntArray>

fun affineTranslationOf(x: Int, y: Int, z: Int = 0, inverse: Boolean = false): Matrix {
    val m = if (inverse) -1 else 1
    return affineTransformOf(
        1, 0, 0, m * x,
        0, 1, 0, m * y,
        0, 0, 1, m * z,
        0, 0, 0, 1
    )
}

fun affineIdentity(): Matrix {
    return affineTransformOf(
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    )
}

/**
 * Amount means the number of 90 degrees rotations that have to be made
 */
fun affineRotationOf(amount: Int, axis: Int, inverse: Boolean = false): Matrix {
    if (amount == 0) return affineIdentity()
    return when (axis) {
        0 -> {
            // Rotate around x-axis
            affineTransformOf(
                1, 0, 0, 0,
                0, intCos(amount), -intSin(amount), 0,
                0, intSin(amount), intCos(amount), 0,
                0, 0, 0, 1
            )
        }
        1 -> {
            // Rotate around y-axis
            affineTransformOf(
                intCos(amount), 0, intSin(amount), 0,
                0, 1, 0, 0,
                -intSin(amount), 0, intCos(amount), 0,
                0, 0, 0, 1
            )
        }
        else -> if (axis == 2) {
            // Rotate around z-axis
            affineTransformOf(
                intCos(amount), -intSin(amount), 0, 0,
                intSin(amount), intCos(amount), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
            )
        } else {
            error("Invalid axis: $axis")
        }
    }.also {
        if (inverse) {
            it.transpose()
        }
    }
}

private fun intSin(angle: Int): Int {
    var effectiveAngle = angle % 4
    if (effectiveAngle < 0) effectiveAngle += 4
    return when(angle) {
        0 -> 0
        1 -> 1
        2 -> 0
        3 -> -1
        else -> error("Should not reach this line")
    }
}

private fun intCos(angle: Int): Int {
    var effectiveAngle = angle % 4
    if (effectiveAngle < 0) effectiveAngle += 4
    return when(angle) {
        0 -> 1
        1 -> 0
        2 -> -1
        3 -> 0
        else -> error("Should not reach this line")
    }
}

fun affineTransformOf(vararg elements: Int): Matrix {
    return matrixOf(
        4, 4,
        *elements
    )
}

fun Matrix.transposed(): Matrix {
    val cols = this.size
    val rows = this[0].size
    if (cols != rows) error("Column and row size should match")
    return Array(rows) { row ->
        IntArray(cols) { col ->
            this[col][row]
        }
    }
}

fun Matrix.transpose() {
    val cols = this.size
    val rows = this[0].size
    if (cols != rows) error("Column and row size should match")
    for (r in 0 until rows) {
        for (c in 0 until cols) {
            this[r][c] = this[c][r]
        }
    }
}

fun matrixOf(rows: Int, cols: Int, vararg values: Int): Matrix {
    return Array(rows) { row ->
        IntArray(cols) { column ->
            val index = row * cols + column
            values[index]
        }
    }
}

operator fun Matrix.times(other: Matrix): Matrix {
    return multiply(this, other)
}

fun Coordinate3D.toMatrix(): Matrix {
    return matrixOf(4, 1, this.x.toInt(), this.y.toInt(), this.z.toInt(), 1)
}

fun Matrix.toCoordinate3D(): Coordinate3D {
    if (this.size != 4 || this[0].size != 1) error("Invalid matrix size")
    return Coordinate3D(this[0][0].toLong(), this[1][0].toLong(), this[2][0].toLong())
}

private fun multiply(matrix1: Matrix, matrix2: Matrix): Matrix {
    val r1 = matrix1.size
    val c1 = matrix1[0].size
    val r2 = matrix2.size
    val c2 = matrix2[0].size
    if (c1 != r2) error("Incompatible matrices: row size of first needs to match column size of second")

    return Array(r1) { row ->
        IntArray(c2) { column ->
            (0 until c1).sumOf {
                matrix1[row][it] * matrix2[it][column]
            }
        }
    }
}
