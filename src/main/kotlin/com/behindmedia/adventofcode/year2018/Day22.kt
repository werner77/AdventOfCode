package com.behindmedia.adventofcode.year2018

import com.behindmedia.adventofcode.common.Coordinate
import com.behindmedia.adventofcode.common.only
import com.behindmedia.adventofcode.common.popFirst

class Day22 {

    private enum class RegionType(val rawValue: Long, val compatibleGears: Set<Gear>) {
        Rocky(0, setOf(Gear.Torch, Gear.Climbing)),
        Wet(1, setOf(Gear.Climbing, Gear.Neither)),
        Narrow(2, setOf(Gear.Torch, Gear.Neither));

        companion object {
            fun from(erosionLevel: Long): RegionType {
                return values().first { it.rawValue == erosionLevel % 3 }
            }
        }
    }

    private enum class Gear {
        Torch, Climbing, Neither;
    }

    private data class CaveNode(val coordinate: Coordinate, val gear: Gear): Comparable<CaveNode> {
        override fun compareTo(other: CaveNode): Int {
            val result = coordinate.compareTo(other.coordinate)
            return if (result == 0) gear.compareTo(other.gear) else result
        }
    }

    private data class CaveNodePath(val node: CaveNode, val path: Int): Comparable<CaveNodePath> {
        override fun compareTo(other: CaveNodePath): Int {
            val result = path.compareTo(other.path)
            return if (result == 0) node.compareTo(other.node) else result
        }
    }

    /*
    The region at 0,0 (the mouth of the cave) has a geologic index of 0.
    The region at the coordinates of the target has a geologic index of 0.
    If the region's Y coordinate is 0, the geologic index is its X coordinate times 16807.
    If the region's X coordinate is 0, the geologic index is its Y coordinate times 48271.
    Otherwise, the region's geologic index is the result of multiplying the erosion levels of the regions at X-1,Y and X,Y-1.
     */
    private fun geologyIndex(erosionMap: Map<Coordinate, Long>, coordinate: Coordinate, target: Coordinate): Long {
        return if (coordinate == Coordinate.origin || coordinate == target) {
            0L
        } else if (coordinate.y == 0) {
            coordinate.x * 16807L
        } else if (coordinate.x == 0) {
            coordinate.y * 48271L
        } else {
            erosionMap.getValue(coordinate + Coordinate.up) * erosionMap.getValue(coordinate + Coordinate.left)
        }
    }

    private fun erosionLevel(geologyIndex: Long, depth: Long): Long {
        return (geologyIndex + depth) % 20183L
    }

    private fun erosionLevel(erosionMap: Map<Coordinate, Long>, coordinate: Coordinate, depth: Long, target: Coordinate): Long {
        val geologyIndex = geologyIndex(erosionMap, coordinate, target)
        return erosionLevel(geologyIndex, depth)
    }

    private fun getErosionMap(target: Coordinate, depth: Long, until: Coordinate = target): Map<Coordinate, Long> {
        val erosionMap = mutableMapOf<Coordinate, Long>()
        for (coordinate in Coordinate.origin..until) {
            erosionMap[coordinate] = erosionLevel(erosionMap, coordinate, depth, target)
        }
        return erosionMap
    }

    private fun compatibleGears(erosionLevel: Long): Set<Gear> {
        return RegionType.from(erosionLevel).compatibleGears
    }

    private fun shortestPath(
        map: Map<Coordinate, Long>,
        target: Coordinate
    ): Int {
        val pending = sortedSetOf<CaveNodePath>()
        val visited = mutableSetOf<CaveNode>()
        val candidates = mutableListOf<Int>()
        pending.add(CaveNodePath(CaveNode(Coordinate.origin, Gear.Torch), 0))

        while (pending.isNotEmpty()) {
            val current = pending.popFirst() ?: break
            if (current.node in visited) continue
            if (current.node.coordinate == target) {
                val finalCost = if (current.node.gear == Gear.Torch) 0 else 7
                candidates.add(current.path + finalCost)
            }

            for (neighbour in current.node.coordinate.directNeighbours) {
                val erosionLevel = map[neighbour] ?: continue
                val compatibleGears = compatibleGears(erosionLevel)
                val nextGear = if (current.node.gear in compatibleGears) {
                    current.node.gear
                } else {
                    val currentErosionLevel = map.getValue(current.node.coordinate)
                    (compatibleGears intersect compatibleGears(currentErosionLevel)).only()
                }
                val cost = if (nextGear == current.node.gear) 0 else 7
                pending.add(CaveNodePath(CaveNode(neighbour, nextGear), current.path + 1 + cost))
            }
            visited.add(current.node)
        }
        return candidates.min() ?: throw IllegalStateException("No valid path to target found")
    }

    fun determineRiskLevel(depth: Long, target: Coordinate): Int {
        val erosionMap = getErosionMap(target, depth)
        return erosionMap.values.sumBy { RegionType.from(it).rawValue.toInt() }
    }

    fun shortestPath(depth: Long, target: Coordinate): Int {
        val erosionMap = getErosionMap(target, depth, target * Coordinate(2, 2))
        return shortestPath(erosionMap, target)
    }

}