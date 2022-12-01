package com.behindmedia.adventofcode.year2018old

import com.behindmedia.adventofcode.common.Coordinate

class Day18 {

    enum class AcreType(val rawValue: Char) {
        empty('.'), trees('|'), lumberyard('#');

        companion object {
            fun fromRawValue(rawValue: Char): AcreType? {
                return values().find { it.rawValue == rawValue }
            }
        }
    }

    data class Area(var initialState: Map<Coordinate, AcreType>, val sizeX: Int, val sizeY: Int) {

        var state: Map<Coordinate, AcreType> = initialState

        fun Coordinate.adjacent(eval: (Coordinate) -> Boolean): Unit {
            for (i in -1..1) {
                for (j in -1..1) {
                    if (i == 0 && j == 0) continue
                    val x1 = x + i
                    val y1 = y + j
                    if (x1 < 0 || y1 < 0 || x1 >= sizeX || y1 >= sizeY) continue
                    if (!eval(Coordinate(x1, y1))) return
                }
            }
        }

        fun Map<Coordinate, AcreType>.returnValue(): Int {
            return values.filter { it == AcreType.trees }.size * values.filter { it == AcreType.lumberyard }.size
        }

        fun reset() {
            state = initialState
        }

        fun newAcreType(coordinate: Coordinate): AcreType {
            val acreType = state[coordinate] ?: throw IllegalArgumentException("Illegal coordinate")
            var newAcreType = acreType
            var trees = 0
            var lumberyard = 0
            coordinate.adjacent {
                val adjacentAcreType = state[it]
                if (adjacentAcreType == AcreType.lumberyard) {
                    lumberyard++
                } else if (adjacentAcreType == AcreType.trees) {
                    trees++
                }

                var ret = true
                if (acreType == AcreType.empty) {
                    if (trees >= 3) {
                        newAcreType = AcreType.trees
                        ret = false
                    }
                } else if (acreType == AcreType.trees) {
                    if (lumberyard >= 3) {
                        newAcreType = AcreType.lumberyard
                        ret = false
                    }
                } else if (acreType == AcreType.lumberyard) {
                    if (lumberyard == 0 || trees == 0) {
                        newAcreType = AcreType.empty
                    } else if (lumberyard >= 1 && trees >= 1) {
                        newAcreType = AcreType.lumberyard
                        ret = false
                    }
                }
                ret
            }
            return newAcreType
        }

        fun process(numberOfTurns: Int, optimize: Boolean = false): Int {
            val historicStates = mutableListOf<Map<Coordinate, AcreType>>()
            historicStates.add(initialState)

            for (i in 0 until numberOfTurns) {
                val newState = mutableMapOf<Coordinate, AcreType>()
                for (coordinate in state.keys) {
                    val newAcreType = newAcreType(coordinate)
                    newState[coordinate] = newAcreType
                }
                state = newState

                if (optimize) {
                    for ((j, historicState) in historicStates.withIndex()) {
                        if (historicState == state) {
                            println("Historic state ${j} matches state ${i + 1}")
                            //subtract the initial iterations needed from numberOfTurns

                            val period = (i + 1 - j)
                            val iterationsAfterInitialRepetitionState = numberOfTurns - j

                            val totalNumberOfIterations = iterationsAfterInitialRepetitionState % period + j

                            val effectiveState = historicStates[totalNumberOfIterations]

                            return effectiveState.returnValue()
                        }
                    }
                }

                historicStates.add(state)
            }
            return state.returnValue()
        }

        override fun toString(): String {
            val buffer = StringBuilder()
            var y = 0
            for (coordinate in state.keys.sorted()) {
                if (coordinate.y != y) {
                    buffer.append("\n")
                }
                val acreType = state[coordinate] ?: throw IllegalStateException("Should not happen")
                buffer.append(acreType.rawValue)
                y = coordinate.y
            }
            buffer.append("\n")
            return buffer.toString()
        }

        companion object {
            fun fromString(input: String): Area {
                var y = 0
                var sizeX = 0
                var sizeY = 0
                val map = mutableMapOf<Coordinate, AcreType>()
                for (line in input.split("\n")) {
                    var x = 0
                    for (c in line.toCharArray()) {
                        val acreType = AcreType.fromRawValue(c) ?: throw IllegalArgumentException("Illegal character found: ${c}")
                        map[Coordinate(x, y)] = acreType
                        x++
                        sizeX = Math.max(sizeX, x)
                    }
                    y++
                    sizeY = Math.max(sizeY, y)
                }

                return Area(map, sizeX, sizeY)
            }
        }
    }

}